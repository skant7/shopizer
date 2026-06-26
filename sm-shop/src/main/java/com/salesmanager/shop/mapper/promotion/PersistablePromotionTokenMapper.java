package com.salesmanager.shop.mapper.promotion;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Component;

import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.promotion.PromotionToken;
import com.salesmanager.core.model.promotion.TokenDiscountType;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.mapper.Mapper;
import com.salesmanager.shop.model.promotion.PersistablePromotionToken;
import com.salesmanager.shop.store.api.exception.ServiceRuntimeException;

@Component
public class PersistablePromotionTokenMapper implements Mapper<PersistablePromotionToken, PromotionToken> {

	@Override
	public PromotionToken convert(PersistablePromotionToken source, MerchantStore store, Language language) {
		Validate.notNull(source, "PersistablePromotionToken cannot be null");
		Validate.notNull(store, "MerchantStore cannot be null");
		PromotionToken token = new PromotionToken();
		token.setMerchantStore(store);
		return merge(source, token, store, language);
	}

	@Override
	public PromotionToken merge(PersistablePromotionToken source, PromotionToken destination, MerchantStore store,
			Language language) {
		Validate.notNull(source, "PersistablePromotionToken cannot be null");
		Validate.notNull(destination, "PromotionToken cannot be null");
		Validate.notNull(store, "MerchantStore cannot be null");

		if (source.getId() != null && source.getId() > 0) {
			destination.setId(source.getId());
		}
		destination.setCode(source.getCode() != null ? source.getCode().trim() : null);
		destination.setDescription(source.getDescription());
		destination.setDiscountValue(source.getDiscountValue());
		destination.setStartDate(source.getStartDate());
		destination.setEndDate(source.getEndDate());
		destination.setActive(source.isActive());
		destination.setMaxUses(source.getMaxUses());
		destination.setMinCartAmount(source.getMinCartAmount());
		destination.setMerchantStore(store);

		if (StringUtils.isBlank(source.getDiscountType())) {
			throw new ServiceRuntimeException("discountType is required (PERCENTAGE or FIXED)");
		}
		try {
			destination.setDiscountType(TokenDiscountType.valueOf(source.getDiscountType().trim().toUpperCase()));
		} catch (IllegalArgumentException e) {
			throw new ServiceRuntimeException("Invalid discountType [" + source.getDiscountType()
					+ "]. Allowed values: PERCENTAGE, FIXED");
		}
		return destination;
	}
}
