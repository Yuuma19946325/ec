package com.example.ec.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ec.Handler.ErrorResponse;
import com.example.ec.model.Account;
import com.example.ec.service.AccountService;

/**
 * アカウントコントローラー
 */
@RestController
@RequestMapping("/api")
public class AccountController {

	@Autowired
	AccountService accountService;

	/**
	 * アカウント作成
	 * @param accountDetails アカウント情報
	 * @return 完了レスポンス
	 * @throws Exception エラーレスポンス
	 */
	@PostMapping("/account")
	public ErrorResponse createAccount(@RequestBody Account accountDetails) throws Exception {

		// アカウント情報チェック処理
		accountService.checkAccountData(accountDetails);

		// アカウント作成処理
		accountService.createAccount(accountDetails);

		// 完了レスポンスを返却
		return new ErrorResponse(
				HttpStatus.CREATED.value(),
				null);
	}

	/**
	 * アカウント取得
	 * @param accountId アカウントID
	 * @return アカウント情報
	 * @throws Exception エラーレスポンス
	 */
	@GetMapping("/account/{accountId}")
	public Account getAccount(@PathVariable(value = "accountId") Long accountId) throws Exception {

		// アカウント情報取得処理
		return accountService.getAccount(accountId);
	}

	/**
	 * アカウント情報更新
	 * @param accountId アカウントID
	 * @param accountDetails アカウント情報
	 * @return 完了レスポンス
	 * @throws Exception エラーレスポンス
	 */
	@PutMapping("/account/{accountId}")
	public ErrorResponse updataAccount(@PathVariable(value = "accountId") Long accountId,
			@RequestBody Account accountDetails) throws Exception {

		// アカウント情報チェック処理
		accountService.checkAccountData(accountDetails);

		// アカウント更新処理
		accountService.updateAccount(accountId, accountDetails);

		// 完了レスポンスを返却
		return new ErrorResponse(
				HttpStatus.CREATED.value(),
				null);
	}
}
