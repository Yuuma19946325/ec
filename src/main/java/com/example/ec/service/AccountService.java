package com.example.ec.service;

import org.springframework.stereotype.Service;

import com.example.ec.model.Account;

@Service
public interface AccountService {

	// アカウント作成処理
	public void createAccount(Account accountDetails) throws Exception;

	// アカウント情報取得処理
	public Account getAccount(Long accountId) throws Exception;

	// アカウント更新処理
	public void updateAccount(Long accountId, Account accountDetails) throws Exception;

	// アカウント情報チェック処理
	public void checkAccountData(Account accountDetails) throws Exception;
}
