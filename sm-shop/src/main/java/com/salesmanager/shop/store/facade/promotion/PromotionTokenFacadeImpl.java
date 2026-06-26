package com.salesmanager.shop.store.facade.promotion;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.services.promotion.PromotionTokenService;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.promotion.PromotionToken;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.mapper.promotion.PersistablePromotionTokenMapper;
import com.salesmanager.shop.mapper.promotion.ReadablePromotionTokenMapper;
import com.salesmanager.shop.model.entity.Entity;
import com.salesmanager.shop.model.entity.ReadableEntityList;
import com.salesmanager.shop.model.promotion.PersistablePromotionToken;
import com.salesmanager.shop.model.promotion.ReadablePromotionToken;
import com.salesmanager.shop.store.api.exception.OperationNotAllowedException;
import com.salesmanager.shop.store.api.exception.ResourceNotFoundException;
import com.salesmanager.shop.store.api.exception.ServiceRuntimeException;
import com.salesmanager.shop.store.api.exception.UnauthorizedException;
import com.salesmanager.shop.store.controller.promotion.facade.PromotionTokenFacade;

@Service
public class PromotionTokenFacadeImpl implements PromotionTokenFacade {

	private static final Logger LOGGER = LoggerFactory.getLogger(PromotionTokenFacadeImpl.class);

	@Autowired
	private PromotionTokenService promotionTokenService;

	@Autowired
	private PersistablePromotionTokenMapper persistablePromotionTokenMapper;

	@Autowired
	private ReadablePromotionTokenMapper readablePromotionTokenMapper;

	@Override
	public Entity create(PersistablePromotionToken token, MerchantStore store, Language language) {
		Validate.notNull(token, "Promotion token cannot be null");
		Validate.notNull(store, "MerchantStore cannot be null");
		try {
			if (exists(token.getCode(), store, language)) {
				throw new OperationNotAllowedException(
						"Promotion token [" + token.getCode() + "] already exists for store [" + store.getCode() + "]");
			}
			token.setStore(store.getCode());
			PromotionToken model = persistablePromotionTokenMapper.convert(token, store, language);
			model.setUsesCount(0);
			model = promotionTokenService.saveOrUpdate(model);
			Entity entity = new Entity();
			entity.setId(model.getId());
			return entity;
		} catch (ServiceException e) {
			LOGGER.error("Error creating promotion token for store [{}]", store.getCode(), e);
			throw new ServiceRuntimeException("Error creating promotion token: " + e.getMessage(), e);
		}
	}

	@Override
	public void update(Long id, PersistablePromotionToken token, MerchantStore store, Language language) {
		Validate.notNull(id, "Promotion token id cannot be null");
		Validate.notNull(token, "Promotion token cannot be null");
		Validate.notNull(store, "MerchantStore cannot be null");
		try {
			PromotionToken existing = requireOwnedToken(id, store);
			// Preserve use count on update; code uniqueness if changed
			if (token.getCode() != null && !token.getCode().equalsIgnoreCase(existing.getCode())
					&& promotionTokenService.exists(token.getCode(), store)) {
				throw new OperationNotAllowedException(
						"Promotion token [" + token.getCode() + "] already exists for store [" + store.getCode() + "]");
			}
			int usesCount = existing.getUsesCount();
			token.setId(id);
			PromotionToken model = persistablePromotionTokenMapper.merge(token, existing, store, language);
			model.setUsesCount(usesCount);
			promotionTokenService.saveOrUpdate(model);
		} catch (ServiceException e) {
			LOGGER.error("Error updating promotion token [{}] for store [{}]", id, store.getCode(), e);
			throw new ServiceRuntimeException("Error updating promotion token: " + e.getMessage(), e);
		}
	}

	@Override
	public void delete(Long id, MerchantStore store, Language language) {
		Validate.notNull(id, "Promotion token id cannot be null");
		Validate.notNull(store, "MerchantStore cannot be null");
		try {
			PromotionToken existing = requireOwnedToken(id, store);
			promotionTokenService.delete(existing);
		} catch (ServiceException e) {
			LOGGER.error("Error deleting promotion token [{}] for store [{}]", id, store.getCode(), e);
			throw new ServiceRuntimeException("Error deleting promotion token: " + e.getMessage(), e);
		}
	}

	@Override
	public ReadablePromotionToken get(Long id, MerchantStore store, Language language) {
		Validate.notNull(id, "Promotion token id cannot be null");
		Validate.notNull(store, "MerchantStore cannot be null");
		PromotionToken existing = requireOwnedToken(id, store);
		return readablePromotionTokenMapper.convert(existing, store, language);
	}

	@Override
	public ReadablePromotionToken getByCode(String code, MerchantStore store, Language language) {
		Validate.notNull(store, "MerchantStore cannot be null");
		try {
			PromotionToken existing = promotionTokenService.getByCode(code, store);
			if (existing == null) {
				throw new ResourceNotFoundException(
						"Promotion token [" + code + "] not found for store [" + store.getCode() + "]");
			}
			assertOwned(existing, store);
			return readablePromotionTokenMapper.convert(existing, store, language);
		} catch (ServiceException e) {
			throw new ServiceRuntimeException("Error loading promotion token: " + e.getMessage(), e);
		}
	}

	@Override
	public ReadableEntityList<ReadablePromotionToken> list(MerchantStore store, Language language) {
		Validate.notNull(store, "MerchantStore cannot be null");
		try {
			List<PromotionToken> models = promotionTokenService.listByStore(store);
			List<ReadablePromotionToken> items = models.stream()
					.map(t -> readablePromotionTokenMapper.convert(t, store, language)).collect(Collectors.toList());
			ReadableEntityList<ReadablePromotionToken> list = new ReadableEntityList<>();
			list.setItems(items);
			list.setNumber(items.size());
			list.setTotalPages(1);
			list.setRecordsTotal((long) items.size());
			list.setRecordsFiltered(items.size());
			return list;
		} catch (ServiceException e) {
			throw new ServiceRuntimeException("Error listing promotion tokens: " + e.getMessage(), e);
		}
	}

	@Override
	public boolean exists(String code, MerchantStore store, Language language) {
		Validate.notNull(store, "MerchantStore cannot be null");
		try {
			return promotionTokenService.exists(code, store);
		} catch (ServiceException e) {
			throw new ServiceRuntimeException("Error checking promotion token existence: " + e.getMessage(), e);
		}
	}

	private PromotionToken requireOwnedToken(Long id, MerchantStore store) {
		PromotionToken existing = promotionTokenService.getById(id);
		if (existing == null) {
			throw new ResourceNotFoundException(
					"Promotion token [" + id + "] not found for store [" + store.getCode() + "]");
		}
		assertOwned(existing, store);
		return existing;
	}

	private void assertOwned(PromotionToken token, MerchantStore store) {
		if (token.getMerchantStore() == null || !token.getMerchantStore().getCode().equals(store.getCode())) {
			throw new UnauthorizedException(
					"MerchantStore [" + store.getCode() + "] cannot access promotion token [" + token.getId() + "]");
		}
	}
}
