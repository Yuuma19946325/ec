package com.example.ec.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.util.CollectionUtils;

import io.micrometer.common.util.StringUtils;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;

@Entity
@Data
@Table(name = "orderInformation")
public class OrderInformation {

	public OrderInformation() {
	}

	// 注文ID
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "orderId")
	private Long orderId;

	// アカウントID
	@Column(name = "accountId")
	private Long accountId;

	// 郵便番号
	@Column(name = "postCode")
	private String postCode;

	// 住所
	@Column(name = "address")
	private String address;

	// 合計金額
	@Column(name = "totalAmount")
	private int totalAmount;

	// 注文状態
	@Column(name = "orderState")
	private int orderState;

	// 更新日時
	@Column(name = "updateData")
	private Date updateData;

	// 注文詳細リスト
	@Transient
	private List<OrderDetails> orderDetails;

	/**
	 * 注文状態に注文中を設定する
	 */
	public void setOrder() {
		this.orderState = 0;
	}

	/**
	 * 現在日時を更新日時に設定する
	 */
	public void setUpdateDataNow() {
		this.updateData = new Date();
	}

	/**
	 * オブジェクトの内部情報をチェックする
	 */
	public String checkOrderData() {

		ArrayList<String> errorMessage = new ArrayList<String>();

		if (StringUtils.isEmpty(this.postCode))
			errorMessage.add("郵便番号");

		if (StringUtils.isEmpty(this.address))
			errorMessage.add("住所");

		if (0 == this.totalAmount)
			errorMessage.add("合計金額");

		if (!CollectionUtils.isEmpty(errorMessage))
			return String.join(",", errorMessage) + "が未入力です";

		return null;

	}
}
