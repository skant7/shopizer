package com.salesmanager.core.model.promotion;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.salesmanager.core.model.generic.SalesManagerEntity;
import com.salesmanager.core.model.merchant.MerchantStore;

/**
 * Admin-managed promotion token (coupon code) that customers can apply to a shopping cart.
 */
@Entity
@Table(name = "PROMOTION_TOKEN",
		indexes = { @Index(name = "PROMO_TOKEN_CODE_IDX", columnList = "TOKEN_CODE") },
		uniqueConstraints = @UniqueConstraint(columnNames = { "MERCHANT_ID", "TOKEN_CODE" }))
public class PromotionToken extends SalesManagerEntity<Long, PromotionToken> {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "PROMOTION_TOKEN_ID", unique = true, nullable = false)
	@TableGenerator(name = "TABLE_GEN", table = "SM_SEQUENCER", pkColumnName = "SEQ_NAME",
			valueColumnName = "SEQ_COUNT", pkColumnValue = "PROMO_TOKEN_SEQ_NEXT_VAL")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "TABLE_GEN")
	private Long id;

	@NotEmpty
	@Column(name = "TOKEN_CODE", nullable = false, length = 32)
	private String code;

	@Column(name = "TOKEN_DESCRIPTION", length = 255)
	private String description;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "DISCOUNT_TYPE", nullable = false, length = 20)
	private TokenDiscountType discountType = TokenDiscountType.PERCENTAGE;

	@NotNull
	@Column(name = "DISCOUNT_VALUE", nullable = false, precision = 15, scale = 4)
	private BigDecimal discountValue;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "START_DATE")
	private Date startDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "END_DATE")
	private Date endDate;

	@Column(name = "ACTIVE")
	private boolean active = true;

	/** Null or &lt;= 0 means unlimited uses. */
	@Column(name = "MAX_USES")
	private Integer maxUses;

	@Column(name = "USES_COUNT")
	private int usesCount = 0;

	/** Minimum cart subtotal required to apply; null means no minimum. */
	@Column(name = "MIN_CART_AMOUNT", precision = 15, scale = 4)
	private BigDecimal minCartAmount;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MERCHANT_ID", nullable = false)
	private MerchantStore merchantStore;

	public PromotionToken() {
		super();
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

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

	public TokenDiscountType getDiscountType() {
		return discountType;
	}

	public void setDiscountType(TokenDiscountType discountType) {
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

	public MerchantStore getMerchantStore() {
		return merchantStore;
	}

	public void setMerchantStore(MerchantStore merchantStore) {
		this.merchantStore = merchantStore;
	}
}
