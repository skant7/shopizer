package com.salesmanager.shop.mapper.promotion;

import org.springframework.stereotype.Component;

import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.promotion.PromotionToken;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.mapper.Mapper;
import com.salesmanager.shop.model.promotion.ReadablePromotionToken;

@Component
public class ReadablePromotionTokenMapper implements Mapper<PromotionToken, ReadablePromotionToken> {

	@Override
	public ReadablePromotionToken convert(PromotionToken source, MerchantStore store, Language language) {
		ReadablePromotionToken destination = new ReadablePromotionToken();
		return merge(source, destination, store, language);
	}

	@Override
	public ReadablePromotionToken merge(PromotionToken source, ReadablePromotionToken destination, MerchantStore store,
			Language language) {
		if (source == null) {
			return destination;
		}
		destination.setId(source.getId());
		destination.setCode(source.getCode());
		destination.setDescription(source.getDescription());
		if (source.getDiscountType() != null) {
			destination.setDiscountType(source.getDiscountType().name());
		}
		destination.setDiscountValue(source.getDiscountValue());
		destination.setStartDate(source.getStartDate());
		destination.setEndDate(source.getEndDate());
		destination.setActive(source.isActive());
		destination.setMaxUses(source.getMaxUses());
		destination.setUsesCount(source.getUsesCount());
		destination.setMinCartAmount(source.getMinCartAmount());
		if (source.getMerchantStore() != null) {
			destination.setStore(source.getMerchantStore().getCode());
		} else if (store != null) {
			destination.setStore(store.getCode());
		}
		return destination;
	}
}
