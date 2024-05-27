package com.example.ec.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.ec.ServiceImpl.CartServiceImpl;
import com.example.ec.entity.Cart;
import com.example.ec.repository.CartRepository;
import com.example.ec.repository.GoodsRepository;

/**
 * Cart APIのサービステスト
 */
@SpringBootTest
public class CartServiceTest {

	@Autowired
	CartServiceImpl cartService;

	private CartRepository cartRepository;

	private GoodsRepository goodsRepository;

	@Autowired
	private DataSource dataSource;
	private IDatabaseConnection dbUnitConnection;

	/**
	 * テスト前に毎回実施
	 * @throws Exception
	 */
	@BeforeEach
	void beforeEach() throws Exception {
		// 元のリポジトリを保存
		cartRepository = (CartRepository) ReflectionTestUtils.getField(cartService, "cartRepository");
		goodsRepository = (GoodsRepository) ReflectionTestUtils.getField(cartService, "goodsRepository");

		// DBUnit データ抽入
		Connection connection = dataSource.getConnection();
		dbUnitConnection = new DatabaseConnection(connection);
		try (InputStream is = getClass().getClassLoader().getResourceAsStream("Cart.xml")) {
			IDataSet originalDataSet = new FlatXmlDataSetBuilder().build(is);
			ReplacementDataSet dataSet = new ReplacementDataSet(originalDataSet);
			dataSet.addReplacementObject("", null);
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
	public void afterEach() throws Exception {
		// 元のリポジトリを戻す
		ReflectionTestUtils.setField(cartService, "cartRepository", cartRepository);
		ReflectionTestUtils.setField(cartService, "goodsRepository", goodsRepository);

		// DBUnit データ削除
		DatabaseOperation.DELETE_ALL.execute(dbUnitConnection, dbUnitConnection.createDataSet());
		dbUnitConnection.close();
	}

	@Test
	@DisplayName("カート作成_正常終了")
	public void createCart_OK() throws Exception {
		final Cart cartDetails = new Cart((long) 1, (long) 10, 3, 3000);

		// テスト実行
		cartService.createCart(cartDetails);

		// DB Assert
		ITable actualTable = dbUnitConnection.createDataSet().getTable("cart");
		assertThat(actualTable.getRowCount()).isEqualTo(3);
		assertThat(actualTable.getValue(2, "cart_id")).isNotNull();
		assertThat(actualTable.getValue(2, "account_id")).isEqualTo(1);
		assertThat(actualTable.getValue(2, "goods_id")).isEqualTo(10);
		assertThat(actualTable.getValue(2, "quantity")).isEqualTo(3);
		assertThat(actualTable.getValue(2, "total_amount")).isEqualTo(3000);
	}

	@Test
	@DisplayName("カート作成_異常終了")
	public void createCart_NG() throws Exception {
		final Cart cartDetails = new Cart((long) 1, (long) 10, 3, 3000);

		// リポジトリをnullに設定
		ReflectionTestUtils.setField(cartService, "cartRepository", null);

		// テスト実行
		Exception exception = assertThrows(Exception.class, () -> {
			cartService.createCart(cartDetails);
		});

		assertThat(exception.getMessage()).contains("カートの作成に失敗しました");
	}

	@Test
	@DisplayName("カート取得_正常終了")
	public void getCartList_OK() throws Exception {

		Long accountId = (long) 80;

		// テスト実行
		List<Cart> cartList = cartService.getCartList(accountId);

		// Assert
		assertThat(cartList.get(0).getCartId()).isNotNull();
		assertThat(cartList.get(0).getAccountId()).isEqualTo(80);
		assertThat(cartList.get(0).getGoodsId()).isEqualTo(1);
		assertThat(cartList.get(0).getQuantity()).isEqualTo(10);
		assertThat(cartList.get(0).getTotalAmount()).isEqualTo(10000);

		// Assert
		assertThat(cartList.get(1).getCartId()).isNotNull();
		assertThat(cartList.get(1).getAccountId()).isEqualTo(80);
		assertThat(cartList.get(1).getGoodsId()).isEqualTo(2);
		assertThat(cartList.get(1).getQuantity()).isEqualTo(20);
		assertThat(cartList.get(1).getTotalAmount()).isEqualTo(20000);
	}

	@Test
	@DisplayName("カート取得_異常終了")
	public void getCartList_NG() throws Exception {
		Long accountId = (long) 80;

		// リポジトリをnullに設定
		ReflectionTestUtils.setField(cartService, "cartRepository", null);

		// テスト実行
		Exception exception = assertThrows(Exception.class, () -> {
			cartService.getCartList(accountId);
		});

		assertThat(exception.getMessage()).contains("カートの取得に失敗しました");
	}

	@Test
	@DisplayName("カート更新_正常終了")
	public void updateCart_OK() throws Exception {
		final Long cartId = (long) 1;

		final Cart cartDetails = new Cart((long) 1, (long) 10, 3, 3000);

		// テスト実行
		cartService.updateCart(cartId, cartDetails);

		// DB Assert
		ITable actualTable = dbUnitConnection.createDataSet().getTable("cart");
		assertThat(actualTable.getRowCount()).isEqualTo(2);
		assertThat(actualTable.getValue(0, "cart_id")).isNotNull();
		assertThat(actualTable.getValue(0, "account_id")).isEqualTo(80);
		assertThat(actualTable.getValue(0, "goods_id")).isEqualTo(1);
		assertThat(actualTable.getValue(0, "quantity")).isEqualTo(3);
		assertThat(actualTable.getValue(0, "total_amount")).isEqualTo(3000);
	}

	@Test
	@DisplayName("カート更新_異常終了")
	public void updateCart_NG() throws Exception {
		final Long cartId = (long) 1;

		final Cart cartDetails = new Cart((long) 1, (long) 10, 3, 3000);

		// リポジトリをnullに設定
		ReflectionTestUtils.setField(cartService, "cartRepository", null);

		// テスト実行
		Exception exception = assertThrows(Exception.class, () -> {
			cartService.updateCart(cartId, cartDetails);
		});

		assertThat(exception.getMessage()).contains("カートの更新に失敗しました");
	}

	@Test
	@DisplayName("カート削除_正常終了")
	public void deleteCart_OK() throws Exception {
		final Long cartId = (long) 1;

		// テスト実行
		cartService.deleteCart(cartId);

		// DB Assert
		ITable actualTable = dbUnitConnection.createDataSet().getTable("cart");
		assertThat(actualTable.getRowCount()).isEqualTo(1);
	}

	@Test
	@DisplayName("カート削除_異常終了")
	public void deleteCart_NG() throws Exception {
		final Long cartId = (long) 1;

		// リポジトリをnullに設定
		ReflectionTestUtils.setField(cartService, "cartRepository", null);

		// テスト実行
		Exception exception = assertThrows(Exception.class, () -> {
			cartService.deleteCart(cartId);
		});

		assertThat(exception.getMessage()).contains("カートの削除に失敗しました");
	}

	@Test
	@DisplayName("カート情報チェック処理_チェック結果が問題なし")
	public void checkCartData_OK() throws Exception {
		final Cart cartDetails = new Cart((long) 1, (long) 1, 3, 3000);

		// テスト実行
		cartService.checkCartData(cartDetails);
	}

	@Test
	@DisplayName("カート情報チェック処理_アカウントが未存在")
	public void checkCartData_NG1() throws Exception {
		final Cart cartDetails = new Cart((long) 0, (long) 1, 3, 3000);

		// テスト実行	
		Exception exception = assertThrows(Exception.class, () -> {
			cartService.checkCartData(cartDetails);
		});

		// Assert
		assertThat(exception.getMessage()).contains("アカウントが未入力です");
	}

	@Test
	@DisplayName("カート情報チェック処理_商品が未存在")
	public void checkCartData_NG2() throws Exception {
		final Cart cartDetails = new Cart((long) 1, (long) 0, 3, 3000);

		// テスト実行	
		Exception exception = assertThrows(Exception.class, () -> {
			cartService.checkCartData(cartDetails);
		});

		// Assert
		assertThat(exception.getMessage()).contains("商品が未入力です");
	}

	@Test
	@DisplayName("カート情報チェック処理_数量が未存在")
	public void checkCartData_NG3() throws Exception {
		final Cart cartDetails = new Cart((long) 1, (long) 1, 0, 3000);

		// テスト実行	
		Exception exception = assertThrows(Exception.class, () -> {
			cartService.checkCartData(cartDetails);
		});

		// Assert
		assertThat(exception.getMessage()).contains("数量が未入力です");
	}

	@Test
	@DisplayName("カート情報チェック処理_数量合計金額が未存在")
	public void checkCartData_NG4() throws Exception {
		final Cart cartDetails = new Cart((long) 1, (long) 1, 3, 0);

		// テスト実行	
		Exception exception = assertThrows(Exception.class, () -> {
			cartService.checkCartData(cartDetails);
		});

		// Assert
		assertThat(exception.getMessage()).contains("数量合計金額が未入力です");
	}

	@Test
	@DisplayName("カート情報チェック処理_全てが未存在")
	public void checkCartData_NG5() throws Exception {
		final Cart cartDetails = new Cart((long) 0, (long) 0, 0, 0);

		// テスト実行	
		Exception exception = assertThrows(Exception.class, () -> {
			cartService.checkCartData(cartDetails);
		});

		// Assert
		assertThat(exception.getMessage()).contains("アカウント,商品,数量,数量合計金額が未入力です");
	}

	@Test
	@DisplayName("カート情報チェック処理_商品自体が未存在")
	public void checkCartData_NG6() throws Exception {
		final Cart cartDetails = new Cart((long) 1, (long) 999, 3, 3000);

		// テスト実行	
		Exception exception = assertThrows(Exception.class, () -> {
			cartService.checkCartData(cartDetails);
		});

		// Assert
		assertThat(exception.getMessage()).contains("商品が未存在です");
	}

	@Test
	@DisplayName("カート情報チェック処理_数量合計金額が未一致")
	public void checkCartData_NG7() throws Exception {
		final Cart cartDetails = new Cart((long) 1, (long) 1, 3, 9000);

		// テスト実行	
		Exception exception = assertThrows(Exception.class, () -> {
			cartService.checkCartData(cartDetails);
		});

		// Assert
		assertThat(exception.getMessage()).contains("数量合計金額と計算結果が合いませんでした");
	}
}
