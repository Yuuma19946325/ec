package com.example.ec.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.InputStream;
import java.sql.Connection;

import javax.sql.DataSource;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.ec.ServiceImpl.AccountServiceImpl;
import com.example.ec.entity.Account;
import com.example.ec.repository.AccountRepository;

/**
 * Account APIのサービステスト
 */
@SpringBootTest
public class AccountServiceTest {

	@Autowired
	AccountServiceImpl accountService;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private DataSource dataSource;
	private IDatabaseConnection dbUnitConnection;

	/**
	 * テスト前に毎回実施
	 * @throws Exception
	 */
	@BeforeEach
	void setUp() throws Exception {
		accountService = new AccountServiceImpl();

		// DBUnit データ抽入
		Connection connection = dataSource.getConnection();
		dbUnitConnection = new DatabaseConnection(connection);
		try (InputStream is = getClass().getClassLoader().getResourceAsStream("Account.xml")) {
			IDataSet dataSet = new FlatXmlDataSetBuilder().build(is);
			DatabaseOperation.CLEAN_INSERT.execute(dbUnitConnection, dataSet);
		}
	}

	/**
	 * テスト後に毎回実施
	 * @throws Exception
	 */
	@AfterEach
	public void tearDown() throws Exception {
		// DBUnit データ削除
		DatabaseOperation.DELETE_ALL.execute(dbUnitConnection, dbUnitConnection.createDataSet());
		dbUnitConnection.close();
	}

	@Test
	@DisplayName("アカウント作成")
	public void createAccount01() {
		final Account account = createAccount("小林5", "3380014", "埼玉県", "080");

		accountRepository.save(account);
		// アカウント作成
		try {
			ITable actualTable = dbUnitConnection.createDataSet().getTable("account");

			assertThat(actualTable.getRowCount()).isEqualTo(3); // Assuming initial dataset has 2 records
			assertThat(actualTable.getValue(2, "account_name")).isEqualTo("小林5");
			assertThat(actualTable.getValue(2, "post_code")).isEqualTo("3380018");
			assertThat(actualTable.getValue(2, "delete_data")).isNull();

			System.out.println(actualTable);
			// accountService.createAccount(account);
		} catch (Exception e) {
			// 例外がスローされた場合は失敗
			fail("例外がスローされました: " + e.getMessage());
		}
	}

	@Test
	@DisplayName("アカウント取得")
	public void getAccount01() {

		try {
			// アカウント取得処理
			Account account = accountService.getAccount((long) 1);

			System.out.println(account);
		} catch (Exception e) {
			// 例外がスローされた場合は失敗
			fail("例外がスローされました: " + e.getMessage());
		}
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

	public Account createAccount(String accountName, String postCode, String address, String telephoneNumber) {
		Account account = new Account();

		account.setAccountName(accountName);
		account.setPostCode(postCode);
		account.setAddress(address);
		account.setTelephoneNumber(telephoneNumber);
		account.setUpdateDataNow();

		return account;

	}
}
