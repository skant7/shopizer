package com.salesmanager.core.business.modules.order.total;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.salesmanager.core.business.configuration.DroolsBeanFactory;
import com.salesmanager.core.business.constants.Constants;
import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.services.catalog.pricing.PricingService;
import com.salesmanager.core.business.services.promotion.PromotionTokenService;
import com.salesmanager.core.model.catalog.product.Product;
import com.salesmanager.core.model.catalog.product.price.FinalPrice;
import com.salesmanager.core.model.customer.Customer;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.order.OrderSummary;
import com.salesmanager.core.model.order.OrderTotal;
import com.salesmanager.core.model.order.OrderTotalType;
import com.salesmanager.core.model.promotion.PromotionToken;
import com.salesmanager.core.model.promotion.TokenDiscountType;
import com.salesmanager.core.model.shoppingcart.ShoppingCartItem;
import com.salesmanager.core.modules.order.total.OrderTotalPostProcessorModule;

@Component
public class PromoCodeCalculatorModule implements OrderTotalPostProcessorModule {

	@Autowired
	private DroolsBeanFactory droolsBeanFactory;

	@Autowired
	private PricingService pricingService;

	@Autowired
	private PromotionTokenService promotionTokenService;

	private String name;
	private String code;

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getCode() {
		return code;
	}

	@Override
	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public OrderTotal caculateProductPiceVariation(OrderSummary summary, ShoppingCartItem shoppingCartItem,
			Product product, Customer customer, MerchantStore store) throws Exception {

		Validate.notNull(summary, "OrderTotalSummary must not be null");
		Validate.notNull(store, "MerchantStore must not be null");

		if (StringUtils.isBlank(summary.getPromoCode())) {
			return null;
		}

		// Prefer admin-managed promotion tokens from the database
		PromotionToken token = null;
		try {
			token = promotionTokenService.getByCode(summary.getPromoCode(), store);
		} catch (ServiceException e) {
			token = null;
		}

		if (token != null) {
			return calculateFromToken(token, summary, shoppingCartItem, product);
		}

		// Legacy Drools rules for ad-hoc promo codes not stored as tokens
		return calculateFromDrools(summary, shoppingCartItem, product);
	}

	private OrderTotal calculateFromToken(PromotionToken token, OrderSummary summary, ShoppingCartItem shoppingCartItem,
			Product product) throws Exception {
		if (!token.isActive()) {
			return null;
		}
		Date now = new Date();
		if (token.getStartDate() != null && now.before(token.getStartDate())) {
			return null;
		}
		if (token.getEndDate() != null && now.after(token.getEndDate())) {
			return null;
		}
		if (token.getMaxUses() != null && token.getMaxUses() > 0 && token.getUsesCount() >= token.getMaxUses()) {
			return null;
		}

		FinalPrice productPrice = pricingService.calculateProductPrice(product);
		BigDecimal lineTotal = productPrice.getFinalPrice()
				.multiply(BigDecimal.valueOf(shoppingCartItem.getQuantity()));

		BigDecimal reduction;
		if (token.getDiscountType() == TokenDiscountType.PERCENTAGE) {
			// discountValue is 0-100
			reduction = lineTotal.multiply(token.getDiscountValue()).divide(new BigDecimal("100"), 4,
					RoundingMode.HALF_UP);
		} else {
			// FIXED: apply the full fixed amount once on the first line item only
			if (!isFirstLineItem(summary, shoppingCartItem)) {
				return null;
			}
			reduction = token.getDiscountValue();
			// Cap at cart subtotal best-effort (this line at minimum)
			if (reduction.compareTo(lineTotal) > 0) {
				reduction = lineTotal;
			}
		}

		if (reduction.compareTo(BigDecimal.ZERO) <= 0) {
			return null;
		}

		OrderTotal orderTotal = new OrderTotal();
		orderTotal.setOrderTotalCode(Constants.OT_DISCOUNT_TITLE);
		orderTotal.setOrderTotalType(OrderTotalType.SUBTOTAL);
		orderTotal.setTitle(Constants.OT_SUBTOTAL_MODULE_CODE);
		orderTotal.setText(summary.getPromoCode());
		orderTotal.setValue(reduction);
		return orderTotal;
	}

	private boolean isFirstLineItem(OrderSummary summary, ShoppingCartItem shoppingCartItem) {
		List<ShoppingCartItem> products = summary.getProducts();
		if (products == null || products.isEmpty() || shoppingCartItem == null) {
			return true;
		}
		ShoppingCartItem first = products.get(0);
		if (first == null) {
			return true;
		}
		if (first.getId() != null && shoppingCartItem.getId() != null) {
			return first.getId().equals(shoppingCartItem.getId());
		}
		return first == shoppingCartItem;
	}

	private OrderTotal calculateFromDrools(OrderSummary summary, ShoppingCartItem shoppingCartItem, Product product)
			throws Exception {
		KieSession kieSession = droolsBeanFactory
				.getKieSession(ResourceFactory.newClassPathResource("com/salesmanager/drools/rules/PromoCoupon.drl"));

		OrderTotalResponse resp = new OrderTotalResponse();

		OrderTotalInputParameters inputParameters = new OrderTotalInputParameters();
		inputParameters.setPromoCode(summary.getPromoCode());
		inputParameters.setDate(new Date());

		kieSession.insert(inputParameters);
		kieSession.setGlobal("total", resp);
		kieSession.fireAllRules();

		if (resp.getDiscount() != null) {
			OrderTotal orderTotal = new OrderTotal();
			orderTotal.setOrderTotalCode(Constants.OT_DISCOUNT_TITLE);
			orderTotal.setOrderTotalType(OrderTotalType.SUBTOTAL);
			orderTotal.setTitle(Constants.OT_SUBTOTAL_MODULE_CODE);
			orderTotal.setText(summary.getPromoCode());

			FinalPrice productPrice = pricingService.calculateProductPrice(product);

			Double discount = resp.getDiscount();
			BigDecimal reduction = productPrice.getFinalPrice().multiply(new BigDecimal(discount));
			reduction = reduction.multiply(new BigDecimal(shoppingCartItem.getQuantity()));

			orderTotal.setValue(reduction);
			return orderTotal;
		}

		return null;
	}

}
