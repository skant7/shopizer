package com.salesmanager.test.promotion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.repositories.promotion.PromotionTokenRepository;
import com.salesmanager.core.business.services.promotion.PromotionTokenServiceImpl;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.promotion.PromotionToken;
import com.salesmanager.core.model.promotion.TokenDiscountType;

@RunWith(MockitoJUnitRunner.class)
public class PromotionTokenServiceTest {

	@Mock
	private PromotionTokenRepository promotionTokenRepository;

	private PromotionTokenServiceImpl service;
	private MerchantStore store;
	private PromotionToken token;

	@Before
	public void setUp() {
		service = new PromotionTokenServiceImpl(promotionTokenRepository);
		store = new MerchantStore();
		store.setId(1);
		store.setCode("DEFAULT");

		token = new PromotionToken();
		token.setId(10L);
		token.setCode("SAVE10");
		token.setActive(true);
		token.setDiscountType(TokenDiscountType.PERCENTAGE);
		token.setDiscountValue(new BigDecimal("10"));
		token.setMerchantStore(store);
		token.setUsesCount(0);
	}

	@Test
	public void validateForCart_success() throws Exception {
		when(promotionTokenRepository.findByStoreAndCode(eq(1), eq("SAVE10"))).thenReturn(token);
		PromotionToken result = service.validateForCart("SAVE10", store, new BigDecimal("50"), new Date());
		assertNotNull(result);
		assertEquals("SAVE10", result.getCode());
	}

	@Test
	public void validateForCart_rejectsInactive() throws Exception {
		token.setActive(false);
		when(promotionTokenRepository.findByStoreAndCode(anyInt(), anyString())).thenReturn(token);
		try {
			service.validateForCart("SAVE10", store, BigDecimal.TEN, new Date());
			fail("expected ServiceException");
		} catch (ServiceException e) {
			// expected
		}
	}

	@Test
	public void validateForCart_rejectsExpired() throws Exception {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -2);
		token.setEndDate(cal.getTime());
		when(promotionTokenRepository.findByStoreAndCode(anyInt(), anyString())).thenReturn(token);
		try {
			service.validateForCart("SAVE10", store, BigDecimal.TEN, new Date());
			fail("expected ServiceException");
		} catch (ServiceException e) {
			// expected
		}
	}

	@Test
	public void validateForCart_rejectsMaxUses() throws Exception {
		token.setMaxUses(1);
		token.setUsesCount(1);
		when(promotionTokenRepository.findByStoreAndCode(anyInt(), anyString())).thenReturn(token);
		try {
			service.validateForCart("SAVE10", store, BigDecimal.TEN, new Date());
			fail("expected ServiceException");
		} catch (ServiceException e) {
			// expected
		}
	}

	@Test
	public void validateForCart_rejectsMinCartAmount() throws Exception {
		token.setMinCartAmount(new BigDecimal("100"));
		when(promotionTokenRepository.findByStoreAndCode(anyInt(), anyString())).thenReturn(token);
		try {
			service.validateForCart("SAVE10", store, new BigDecimal("50"), new Date());
			fail("expected ServiceException");
		} catch (ServiceException e) {
			// expected
		}
	}

	@Test
	public void validateForCart_rejectsUnknownCode() throws Exception {
		when(promotionTokenRepository.findByStoreAndCode(anyInt(), anyString())).thenReturn(null);
		try {
			service.validateForCart("UNKNOWN", store, BigDecimal.TEN, new Date());
			fail("expected ServiceException");
		} catch (ServiceException e) {
			// expected
		}
	}

	@Test
	public void saveOrUpdate_rejectsInvalidPercentage() throws Exception {
		token.setDiscountValue(new BigDecimal("150"));
		try {
			service.saveOrUpdate(token);
			fail("expected ServiceException");
		} catch (ServiceException e) {
			// expected
		}
	}
}
