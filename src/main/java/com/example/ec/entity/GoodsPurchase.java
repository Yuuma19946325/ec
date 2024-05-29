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
@Table(name = "goodsPurchase")
public class GoodsPurchase {

	public GoodsPurchase() {
	}

	public GoodsPurchase(Long accountId, Long goodsId, int purchasNumber) {
		this.accountId = accountId;
		this.goodsId = goodsId;
		this.purchasNumber = purchasNumber;
	}

	// 商品購入ID
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long goodsPurchaseId;

	// アカウントID
	@Column(name = "accountId")
	private Long accountId;

	// 商品ID
	@Column(name = "goodsId")
	private Long goodsId;

	// 購入回数
	@Column(name = "purchasNumber")
	private int purchasNumber;

	// 更新日時
	@Column(name = "updateData")
	private Date updateData;

	// 削除日時
	@Column(name = "delete_data", nullable = true)
	private Date deleteData;

	// 削除フラグ
	@Column(name = "deleteFlag")
	private boolean deleteFlag;

	/**
	 * 現在日時を更新日時に設定する
	 */
	public void setUpdateDataNow() {
		this.updateData = new Date();

	}

	/**
	 * オブジェクトの内部情報をチェックする
	 */
	public String checkGoodsPurchaseData() {

		ArrayList<String> errorMessage = new ArrayList<String>();

		if (0 == this.accountId)
			errorMessage.add("アカウント");

		if (0 == this.goodsId)
			errorMessage.add("商品");

		if (0 == this.purchasNumber)
			errorMessage.add("購入回数");

		if (!CollectionUtils.isEmpty(errorMessage))
			return String.join(",", errorMessage) + "が未入力です";

		return null;

	}
}
