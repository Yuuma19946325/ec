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

import com.example.ec.ServiceImpl.GoodsServiceImpl;
import com.example.ec.entity.Goods;
import com.example.ec.repository.GoodsRepository;

/**
 * Goods APIのサービステスト
 */
@SpringBootTest
public class GoodsServiceTest {

	@Autowired
	GoodsServiceImpl goodsService;

	GoodsRepository goodsRepository;

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
		goodsRepository = (GoodsRepository) ReflectionTestUtils.getField(goodsService, "goodsRepository");

		// DBUnit データ抽入
		Connection connection = dataSource.getConnection();
		dbUnitConnection = new DatabaseConnection(connection);
		try (InputStream is = getClass().getClassLoader().getResourceAsStream("Goods.xml")) {
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
		ReflectionTestUtils.setField(goodsService, "goodsRepository", goodsRepository);

		// DBUnit データ削除
		DatabaseOperation.DELETE_ALL.execute(dbUnitConnection, dbUnitConnection.createDataSet());
		dbUnitConnection.close();
	}

	@Test
	@DisplayName("商品作成_正常終了")
	public void createGoods_OK() throws Exception {
		final Goods goodsDetails = new Goods("テスト商品", 5, 5000, 50, 5,
				"ステンレス", "レフレム", "ブラックラビット", 18, 200, new byte[1]);

		// テスト実行
		goodsService.createGoods(goodsDetails);

		// DB Assert
		ITable actualTable = dbUnitConnection.createDataSet().getTable("goods");
		assertThat(actualTable.getRowCount()).isEqualTo(4);
		assertThat(actualTable.getValue(3, "goods_id")).isNotNull();
		assertThat(actualTable.getValue(3, "goods_name")).isEqualTo("テスト商品");
		assertThat(actualTable.getValue(3, "category_id")).isEqualTo(5);
		assertThat(actualTable.getValue(3, "amount")).isEqualTo(5000);
		assertThat(actualTable.getValue(3, "stock")).isEqualTo(50);
		assertThat(actualTable.getValue(3, "set")).isEqualTo(5);
		assertThat(actualTable.getValue(3, "material")).isEqualTo("ステンレス");
		assertThat(actualTable.getValue(3, "brand")).isEqualTo("レフレム");
		assertThat(actualTable.getValue(3, "theme")).isEqualTo("ブラックラビット");
		assertThat(actualTable.getValue(3, "target")).isEqualTo(18);
		assertThat(actualTable.getValue(3, "point")).isEqualTo(200);
		assertThat(actualTable.getValue(3, "image")).isNotNull();
		assertThat(actualTable.getValue(3, "update_data")).isNotNull();
		assertThat(actualTable.getValue(3, "delete_data")).isNull();
		assertThat(actualTable.getValue(3, "delete_flag")).isEqualTo(false);
	}

	@Test
	@DisplayName("商品作成_異常終了")
	public void createGoods_NG() throws Exception {
		final Goods goodsDetails = new Goods("テスト商品", 5, 5000, 50, 5,
				"ステンレス", "レフレム", "ブラックラビット", 18, 200, new byte[1]);

		// リポジトリをnullに設定
		ReflectionTestUtils.setField(goodsService, "goodsRepository", null);

		// テスト実行
		Exception exception = assertThrows(Exception.class, () -> {
			goodsService.createGoods(goodsDetails);
		});

		assertThat(exception.getMessage()).contains("商品の作成に失敗しました");
	}

	@Test
	@DisplayName("商品リスト取得_正常終了")
	public void getGoodsList_OK() throws Exception {

		// テスト実行
		List<Goods> goodsList = goodsService.getGoodsList();

		// Assert
		assertThat(goodsList.get(0).getGoodsId()).isEqualTo(1);
		assertThat(goodsList.get(0).getGoodsName()).isEqualTo("テスト商品1");
		assertThat(goodsList.get(0).getCategoryId()).isEqualTo(1);
		assertThat(goodsList.get(0).getAmount()).isEqualTo(1000);
		assertThat(goodsList.get(0).getStock()).isEqualTo(10);
		assertThat(goodsList.get(0).getSet()).isEqualTo(1);
		assertThat(goodsList.get(0).getMaterial()).isEqualTo("テスト材質1");
		assertThat(goodsList.get(0).getBrand()).isEqualTo("テストブランド1");
		assertThat(goodsList.get(0).getTheme()).isEqualTo("テストテーマ1");
		assertThat(goodsList.get(0).getTarget()).isEqualTo(1);
		assertThat(goodsList.get(0).getPoint()).isEqualTo(100);
		assertThat(goodsList.get(0).getImage()).isNotNull();
		assertThat(goodsList.get(0).getUpdateData()).isNotNull();
		assertThat(goodsList.get(0).getDeleteData()).isNull();
		assertThat(goodsList.get(0).isDeleteFlag()).isEqualTo(false);

		// Assert
		assertThat(goodsList.get(1).getGoodsId()).isEqualTo(2);
		assertThat(goodsList.get(1).getGoodsName()).isEqualTo("テスト商品2");
		assertThat(goodsList.get(1).getCategoryId()).isEqualTo(2);
		assertThat(goodsList.get(1).getAmount()).isEqualTo(2000);
		assertThat(goodsList.get(1).getStock()).isEqualTo(20);
		assertThat(goodsList.get(1).getSet()).isEqualTo(2);
		assertThat(goodsList.get(1).getMaterial()).isEqualTo("テスト材質2");
		assertThat(goodsList.get(1).getBrand()).isEqualTo("テストブランド2");
		assertThat(goodsList.get(1).getTheme()).isEqualTo("テストテーマ2");
		assertThat(goodsList.get(1).getTarget()).isEqualTo(2);
		assertThat(goodsList.get(1).getPoint()).isEqualTo(200);
		assertThat(goodsList.get(1).getImage()).isNotNull();
		assertThat(goodsList.get(1).getUpdateData()).isNotNull();
		assertThat(goodsList.get(1).getDeleteData()).isNull();
		assertThat(goodsList.get(1).isDeleteFlag()).isEqualTo(false);
	}

