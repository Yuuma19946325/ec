package com.example.ec.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.ec.entity.OrderDetails;

@Repository
public interface OrderDetailsRepository extends JpaRepository<OrderDetails, Long> {
	@Query(value = "select * from order_details where order_id = :order_id", nativeQuery = true) // SQL
	List<OrderDetails> findByOrderId(@Param("order_id") Long order_id);
}
