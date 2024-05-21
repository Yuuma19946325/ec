package com.example.ec.ServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.ec.Handler.BadRequestException;
import com.example.ec.model.Account;
import com.example.ec.repository.AccountRepository;
import com.example.ec.service.AccountService;

import io.micrometer.common.util.StringUtils;

@Service
public class AccountServiceImpl implements AccountService {

	@Autowired
	private AccountRepository accountRepository;

	/**
	 * アカウント作成
	 * @param accountDetails アカウント情報
	 * @throws Exception エラーレスポンス
	 */
	@Override
	public void createAccount(Account accountDetails) throws Exception {

		accountDetails.setUpdateDataNow();

		try {
			// DB→アカウント作成
			accountRepository.save(accountDetails);
		} catch (Exception e) {
			throw new Exception("アカウントの作成に失敗しました");
		}

	}

	/**
	 * アカウント取得
	 * @param accountId アカウントID
	 * @return アカウント情報
	 * @throws Exception エラーレスポンス
	 */
	@Override
	public Account getAccount(Long accountId) throws Exception {

		// アカウント情報インスタンスを作成
		Account account = new Account();

		try {
			// DB→アカウント情報取得
			account = accountRepository.findById(accountId).get();
		} catch (Exception e) {
			throw new Exception("アカウントの取得に失敗しました");
		}

		return account;
	}

	/**
	 * アカウント情報更新
	 * @param accountId アカウントID
	 * @param accountDetails アカウント情報
	 * @throws Exception エラーレスポンス
	 */
	@Override
	public void updateAccount(Long accountId, Account accountDetails) throws Exception {

		// アカウント取得
		Account account = getAccount(accountId);

		try {
			// アカウント情報を設定
			account.setAccountName(accountDetails.getAccountName());
			account.setPostCode(accountDetails.getPostCode());
			account.setAddress(accountDetails.getAddress());
			account.setTelephoneNumber(accountDetails.getTelephoneNumber());
			account.setUpdateDataNow();

			// DB→アカウント更新
			accountRepository.save(account);
		} catch (Exception e) {
			throw new Exception("アカウントの更新に失敗しました");
		}

	}

	/**
	 * アカウント情報チェック処理
	 * @param accountDetails アカウント情報
	 * @throws Exception エラーレスポンス
	 */
	@Override
	public void checkAccountData(Account accountDetails) throws BadRequestException {
		// アカウント情報チェック処理
		String accountErrorMessage = accountDetails.checkAccountData();
		// エラーメッセージが存在する場合
		if (StringUtils.isNotEmpty(accountErrorMessage))
			// 処理を異常終了で終了
			throw new BadRequestException(accountErrorMessage);
	}
}
