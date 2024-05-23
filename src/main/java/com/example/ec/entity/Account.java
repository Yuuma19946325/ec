package com.example.ec.entity;

import java.util.ArrayList;
import java.util.Date;

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
@Table(name = "account")
public class Account {

	public Account() {
	}

	public Account(String accountName, String postCode, String address, String telephoneNumber, String mailAddress,
			String password) {

		this.accountName = accountName;
		this.postCode = postCode;
		this.address = address;
		this.telephoneNumber = telephoneNumber;
		this.mailAddress = mailAddress;
		this.password = password;
	}

	// アカウントID
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "accountId")
	private Long accountId;

	// アカウント名
	@Column(name = "accountName")
	private String accountName;

	// 郵便番号
	@Column(name = "postCode")
	private String postCode;

	// 住所
	@Column(name = "address")
	private String address;

	// 電話番号
	@Column(name = "telephoneNumber")
	private String telephoneNumber;

	// Eメールアドレス
	@Column(name = "mailAddress")
	private String mailAddress;

	// パスワード
	@Column(name = "password")
	private String password;

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
	public String checkAccountData() {

		ArrayList<String> errorMessage = new ArrayList<String>();

		if (StringUtils.isEmpty(this.accountName))
			errorMessage.add("アカウント名");

		if (StringUtils.isEmpty(this.postCode))
			errorMessage.add("郵便番号");

		if (StringUtils.isEmpty(this.address))
			errorMessage.add("住所");

		if (StringUtils.isEmpty(this.telephoneNumber))
			errorMessage.add("電話番号");

		if (StringUtils.isEmpty(this.mailAddress))
			errorMessage.add("Eメール");

		if (StringUtils.isEmpty(this.password))
			errorMessage.add("パスワード");

		if (!CollectionUtils.isEmpty(errorMessage))
			return String.join(",", errorMessage) + "が未入力です";

		return null;

	}
}
