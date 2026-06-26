package com.salesmanager.core.business.repositories.promotion;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.salesmanager.core.model.promotion.PromotionToken;

public interface PromotionTokenRepository extends JpaRepository<PromotionToken, Long> {

	@Query("select t from PromotionToken t left join fetch t.merchantStore tm where tm.id=?1")
	List<PromotionToken> findByStore(Integer storeId);

	@Query("select t from PromotionToken t left join fetch t.merchantStore tm where tm.id=?1 and upper(t.code)=upper(?2)")
	PromotionToken findByStoreAndCode(Integer storeId, String code);

	@Query("select t from PromotionToken t left join fetch t.merchantStore tm where t.id=?1")
	PromotionToken findOne(Long id);
}
