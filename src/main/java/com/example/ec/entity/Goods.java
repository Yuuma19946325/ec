package com.example.ec.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import org.springframework.util.CollectionUtils;

import io.micrometer.common.util.StringUtils;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "goods")
public class Goods {

	// 商品ID
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "goodsId")
	private Long goodsId;

	// 商品名
	@Column(name = "goodsName")
	private String goodsName;

	// カテゴリーID
	@Column(name = "categoryId")
	private int categoryId;

	// 金額
	@Column(name = "amount")
	private int amount;

	// 在庫
	@Column(name = "stock")
	private int stock;

	// セット個数
	@Column(name = "set")
	private int set;

	// 材質,
	@Column(name = "material")
	private String material;

	// ブランド
	@Column(name = "brand")
	private String brand;

	// テーマ
	@Column(name = "theme")
	private String theme;

	// 対象年齢
	@Column(name = "target")
	private int target;

	// 付与ポイント
	@Column(name = "point")
	private int point;

	// 商品画像URL1
	@Column(name = "goodsImage1")
	private byte goodsImage1;

	// 商品画像URL2
	@Column(name = "goodsImage2")
	private byte goodsImage2;

	// 商品画像URL3
	@Column(name = "goodsImage3")
	private byte goodsImage3;

	// 商品画像URL4
	@Column(name = "goodsImage4")
	private byte goodsImage4;

	// 商品画像URL5
	@Column(name = "goodsImage5")
	private byte goodsImage5;

	// 商品画像URL6
	@Column(name = "goodsImage6")
	private byte goodsImage6;

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
	 * 削除状態に設定する
	 */
	public void setDelet() {
		this.deleteData = new Date();
		this.deleteFlag = true;
	}

	/**
	 * オブジェクトの内部情報をチェックする
	 */
	public String checkGoodsData() {

		ArrayList<String> errorMessage = new ArrayList<String>();

		if (StringUtils.isEmpty(this.goodsName))
			errorMessage.add("商品名");

		if (0 == this.amount)
			errorMessage.add("金額");

		if (0 == this.stock)
			errorMessage.add("在庫");

		if (Objects.isNull(this.goodsImage1))
			errorMessage.add("画像1");

		if (!CollectionUtils.isEmpty(errorMessage))
			return String.join(",", errorMessage) + "が未入力です";

		return null;

	}
}
