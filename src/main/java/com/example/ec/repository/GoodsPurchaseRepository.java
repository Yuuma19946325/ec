package com.example.ec.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.ec.entity.GoodsPurchase;

@Repository
public interface GoodsPurchaseRepository extends JpaRepository<GoodsPurchase, Long> {

	@Query(value = "select * from goods_purchase WHERE account_id = :account_id ORDER BY update_data DESC", nativeQuery = true) // SQL
	List<GoodsPurchase> findByGoodsPurchaseList(@Param("account_id") Long accountId);

	@Query(value = "select * from goods_purchase WHERE account_id = :account_id AND goods_id = :goods_id", nativeQuery = true) // SQL
	GoodsPurchase findByGoodsPurchase(@Param("account_id") Long accountId, @Param("goods_id") Long goodsId);
}
