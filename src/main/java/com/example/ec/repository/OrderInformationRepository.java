package com.example.ec.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ec.entity.OrderInformation;

@Repository
public interface OrderInformationRepository extends JpaRepository<OrderInformation, Long> {
}