	@Test
	@DisplayName("商品リスト取得_異常終了")
	public void getGoodsList_NG() throws Exception {

		// リポジトリをnullに設定
		ReflectionTestUtils.setField(goodsService, "goodsRepository", null);

		// テスト実行
		Exception exception = assertThrows(Exception.class, () -> {
			goodsService.getGoodsList();
		});

		assertThat(exception.getMessage()).contains("商品の取得に失敗しました");
	}

	@Test
	@DisplayName("商品更新_正常終了")
	public void updateGoods_OK() throws Exception {
		final Long goodsId = (long) 2;

		final Goods goodsDetails = new Goods("テスト商品5", 5, 5000, 50, 5,
				"ステンレス", "レフレム", "ブラックラビット", 18, 200, new byte[1]);

		// テスト実行
		goodsService.updateGoods(goodsId, goodsDetails);

		// DB Assert
		ITable actualTable = dbUnitConnection.createDataSet().getTable("goods");
		assertThat(actualTable.getRowCount()).isEqualTo(3);
		assertThat(actualTable.getValue(1, "goods_id")).isNotNull();
		assertThat(actualTable.getValue(1, "goods_name")).isEqualTo("テスト商品5");
		assertThat(actualTable.getValue(1, "category_id")).isEqualTo(5);
		assertThat(actualTable.getValue(1, "amount")).isEqualTo(5000);
		assertThat(actualTable.getValue(1, "stock")).isEqualTo(50);
		assertThat(actualTable.getValue(1, "set")).isEqualTo(5);
		assertThat(actualTable.getValue(1, "material")).isEqualTo("ステンレス");
		assertThat(actualTable.getValue(1, "brand")).isEqualTo("レフレム");
		assertThat(actualTable.getValue(1, "theme")).isEqualTo("ブラックラビット");
		assertThat(actualTable.getValue(1, "target")).isEqualTo(18);
		assertThat(actualTable.getValue(1, "point")).isEqualTo(200);
		assertThat(actualTable.getValue(1, "image")).isNotNull();
		assertThat(actualTable.getValue(1, "update_data")).isNotNull();
		assertThat(actualTable.getValue(1, "delete_data")).isNull();
		assertThat(actualTable.getValue(1, "delete_flag")).isEqualTo(false);
	}

	@Test
	@DisplayName("商品更新_異常終了")
	public void updateGoods_NG() throws Exception {
		final Long goodsId = (long) 2;

		final Goods goodsDetails = new Goods("テスト商品5", 5, 5000, 50, 5,
				"ステンレス", "レフレム", "ブラックラビット", 18, 200, new byte[1]);

		// リポジトリをnullに設定
		ReflectionTestUtils.setField(goodsService, "goodsRepository", null);

		// テスト実行
		Exception exception = assertThrows(Exception.class, () -> {
			goodsService.updateGoods(goodsId, goodsDetails);
		});

		assertThat(exception.getMessage()).contains("商品の更新に失敗しました");
	}

	@Test
	@DisplayName("商品削除_正常終了")
	public void deleteGoods_OK() throws Exception {
		final Long goodsId = (long) 1;

		// テスト実行
		goodsService.deleteGoods(goodsId);

		// DB Assert
		ITable actualTable = dbUnitConnection.createDataSet().getTable("goods");
		assertThat(actualTable.getRowCount()).isEqualTo(3);
		assertThat(actualTable.getValue(0, "goods_id")).isNotNull();
		assertThat(actualTable.getValue(0, "goods_name")).isEqualTo("テスト商品1");
		assertThat(actualTable.getValue(0, "category_id")).isEqualTo(1);
		assertThat(actualTable.getValue(0, "amount")).isEqualTo(1000);
		assertThat(actualTable.getValue(0, "stock")).isEqualTo(10);
		assertThat(actualTable.getValue(0, "set")).isEqualTo(1);
		assertThat(actualTable.getValue(0, "material")).isEqualTo("テスト材質1");
		assertThat(actualTable.getValue(0, "brand")).isEqualTo("テストブランド1");
		assertThat(actualTable.getValue(0, "theme")).isEqualTo("テストテーマ1");
		assertThat(actualTable.getValue(0, "target")).isEqualTo(1);
		assertThat(actualTable.getValue(0, "point")).isEqualTo(100);
		assertThat(actualTable.getValue(0, "update_data")).isNotNull();
		assertThat(actualTable.getValue(0, "delete_data")).isNotNull();
		assertThat(actualTable.getValue(0, "delete_flag")).isEqualTo(true);
	}

