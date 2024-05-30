package com.example.ec.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ec.entity.OrderState;

public interface OrderStateRepository extends JpaRepository<OrderState, Integer> {

}
