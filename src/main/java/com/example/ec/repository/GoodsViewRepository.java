package com.example.ec.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.ec.entity.GoodsView;

public interface GoodsViewRepository extends JpaRepository<GoodsView, Long> {

	@Query(value = "select * from goods_view WHERE account_id = :account_id AND delete_flag = false ORDER BY goods_id", nativeQuery = true) // SQL
	List<GoodsView> findByGoodsViewList(@Param("account_id") Long accountId);
}
