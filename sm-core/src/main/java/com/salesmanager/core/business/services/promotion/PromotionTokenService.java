package com.salesmanager.core.business.services.promotion;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.services.common.generic.SalesManagerEntityService;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.promotion.PromotionToken;

public interface PromotionTokenService extends SalesManagerEntityService<Long, PromotionToken> {

	List<PromotionToken> listByStore(MerchantStore store) throws ServiceException;

	PromotionToken getByCode(String code, MerchantStore store) throws ServiceException;

	boolean exists(String code, MerchantStore store) throws ServiceException;

	PromotionToken saveOrUpdate(PromotionToken token) throws ServiceException;

	/**
	 * Validates that the token may be applied to a cart for the given store and optional subtotal.
	 * Throws {@link ServiceException} with a clear message when validation fails.
	 *
	 * @param code token code
	 * @param store merchant store
	 * @param cartSubtotal optional cart subtotal for min-amount checks; may be null to skip that check
	 * @param asOf reference date for validity window (typically now)
	 * @return the loaded valid token
	 */
	PromotionToken validateForCart(String code, MerchantStore store, BigDecimal cartSubtotal, Date asOf)
			throws ServiceException;

	/**
	 * Increments use count after successful order placement (best-effort tracking).
	 */
	void incrementUseCount(PromotionToken token) throws ServiceException;
}
