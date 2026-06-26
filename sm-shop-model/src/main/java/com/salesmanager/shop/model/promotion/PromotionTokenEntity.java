package com.salesmanager.shop.model.promotion;

import java.math.BigDecimal;
import java.util.Date;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.salesmanager.shop.model.entity.Entity;

public class PromotionTokenEntity extends Entity {

	private static final long serialVersionUID = 1L;

	@NotEmpty
	@Size(min = 1, max = 32)
	private String code;

	@Size(max = 255)
	private String description;

	/** PERCENTAGE or FIXED */
	@NotEmpty
	private String discountType;

	@NotNull
	@DecimalMin(value = "0.0001", inclusive = true)
	private BigDecimal discountValue;

	private Date startDate;
	private Date endDate;
	private boolean active = true;
	private Integer maxUses;
	private int usesCount;
	private BigDecimal minCartAmount;
	private String store;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDiscountType() {
		return discountType;
	}

	public void setDiscountType(String discountType) {
		this.discountType = discountType;
	}

	public BigDecimal getDiscountValue() {
		return discountValue;
	}

	public void setDiscountValue(BigDecimal discountValue) {
		this.discountValue = discountValue;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Integer getMaxUses() {
		return maxUses;
	}

	public void setMaxUses(Integer maxUses) {
		this.maxUses = maxUses;
	}

	public int getUsesCount() {
		return usesCount;
	}

	public void setUsesCount(int usesCount) {
		this.usesCount = usesCount;
	}

	public BigDecimal getMinCartAmount() {
		return minCartAmount;
	}

	public void setMinCartAmount(BigDecimal minCartAmount) {
		this.minCartAmount = minCartAmount;
	}

	public String getStore() {
		return store;
	}

	public void setStore(String store) {
		this.store = store;
	}
}
