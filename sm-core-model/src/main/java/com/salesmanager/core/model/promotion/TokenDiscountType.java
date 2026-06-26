package com.salesmanager.core.model.promotion;

/**
 * How a promotion token reduces the cart / order total.
 */
public enum TokenDiscountType {
	/** Discount is a percentage of the applicable amount (0-100). */
	PERCENTAGE,
	/** Discount is a fixed amount in the store currency. */
	FIXED
}
