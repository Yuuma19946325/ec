package com.example.ec.entity;

import java.util.ArrayList;
import java.util.Date;

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
@Table(name = "orderDetails")
public class OrderDetails {

	public OrderDetails() {
	}

	// 注文詳細ID
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "orderDetailsId")
	private Long orderDetailsId;

	// 注文ID
	@Column(name = "orderId")
	private Long orderId;

	// 商品ID
	@Column(name = "goodsId")
	private Long goodsId;

	// 数量
	@Column(name = "quantity")
	private int quantity;

	// 数量合計金額
	@Column(name = "quantityAmount")
	private int quantityAmount;

	// 更新日時
	@Column(name = "updateData")
	private Date updateData;

	/**
	 * 現在日時を更新日時に設定する
	 */
	public void setUpdateDataNow() {
		this.updateData = new Date();
	}

	/**
	 * オブジェクトの内部情報をチェックする
	 */
	public String checkOrderDetailsData() {

		ArrayList<String> errorMessage = new ArrayList<String>();

		if (0 == this.goodsId)
			errorMessage.add("商品ID");

		if (0 == this.quantity)
			errorMessage.add("数量");

		if (0 == this.quantityAmount)
			errorMessage.add("数量合計金額");

		if (!CollectionUtils.isEmpty(errorMessage))
			return "商品ID:" + this.goodsId + "=" + String.join(",", errorMessage) + "が未入力です";

		return null;

	}
}
