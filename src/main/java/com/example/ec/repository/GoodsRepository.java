package com.example.ec.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.ec.entity.Goods;

@Repository
public interface GoodsRepository extends JpaRepository<Goods, Long> {

	@Query(value = "select * from goods ORDER BY goods_id", nativeQuery = true) // SQL
	List<Goods> findByGoodsList();
}
