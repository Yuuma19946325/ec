package com.example.ec.service;

import org.springframework.stereotype.Service;

import com.example.ec.Handler.BadRequestException;
import com.example.ec.Handler.SQLException;
import com.example.ec.entity.Account;

@Service
public interface AccountService {

	// アカウント作成処理
	public void createAccount(Account accountDetails) throws SQLException;

	// アカウント情報取得処理
	public Account getAccount(Long accountId) throws SQLException;

	// アカウント更新処理
	public void updateAccount(Long accountId, Account accountDetails) throws SQLException;

	// アカウント情報チェック処理
	public void checkAccountData(Account accountDetails) throws BadRequestException;

	// アカウント存在チェック処理
	public void checkExistsAccount(String mailAddress) throws SQLException;
}