	@Test
	@DisplayName("商品削除_異常終了")
	public void deleteGoods_NG() throws Exception {
		final Long goodsId = (long) 2;

		// リポジトリをnullに設定
		ReflectionTestUtils.setField(goodsService, "goodsRepository", null);

		// テスト実行
		Exception exception = assertThrows(Exception.class, () -> {
			goodsService.deleteGoods(goodsId);
		});

		assertThat(exception.getMessage()).contains("商品の削除に失敗しました");
	}

	@Test
	@DisplayName("商品情報チェック処理_チェック結果が問題なし")
	public void checkGoodsData_OK() throws Exception {
		final Goods goodsDetails = new Goods("テスト商品5", 5, 5000, 50, 5,
				"ステンレス", "レフレム", "ブラックラビット", 18, 200, new byte[1]);

		// テスト実行
		goodsService.checkGoodsData(goodsDetails);
	}

	@Test
	@DisplayName("商品情報チェック処理_商品名が未存在")
	public void checkGoodsData_NG1() throws Exception {
		final Goods goodsDetails = new Goods(null, 5, 5000, 50, 5,
				"ステンレス", "レフレム", "ブラックラビット", 18, 200, new byte[1]);

		// テスト実行	
		Exception exception = assertThrows(Exception.class, () -> {
			goodsService.checkGoodsData(goodsDetails);
		});

		// Assert
		assertThat(exception.getMessage()).contains("商品名が未入力です");
	}

	@Test
	@DisplayName("商品情報チェック処理_カテゴリが未存在")
	public void checkGoodsData_NG2() throws Exception {
		final Goods goodsDetails = new Goods("テスト商品5", 0, 5000, 50, 5,
				"ステンレス", "レフレム", "ブラックラビット", 18, 200, new byte[1]);

		// テスト実行	
		Exception exception = assertThrows(Exception.class, () -> {
			goodsService.checkGoodsData(goodsDetails);
		});

		// Assert
		assertThat(exception.getMessage()).contains("カテゴリが未入力です");
	}

	@Test
	@DisplayName("商品情報チェック処理_金額が未存在")
	public void checkGoodsData_NG3() throws Exception {
		final Goods goodsDetails = new Goods("テスト商品5", 5, 0, 50, 5,
				"ステンレス", "レフレム", "ブラックラビット", 18, 200, new byte[1]);

		// テスト実行	
		Exception exception = assertThrows(Exception.class, () -> {
			goodsService.checkGoodsData(goodsDetails);
		});

		// Assert
		assertThat(exception.getMessage()).contains("金額が未入力です");
	}

	@Test
	@DisplayName("商品情報チェック処理_在庫が未存在")
	public void checkGoodsData_NG4() throws Exception {
		final Goods goodsDetails = new Goods("テスト商品5", 5, 5000, 0, 5,
				"ステンレス", "レフレム", "ブラックラビット", 18, 200, new byte[1]);

		// テスト実行	
		Exception exception = assertThrows(Exception.class, () -> {
			goodsService.checkGoodsData(goodsDetails);
		});

		// Assert
		assertThat(exception.getMessage()).contains("在庫が未入力です");
	}

	@Test
	@DisplayName("商品情報チェック処理_画像が未存在")
	public void checkGoodsData_NG5() throws Exception {
		final Goods goodsDetails = new Goods("テスト商品5", 5, 5000, 0, 5,
				"ステンレス", "レフレム", "ブラックラビット", 18, 200, null);

		// テスト実行	
		Exception exception = assertThrows(Exception.class, () -> {
			goodsService.checkGoodsData(goodsDetails);
		});

		// Assert
		assertThat(exception.getMessage()).contains("画像が未入力です");
	}

	@Test
	@DisplayName("商品情報チェック処理_全てが未存在")
	public void checkGoodsData_NG6() throws Exception {
		final Goods goodsDetails = new Goods(null, 0, 0, 0, 5,
				"ステンレス", "レフレム", "ブラックラビット", 18, 200, null);

		// テスト実行	
		Exception exception = assertThrows(Exception.class, () -> {
			goodsService.checkGoodsData(goodsDetails);
		});

		// Assert
		assertThat(exception.getMessage()).contains("商品名,カテゴリ,金額,在庫,画像が未入力です");
	}
}
