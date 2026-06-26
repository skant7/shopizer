package com.salesmanager.shop.store.api.v1.promotion;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.model.entity.Entity;
import com.salesmanager.shop.model.entity.EntityExists;
import com.salesmanager.shop.model.entity.ReadableEntityList;
import com.salesmanager.shop.model.promotion.PersistablePromotionToken;
import com.salesmanager.shop.model.promotion.ReadablePromotionToken;
import com.salesmanager.shop.store.controller.promotion.facade.PromotionTokenFacade;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import springfox.documentation.annotations.ApiIgnore;

/**
 * Admin API for creating and managing promotion tokens (coupon codes) that customers apply to carts.
 */
@RestController
@RequestMapping(value = "/api/v1")
@Api(tags = { "Promotion token management (coupon codes)" })
@SwaggerDefinition(tags = {
		@Tag(name = "Promotion token management (coupon codes)", description = "Admin CRUD for promotion tokens / coupons") })
public class PromotionTokenApi {

	@Autowired
	private PromotionTokenFacade promotionTokenFacade;

	@PostMapping("/private/promotion/token")
	@ApiOperation(httpMethod = "POST", value = "Create a promotion token", notes = "Requires administration access", produces = "application/json", response = Entity.class)
	@ApiImplicitParams({ @ApiImplicitParam(name = "store", dataType = "string", defaultValue = "DEFAULT") })
	public Entity create(@ApiIgnore MerchantStore merchantStore, @ApiIgnore Language language,
			@Valid @RequestBody PersistablePromotionToken token) {
		return promotionTokenFacade.create(token, merchantStore, language);
	}

	@PutMapping("/private/promotion/token/{id}")
	@ApiOperation(httpMethod = "PUT", value = "Update a promotion token", notes = "Requires administration access", produces = "application/json")
	@ApiImplicitParams({ @ApiImplicitParam(name = "store", dataType = "string", defaultValue = "DEFAULT") })
	public void update(@PathVariable Long id, @ApiIgnore MerchantStore merchantStore, @ApiIgnore Language language,
			@Valid @RequestBody PersistablePromotionToken token) {
		promotionTokenFacade.update(id, token, merchantStore, language);
	}

	@GetMapping(value = "/private/promotion/token", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "List promotion tokens for the store", produces = "application/json", response = ReadableEntityList.class)
	@ApiImplicitParams({ @ApiImplicitParam(name = "store", dataType = "String", defaultValue = "DEFAULT"),
			@ApiImplicitParam(name = "lang", dataType = "String", defaultValue = "en") })
	public ReadableEntityList<ReadablePromotionToken> list(@ApiIgnore MerchantStore merchantStore,
			@ApiIgnore Language language) {
		return promotionTokenFacade.list(merchantStore, language);
	}

	@GetMapping("/private/promotion/token/{id}")
	@ApiOperation(httpMethod = "GET", value = "Get a promotion token by id", produces = "application/json", response = ReadablePromotionToken.class)
	@ApiImplicitParams({ @ApiImplicitParam(name = "store", dataType = "string", defaultValue = "DEFAULT") })
	public ReadablePromotionToken get(@PathVariable Long id, @ApiIgnore MerchantStore merchantStore,
			@ApiIgnore Language language) {
		return promotionTokenFacade.get(id, merchantStore, language);
	}

	@GetMapping("/private/promotion/token/code/{code}")
	@ApiOperation(httpMethod = "GET", value = "Get a promotion token by code", produces = "application/json", response = ReadablePromotionToken.class)
	@ApiImplicitParams({ @ApiImplicitParam(name = "store", dataType = "string", defaultValue = "DEFAULT") })
	public ReadablePromotionToken getByCode(@PathVariable String code, @ApiIgnore MerchantStore merchantStore,
			@ApiIgnore Language language) {
		return promotionTokenFacade.getByCode(code, merchantStore, language);
	}

	@GetMapping(value = "/private/promotion/token/unique", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "Check if promotion token code is unique", produces = "application/json", response = EntityExists.class)
	@ApiImplicitParams({ @ApiImplicitParam(name = "store", dataType = "String", defaultValue = "DEFAULT") })
	public ResponseEntity<EntityExists> exists(@RequestParam String code, @ApiIgnore MerchantStore merchantStore,
			@ApiIgnore Language language) {
		boolean exists = promotionTokenFacade.exists(code, merchantStore, language);
		return new ResponseEntity<>(new EntityExists(exists), HttpStatus.OK);
	}

	@DeleteMapping("/private/promotion/token/{id}")
	@ApiOperation(httpMethod = "DELETE", value = "Delete a promotion token", notes = "Requires administration access")
	@ApiImplicitParams({ @ApiImplicitParam(name = "store", dataType = "string", defaultValue = "DEFAULT") })
	public void delete(@PathVariable Long id, @ApiIgnore MerchantStore merchantStore, @ApiIgnore Language language) {
		promotionTokenFacade.delete(id, merchantStore, language);
	}
}
