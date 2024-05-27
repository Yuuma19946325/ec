package com.example.ec.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.ec.entity.Cart;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

	@Query(value = "select * from public.cart WHERE account_id = :account_id", nativeQuery = true) // SQL
	List<Cart> findByCartList(@Param("account_id") Long accountId);
}
