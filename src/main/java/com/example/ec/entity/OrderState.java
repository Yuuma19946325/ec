package com.example.ec.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "orderState")
public class OrderState {

	public OrderState() {
	}

	public OrderState(int orderState, String orderStateName) {
		this.orderState = orderState;
		this.orderStateName = orderStateName;
	}

	// 注文状態
	@Id
	@Column(name = "orderState")
	private int orderState;

	// 注文状態名称
	@Column(name = "orderStateName")
	private String orderStateName;
}
