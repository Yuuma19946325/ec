package com.example.ec.model;

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
@Table(name = "account")
public class Account {

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

	// 更新日時
	@Column(name = "updateData")
	private Date updateData;

	// 削除日時
	@Column(name = "delete_data")
	private Date deleteData;

	// 削除フラグ
	@Column(name = "deleteFlag")
	private boolean deleteFlag;

	/**
	 * @return accountId
	 */
	public Long getAccountId() {
		return accountId;
	}

	/**
	 * @param accountId セットする accountId
	 */
	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

	/**
	 * @return accountName
	 */
	public String getAccountName() {
		return accountName;
	}

	/**
	 * @param accountName セットする accountName
	 */
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	/**
	 * @return postCode
	 */
	public String getPostCode() {
		return postCode;
	}

	/**
	 * @param postCode セットする postCode
	 */
	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}

	/**
	 * @return address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @param address セットする address
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * @return telephoneNumber
	 */
	public String getTelephoneNumber() {
		return telephoneNumber;
	}

	/**
	 * @param telephoneNumber セットする telephoneNumber
	 */
	public void setTelephoneNumber(String telephoneNumber) {
		this.telephoneNumber = telephoneNumber;
	}

	/**
	 * @return updateData
	 */
	public Date getUpdateData() {
		return updateData;
	}

	/**
	 * @param updateData セットする updateData
	 */
	public void setUpdateData(Date updateData) {
		this.updateData = updateData;
	}

	/**
	 * @return deleteData
	 */
	public Date getDeleteData() {
		return deleteData;
	}

	/**
	 * @param deleteData セットする deleteData
	 */
	public void setDeleteData(Date deleteData) {
		this.deleteData = deleteData;
	}

	/**
	 * @return deleteFlag
	 */
	public boolean isDeleteFlag() {
		return deleteFlag;
	}

	/**
	 * @param deleteFlag セットする deleteFlag
	 */
	public void setDeleteFlag(boolean deleteFlag) {
		this.deleteFlag = deleteFlag;
	}

	public void setUpdateDataNow() {
		if (Objects.nonNull(this.updateData))
			return;
		this.updateData = new Date();

	}

	public String checkAccountData() {

		ArrayList<String> accountErrorMessage = new ArrayList<String>();

		if (StringUtils.isEmpty(this.accountName))
			accountErrorMessage.add("アカウント名");

		if (StringUtils.isEmpty(this.postCode))
			accountErrorMessage.add("郵便番号");

		if (StringUtils.isEmpty(this.address))
			accountErrorMessage.add("住所");

		if (StringUtils.isEmpty(this.telephoneNumber))
			accountErrorMessage.add("電話番号");

		if (!CollectionUtils.isEmpty(accountErrorMessage))
			return String.join(",", accountErrorMessage) + "が未入力です";

		return null;

	}
}
