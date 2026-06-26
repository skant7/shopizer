package com.salesmanager.shop.store.controller.promotion.facade;

import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.model.entity.Entity;
import com.salesmanager.shop.model.entity.ReadableEntityList;
import com.salesmanager.shop.model.promotion.PersistablePromotionToken;
import com.salesmanager.shop.model.promotion.ReadablePromotionToken;

public interface PromotionTokenFacade {

	Entity create(PersistablePromotionToken token, MerchantStore store, Language language);

	void update(Long id, PersistablePromotionToken token, MerchantStore store, Language language);

	void delete(Long id, MerchantStore store, Language language);

	ReadablePromotionToken get(Long id, MerchantStore store, Language language);

	ReadablePromotionToken getByCode(String code, MerchantStore store, Language language);

	ReadableEntityList<ReadablePromotionToken> list(MerchantStore store, Language language);

	boolean exists(String code, MerchantStore store, Language language);
}
