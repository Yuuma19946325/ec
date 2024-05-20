package com.example.ec.service;

import static org.junit.jupiter.api.Assertions.*;

import java.io.InputStream;

import org.dbunit.IDatabaseTester;
import org.dbunit.JdbcDatabaseTester;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.example.ec.ServiceImpl.AccountServiceImpl;
import com.example.ec.model.Account;

/**
 * Account APIのサービステスト
 */
public class AccountServiceTest {

	AccountServiceImpl accountService;

	private IDatabaseTester databaseTester;

	@BeforeEach
	void setUp() throws Exception {
		accountService = new AccountServiceImpl();

		// データベースに接続
		databaseTester = new JdbcDatabaseTester("org.postgresql.Driver",
				"jdbc:postgresql://localhost:5432/postgres", "postgres", "19946325");

		// リソースをクラスパスから取得
		InputStream is = getClass().getClassLoader().getResourceAsStream("Account.xml");
		if (is == null) {
			throw new IllegalArgumentException("File Not found");
		}

		// DbUnitのセットアップ
		IDataSet dataSet = new FlatXmlDataSetBuilder().build(is);
		databaseTester.setDataSet(dataSet);
		databaseTester.setSetUpOperation(DatabaseOperation.CLEAN_INSERT);
		databaseTester.onSetup();
	}

	@Test
	@DisplayName("アカウント作成")
	public void createAccount01() {
		final Account account = createAccount("小林", "3380014", "埼玉県", "080");
		// assertEquals(null, accountService.checkAccountData(account));   
	}

	@Test
	@DisplayName("チェック結果が問題なし")
	public void checkAccountData01() {
		final Account account = createAccount("小林", "3380014", "埼玉県", "080");

		try {
			// アカウント情報チェック処理
			accountService.checkAccountData(account);
		} catch (Exception e) {
			// 例外がスローされた場合は失敗
			fail("例外がスローされました: " + e.getMessage());
		}

	}

	@Test
	@DisplayName("アカウント名が未存在")
	public void checkAccountData02() {
		final Account account = createAccount(null, "3380014", "埼玉県", "080");

		// アカウント情報チェック処理		
		Exception exception = assertThrows(Exception.class, () -> {
			accountService.checkAccountData(account);
		});

		assertTrue(exception.getMessage().contains("アカウント名が未入力です"));
	}

	@Test
	@DisplayName("郵便番号が未存在")
	public void checkAccountData03() {
		final Account account = createAccount("小林", null, "埼玉県", "080");

		// アカウント情報チェック処理		
		Exception exception = assertThrows(Exception.class, () -> {
			accountService.checkAccountData(account);
		});

		assertTrue(exception.getMessage().contains("郵便番号が未入力です"));
	}

	@Test
	@DisplayName("住所が未存在")
	public void checkAccountData04() {
		final Account account = createAccount("小林", "3380014", null, "080");

		// アカウント情報チェック処理		
		Exception exception = assertThrows(Exception.class, () -> {
			accountService.checkAccountData(account);
		});

		assertTrue(exception.getMessage().contains("住所が未入力です"));
	}

	@Test
	@DisplayName("電話番号が未存在")
	public void checkAccountData05() {
		final Account account = createAccount("小林", "3380014", "埼玉県", null);

		// アカウント情報チェック処理		
		Exception exception = assertThrows(Exception.class, () -> {
			accountService.checkAccountData(account);
		});

		assertTrue(exception.getMessage().contains("電話番号が未入力です"));
	}

	@Test
	@DisplayName("全てが未存在")
	public void checkAccountData06() {
		final Account account = createAccount(null, null, null, null);

		// アカウント情報チェック処理		
		Exception exception = assertThrows(Exception.class, () -> {
			accountService.checkAccountData(account);
		});

		assertTrue(exception.getMessage().contains("アカウント名,郵便番号,住所,電話番号が未入力です"));
	}

	// @After
	public void tearDown() throws Exception {
		// データベース接続のクローズ
		if (databaseTester != null) {
			databaseTester.onTearDown();
		}
	}

	public Account createAccount(String accountName, String postCode, String address, String telephoneNumber) {
		Account account = new Account();

		account.setAccountName(accountName);
		account.setPostCode(postCode);
		account.setAddress(address);
		account.setTelephoneNumber(telephoneNumber);

		return account;

	}
}
