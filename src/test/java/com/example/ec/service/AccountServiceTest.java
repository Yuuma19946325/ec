package com.example.ec.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.DataSetException;
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
import org.springframework.test.util.ReflectionTestUtils;

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

	AccountRepository accountRepository;

	@Autowired
	private DataSource dataSource;
	private IDatabaseConnection dbUnitConnection;

	/**
	 * テスト前に毎回実施
	 * @throws Exception
	 */
	@BeforeEach
	void setUp() throws Exception {
		// 元のリポジトリを保存
		accountRepository = (AccountRepository) ReflectionTestUtils.getField(accountService, "accountRepository");

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
	 * @throws SQLException 
	 * @throws DataSetException 
	 * @throws Exception
	 */
	@AfterEach
	public void down() throws Exception {
		// 元のリポジトリを戻す
		ReflectionTestUtils.setField(accountService, "accountRepository", accountRepository);

		// DBUnit データ削除
		DatabaseOperation.DELETE_ALL.execute(dbUnitConnection, dbUnitConnection.createDataSet());
		dbUnitConnection.close();
	}

	@Test
	@DisplayName("アカウント作成_正常終了")
	public void createAccount_OK() throws Exception {
		Account account = new Account("小林雄磨", "3380014", "埼玉県", "080", "yuuma19946325@gmail.com",
				"19946325Yuuma");

		// テスト実行
		accountService.createAccount(account);

		// DB Assert
		ITable actualTable = dbUnitConnection.createDataSet().getTable("account");
		assertThat(actualTable.getRowCount()).isEqualTo(3);
		assertThat(actualTable.getValue(2, "account_id")).isNotNull();
		assertThat(actualTable.getValue(2, "account_name")).isEqualTo("小林雄磨");
		assertThat(actualTable.getValue(2, "post_code")).isEqualTo("3380014");
		assertThat(actualTable.getValue(2, "address")).isEqualTo("埼玉県");
		assertThat(actualTable.getValue(2, "telephone_number")).isEqualTo("080");
		assertThat(actualTable.getValue(2, "mail_address")).isEqualTo("yuuma19946325@gmail.com");
		assertThat(actualTable.getValue(2, "password")).isEqualTo("19946325Yuuma");
		assertThat(actualTable.getValue(2, "update_data")).isNotNull();
		assertThat(actualTable.getValue(2, "delete_data")).isNull();
		assertThat(actualTable.getValue(2, "delete_flag")).isEqualTo(false);
	}

	@Test
	@DisplayName("アカウント作成_異常終了")
	public void createAccount_NG1() throws Exception {
		Account account = new Account("小林雄磨", "3380014", "埼玉県", "080", "yuuma19946325@gmail.com",
				"19946325Yuuma");

		// リポジトリをnullに設定
		ReflectionTestUtils.setField(accountService, "accountRepository", null);

		// テスト実行
		Exception exception = assertThrows(Exception.class, () -> {
			accountService.createAccount(account);
		});

		assertThat(exception.getMessage()).contains("アカウントの作成に失敗しました");
	}

	@Test
	@DisplayName("アカウント取得_正常終了")
	public void getAccount_OK() throws Exception {

		// テスト実行
		Account account = accountService.getAccount((long) 1);

		// Assert
		assertThat(account.getAccountId()).isEqualTo(1);
		assertThat(account.getAccountName()).isEqualTo("小林雄磨");
		assertThat(account.getPostCode()).isEqualTo("3380014");
		assertThat(account.getAddress()).isEqualTo("埼玉県");
		assertThat(account.getTelephoneNumber()).isEqualTo("080");
		assertThat(account.getMailAddress()).isEqualTo("yuuma199463252@gmail.com");
		assertThat(account.getPassword()).isEqualTo("19946325Yuuma");
		assertThat(account.getUpdateData()).isNotNull();
		assertThat(account.getDeleteData()).isNotNull();
		assertThat(account.isDeleteFlag()).isEqualTo(false);
	}

	@Test
	@DisplayName("アカウント取得_異常終了")
	public void getAccount_NG() throws Exception {

		// リポジトリをnullに設定
		ReflectionTestUtils.setField(accountService, "accountRepository", null);

		// テスト実行
		Exception exception = assertThrows(Exception.class, () -> {
			accountService.getAccount((long) 1);
		});

		assertThat(exception.getMessage()).contains("アカウントの取得に失敗しました");
	}

	@Test
	@DisplayName("アカウント情報更新_正常終了")
	public void updateAccount_OK() throws Exception {
		Account account = new Account("テスト太郎", "3380000", "テスト県", "090", "yuuma@gmail.com",
				"19946325");

		// テスト実行
		accountService.updateAccount((long) 1, account);

		// DB Assert
		ITable actualTable = dbUnitConnection.createDataSet().getTable("account");
		assertThat(actualTable.getRowCount()).isEqualTo(2);
		assertThat(actualTable.getValue(0, "account_id")).isNotNull();
		assertThat(actualTable.getValue(0, "account_name")).isEqualTo("テスト太郎");
		assertThat(actualTable.getValue(0, "post_code")).isEqualTo("3380000");
		assertThat(actualTable.getValue(0, "address")).isEqualTo("テスト県");
		assertThat(actualTable.getValue(0, "telephone_number")).isEqualTo("090");
		assertThat(actualTable.getValue(0, "mail_address")).isEqualTo("yuuma@gmail.com");
		assertThat(actualTable.getValue(0, "password")).isEqualTo("19946325");
		assertThat(actualTable.getValue(0, "update_data")).isNotNull();
		assertThat(actualTable.getValue(0, "delete_data")).isNotNull();
		assertThat(actualTable.getValue(0, "delete_flag")).isEqualTo(false);
	}

	@Test
	@DisplayName("アカウント情報更新_異常終了")
	public void updateAccount_NG() throws Exception {
		Account account = new Account("小林雄磨3", "3380014", "埼玉県", "080", "yuuma19946325@gmail.com",
				"19946325Yuuma");

		// リポジトリをnullに設定
		ReflectionTestUtils.setField(accountService, "accountRepository", null);

		// テスト実行
		Exception exception = assertThrows(Exception.class, () -> {
			accountService.updateAccount((long) 1, account);
		});

		assertThat(exception.getMessage()).contains("アカウントの更新に失敗しました");
	}

	@Test
	@DisplayName("アカウント情報チェック処理_チェック結果が問題なし")
	public void checkAccountData_OK() throws Exception {
		Account account = new Account("小林雄磨", "3380014", "埼玉県", "080", "yuuma19946325@gmail.com",
				"19946325Yuuma");

		// テスト実行
		accountService.checkAccountData(account);
	}

	@Test
	@DisplayName("アカウント情報チェック処理_アカウント名が未存在")
	public void checkAccountData_NG1() throws Exception {
		Account account = new Account(null, "3380014", "埼玉県", "080", "yuuma19946325@gmail.com",
				"19946325Yuuma");

		// テスト実行	
		Exception exception = assertThrows(Exception.class, () -> {
			accountService.checkAccountData(account);
		});

		// Assert
		assertThat(exception.getMessage()).contains("アカウント名が未入力です");
	}

	@Test
	@DisplayName("アカウント情報チェック処理_郵便番号が未存在")
	public void checkAccountData_NG2() throws Exception {
		Account account = new Account("小林雄磨", null, "埼玉県", "080", "yuuma19946325@gmail.com",
				"19946325Yuuma");

		// テスト実行	
		Exception exception = assertThrows(Exception.class, () -> {
			accountService.checkAccountData(account);
		});

		// Assert
		assertThat(exception.getMessage()).contains("郵便番号が未入力です");
	}

	@Test
	@DisplayName("アカウント情報チェック処理_住所が未存在")
	public void checkAccountData_NG3() throws Exception {
		Account account = new Account("小林雄磨", "3380014", null, "080", "yuuma19946325@gmail.com",
				"19946325Yuuma");

		// テスト実行		
		Exception exception = assertThrows(Exception.class, () -> {
			accountService.checkAccountData(account);
		});

		// Assert
		assertThat(exception.getMessage()).contains("住所が未入力です");
	}

	@Test
	@DisplayName("アカウント情報チェック処理_電話番号が未存在")
	public void checkAccountData_NG4() throws Exception {
		Account account = new Account("小林雄磨", "3380014", "埼玉県", null, "yuuma19946325@gmail.com",
				"19946325Yuuma");

		// テスト実行	
		Exception exception = assertThrows(Exception.class, () -> {
			accountService.checkAccountData(account);
		});

		// Assert
		assertThat(exception.getMessage()).contains("電話番号が未入力です");
	}

	@Test
	@DisplayName("アカウント情報チェック処理_Eメールアドレスが未存在")
	public void checkAccountData_NG5() throws Exception {
		Account account = new Account("小林雄磨", "3380014", "埼玉県", "080", null,
				"19946325Yuuma");

		// テスト実行		
		Exception exception = assertThrows(Exception.class, () -> {
			accountService.checkAccountData(account);
		});

		// Assert
		assertThat(exception.getMessage()).contains("Eメールが未入力です");
	}

	@Test
	@DisplayName("アカウント情報チェック処理_パスワードが未存在")
	public void checkAccountData_NG6() throws Exception {
		Account account = new Account("小林雄磨", "3380014", "埼玉県", "080", "yuuma19946325@gmail.com",
				null);

		// テスト実行		
		Exception exception = assertThrows(Exception.class, () -> {
			accountService.checkAccountData(account);
		});

		// Assert
		assertThat(exception.getMessage()).contains("パスワードが未入力です");
	}

	@Test
	@DisplayName("アカウント情報チェック処理_全てが未存在")
	public void checkAccountData_NG7() throws Exception {
		Account account = new Account();

		// テスト実行		
		Exception exception = assertThrows(Exception.class, () -> {
			accountService.checkAccountData(account);
		});

		// Assert
		assertThat(exception.getMessage()).contains("アカウント名,郵便番号,住所,電話番号,Eメール,パスワードが未入力です");
	}

	@Test
	@DisplayName("アカウント存在チェック処理_未存在")
	public void checkExistsAccount_OK() throws Exception {

		// テスト実行
		accountService.checkExistsAccount("yuuma19946325@gmail.com");
	}

	@Test
	@DisplayName("アカウント存在チェック処理_メールアドレス存在")
	public void checkExistsAccount_NG1() throws Exception {

		// テスト実行		
		Exception exception = assertThrows(Exception.class, () -> {
			accountService.checkExistsAccount("yuuma199463251@gmail.com");
		});

		// Assert
		assertThat(exception.getMessage()).contains("このメールアドレスは既に存在しています");
	}

	@Test
	@DisplayName("アカウント存在チェック処理_アカウント情報取得失敗")
	public void checkExistsAccount_NG2() throws Exception {
		// リポジトリをnullに設定
		ReflectionTestUtils.setField(accountService, "accountRepository", null);

		// テスト実行		
		Exception exception = assertThrows(Exception.class, () -> {
			accountService.checkExistsAccount("yuuma199463251@gmail.com");
		});

		assertThat(exception.getMessage()).contains("アカウントの取得に失敗しました");
	}
}
