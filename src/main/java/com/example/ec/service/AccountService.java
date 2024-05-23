package com.example.ec.service;

import org.springframework.stereotype.Service;

import com.example.ec.entity.Account;

@Service
public interface AccountService {

	// アカウント作成処理
	public void createAccount(Account accountDetails) throws Exception;

	// アカウント情報取得処理
	public Account getAccount(String mailAddress, String password) throws Exception;

	// アカウント更新処理
	public void updateAccount(Long accountId, Account accountDetails) throws Exception;

	// アカウント情報チェック処理
	public void checkAccountData(Account accountDetails) throws Exception;

	// アカウント存在チェック処理
	public void checkExistsAccount(String mailAddress) throws Exception;
}
