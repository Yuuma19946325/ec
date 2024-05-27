package com.example.ec.entity;

import java.util.ArrayList;

import org.springframework.util.CollectionUtils;

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

	public Cart() {
	}

	public Cart(Long accountId, Long goodsId, int quantity, int totalAmount) {
		this.accountId = accountId;
		this.goodsId = goodsId;
		this.quantity = quantity;
		this.totalAmount = totalAmount;
	}

	// カートID
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cartId")
	private Long cartId;

	// アカウントID
	@Column(name = "accountId")
	private Long accountId;

	// 商品ID
	@Column(name = "goodsId")
	private Long goodsId;

	// 数量
	@Column(name = "quantity")
	private int quantity;

	// 数量合計金額
	@Column(name = "totalAmount")
	private int totalAmount;

	/**
	 * オブジェクトの内部情報をチェックする
	 */
	public String checkCartData() {

		ArrayList<String> errorMessage = new ArrayList<String>();

		if (0 == this.accountId)
			errorMessage.add("アカウント");

		if (0 == this.goodsId)
			errorMessage.add("商品");

		if (0 == this.quantity)
			errorMessage.add("数量");

		if (0 == this.totalAmount)
			errorMessage.add("数量合計金額");

		if (!CollectionUtils.isEmpty(errorMessage))
			return String.join(",", errorMessage) + "が未入力です";

		return null;

	}

	/*
	 * 数量合計金額と計算結果の合計金額が一致することの確認
	 */
	public String checkTotalAmount(int amount) {

		int total = this.quantity * amount;

		if (this.totalAmount != total)
			return "数量合計金額と計算結果が合いませんでした";

		return null;
	}
}
