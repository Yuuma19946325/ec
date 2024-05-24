package com.example.ec.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "cart")
public class Cart {

	// アカウントID
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "accountId")
	private Long accountId;

	// 商品ID
	@Column(name = "goodsId")
	private Long goodsId;

	// 数量
	@Column(name = "quantity")
	private Long quantity;

	// 数量合計金額
	@Column(name = "totalAmount")
	private Long totalAmount;
}
