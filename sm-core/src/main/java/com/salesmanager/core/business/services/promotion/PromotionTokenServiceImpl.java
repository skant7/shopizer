package com.salesmanager.core.business.services.promotion;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Service;

import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.repositories.promotion.PromotionTokenRepository;
import com.salesmanager.core.business.services.common.generic.SalesManagerEntityServiceImpl;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.promotion.PromotionToken;
import com.salesmanager.core.model.promotion.TokenDiscountType;

@Service("promotionTokenService")
public class PromotionTokenServiceImpl extends SalesManagerEntityServiceImpl<Long, PromotionToken>
		implements PromotionTokenService {

	private final PromotionTokenRepository promotionTokenRepository;

	@Inject
	public PromotionTokenServiceImpl(PromotionTokenRepository promotionTokenRepository) {
		super(promotionTokenRepository);
		this.promotionTokenRepository = promotionTokenRepository;
	}

	@Override
	public List<PromotionToken> listByStore(MerchantStore store) throws ServiceException {
		Validate.notNull(store, "MerchantStore cannot be null");
		return promotionTokenRepository.findByStore(store.getId());
	}

	@Override
	public PromotionToken getByCode(String code, MerchantStore store) throws ServiceException {
		Validate.notNull(store, "MerchantStore cannot be null");
		if (StringUtils.isBlank(code)) {
			return null;
		}
		return promotionTokenRepository.findByStoreAndCode(store.getId(), code.trim());
	}

	@Override
	public PromotionToken getById(Long id) {
		if (id == null) {
			return null;
		}
		return promotionTokenRepository.findOne(id);
	}

	@Override
	public boolean exists(String code, MerchantStore store) throws ServiceException {
		Validate.notNull(store, "MerchantStore cannot be null");
		if (StringUtils.isBlank(code)) {
			return false;
		}
		return promotionTokenRepository.findByStoreAndCode(store.getId(), code.trim()) != null;
	}

	@Override
	public PromotionToken saveOrUpdate(PromotionToken token) throws ServiceException {
		Validate.notNull(token, "PromotionToken cannot be null");
		validateTokenDefinition(token);
		if (token.getId() != null && token.getId() > 0) {
			this.update(token);
		} else {
			token = super.saveAndFlush(token);
		}
		return token;
	}

	@Override
	public void delete(PromotionToken token) throws ServiceException {
		PromotionToken existing = getById(token.getId());
		if (existing != null) {
			super.delete(existing);
		}
	}

	@Override
	public PromotionToken validateForCart(String code, MerchantStore store, BigDecimal cartSubtotal, Date asOf)
			throws ServiceException {
		Validate.notNull(store, "MerchantStore cannot be null");
		if (StringUtils.isBlank(code)) {
			throw new ServiceException(ServiceException.EXCEPTION_VALIDATION, "Promotion token code is required");
		}
		Date reference = asOf != null ? asOf : new Date();
		PromotionToken token = getByCode(code.trim(), store);
		if (token == null) {
			throw new ServiceException(ServiceException.EXCEPTION_VALIDATION,
					"Invalid promotion token [" + code + "]");
		}
		if (!token.isActive()) {
			throw new ServiceException(ServiceException.EXCEPTION_VALIDATION,
					"Promotion token [" + code + "] is not active");
		}
		if (token.getStartDate() != null && reference.before(token.getStartDate())) {
			throw new ServiceException(ServiceException.EXCEPTION_VALIDATION,
					"Promotion token [" + code + "] is not yet valid");
		}
		if (token.getEndDate() != null && reference.after(token.getEndDate())) {
			throw new ServiceException(ServiceException.EXCEPTION_VALIDATION,
					"Promotion token [" + code + "] has expired");
		}
		if (token.getMaxUses() != null && token.getMaxUses() > 0 && token.getUsesCount() >= token.getMaxUses()) {
			throw new ServiceException(ServiceException.EXCEPTION_VALIDATION,
					"Promotion token [" + code + "] has reached its maximum number of uses");
		}
		if (token.getMinCartAmount() != null && cartSubtotal != null
				&& cartSubtotal.compareTo(token.getMinCartAmount()) < 0) {
			throw new ServiceException(ServiceException.EXCEPTION_VALIDATION,
					"Cart total does not meet the minimum amount required for promotion token [" + code + "]");
		}
		return token;
	}

	@Override
	public void incrementUseCount(PromotionToken token) throws ServiceException {
		Validate.notNull(token, "PromotionToken cannot be null");
		PromotionToken managed = getById(token.getId());
		if (managed == null) {
			return;
		}
		managed.setUsesCount(managed.getUsesCount() + 1);
		update(managed);
	}

	private void validateTokenDefinition(PromotionToken token) throws ServiceException {
		if (StringUtils.isBlank(token.getCode())) {
			throw new ServiceException(ServiceException.EXCEPTION_VALIDATION, "Promotion token code is required");
		}
		token.setCode(token.getCode().trim());
		if (token.getCode().length() > 32) {
			throw new ServiceException(ServiceException.EXCEPTION_VALIDATION,
					"Promotion token code must be at most 32 characters");
		}
		if (token.getDiscountType() == null) {
			throw new ServiceException(ServiceException.EXCEPTION_VALIDATION,
					"Promotion token discount type is required");
		}
		if (token.getDiscountValue() == null || token.getDiscountValue().compareTo(BigDecimal.ZERO) <= 0) {
			throw new ServiceException(ServiceException.EXCEPTION_VALIDATION,
					"Promotion token discount value must be greater than zero");
		}
		if (token.getDiscountType() == TokenDiscountType.PERCENTAGE
				&& token.getDiscountValue().compareTo(new BigDecimal("100")) > 0) {
			throw new ServiceException(ServiceException.EXCEPTION_VALIDATION,
					"Percentage discount cannot exceed 100");
		}
		if (token.getStartDate() != null && token.getEndDate() != null
				&& token.getEndDate().before(token.getStartDate())) {
			throw new ServiceException(ServiceException.EXCEPTION_VALIDATION,
					"Promotion token end date must be on or after start date");
		}
		if (token.getMerchantStore() == null) {
			throw new ServiceException(ServiceException.EXCEPTION_VALIDATION,
					"Promotion token must belong to a merchant store");
		}
	}
}
