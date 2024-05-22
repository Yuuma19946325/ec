package com.example.ec.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

import java.io.InputStream;
import java.sql.Connection;
import java.util.Optional;

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
		Account account = new Account("小林雄磨", "3380014", "埼玉県", "080", "yuuma19946325@gmail.com",
				"19946325Yuuma");
		account.setUpdateDataNow();

		accountRepository.save(account);
		// アカウント作成
		try {
			ITable actualTable = dbUnitConnection.createDataSet().getTable("account");

			assertThat(actualTable.getRowCount()).isEqualTo(3); // Assuming initial dataset has 2 records
			assertThat(actualTable.getValue(2, "account_name")).isEqualTo("小林雄磨");
			assertThat(actualTable.getValue(2, "post_code")).isEqualTo("3380014");
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

		Account account = new Account();

		// doReturn(accountDetails).when(accountRepository).findById((long) 1).get();
		// when(accountRepository.findById((long) 1)).thenReturn(accounts);

		when(accountRepository.findById((long) 1)).thenReturn(Optional.of(account));

		try {
			// アカウント取得処理
			account = accountService.getAccount((long) 1);

			// 結果の検証
			assertThat(account).isNotNull();
			assertThat(account.getAccountId()).isEqualTo(1);

		} catch (Exception e) {
			// 例外がスローされた場合は失敗
			fail("例外がスローされました: " + e.getMessage());
		}
	}

	@Test
	@DisplayName("チェック結果が問題なし")
	public void checkAccountData01() {
		Account account = new Account("小林雄磨", "3380014", "埼玉県", "080", "yuuma19946325@gmail.com",
				"19946325Yuuma");

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
		Account account = new Account(null, "3380014", "埼玉県", "080", "yuuma19946325@gmail.com",
				"19946325Yuuma");

		// アカウント情報チェック処理		
		Exception exception = assertThrows(Exception.class, () -> {
			accountService.checkAccountData(account);
		});

		assertTrue(exception.getMessage().contains("アカウント名が未入力です"));
	}

	@Test
	@DisplayName("郵便番号が未存在")
	public void checkAccountData03() {
		Account account = new Account("小林雄磨", null, "埼玉県", "080", "yuuma19946325@gmail.com",
				"19946325Yuuma");

		// アカウント情報チェック処理		
		Exception exception = assertThrows(Exception.class, () -> {
			accountService.checkAccountData(account);
		});

		assertTrue(exception.getMessage().contains("郵便番号が未入力です"));
	}

	@Test
	@DisplayName("住所が未存在")
	public void checkAccountData04() {
		Account account = new Account("小林雄磨", "3380014", null, "080", "yuuma19946325@gmail.com",
				"19946325Yuuma");

		// アカウント情報チェック処理		
		Exception exception = assertThrows(Exception.class, () -> {
			accountService.checkAccountData(account);
		});

		assertTrue(exception.getMessage().contains("住所が未入力です"));
	}

	@Test
	@DisplayName("電話番号が未存在")
	public void checkAccountData05() {
		Account account = new Account("小林雄磨", "3380014", "埼玉県", null, "yuuma19946325@gmail.com",
				"19946325Yuuma");
		// アカウント情報チェック処理		
		Exception exception = assertThrows(Exception.class, () -> {
			accountService.checkAccountData(account);
		});

		assertTrue(exception.getMessage().contains("電話番号が未入力です"));
	}

	@Test
	@DisplayName("Eメールアドレスが未存在")
	public void checkAccountData06() {
		Account account = new Account("小林雄磨", "3380014", "埼玉県", "080", null,
				"19946325Yuuma");
		// アカウント情報チェック処理		
		Exception exception = assertThrows(Exception.class, () -> {
			accountService.checkAccountData(account);
		});

		assertTrue(exception.getMessage().contains("Eメールが未入力です"));
	}

	@Test
	@DisplayName("パスワードが未存在")
	public void checkAccountData07() {
		Account account = new Account("小林雄磨", "3380014", "埼玉県", "080", "yuuma19946325@gmail.com",
				null);
		// アカウント情報チェック処理		
		Exception exception = assertThrows(Exception.class, () -> {
			accountService.checkAccountData(account);
		});

		assertTrue(exception.getMessage().contains("パスワードが未入力です"));
	}

	@Test
	@DisplayName("全てが未存在")
	public void checkAccountData08() {
		Account account = new Account();

		// アカウント情報チェック処理		
		Exception exception = assertThrows(Exception.class, () -> {
			accountService.checkAccountData(account);
		});

		assertTrue(exception.getMessage().contains("アカウント名,郵便番号,住所,電話番号,Eメール,パスワードが未入力です"));
	}
}
