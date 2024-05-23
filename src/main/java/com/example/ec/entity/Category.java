package com.example.ec.entity;

import java.util.Date;

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
@Table(name = "category")
public class Category {

	public Category() {
	}

	public Category(String categoryName) {

		this.categoryName = categoryName;
	}

	// カテゴリID
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "categoryId")
	private Long categoryId;

	// カテゴリ名
	@Column(name = "categoryName")
	private String categoryName;

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
	 * 実施状態に設定する
	 */
	public void setNotDelet() {
		this.deleteData = null;
		this.deleteFlag = false;
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
	public String checkCategoryData() {

		if (StringUtils.isEmpty(this.categoryName))
			return "カテゴリ名が未入力です";

		return null;

	}
}
