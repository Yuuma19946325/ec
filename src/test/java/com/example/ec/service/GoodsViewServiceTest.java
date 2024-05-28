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

import com.example.ec.ServiceImpl.GoodsViewServiceImpl;
import com.example.ec.entity.GoodsView;
import com.example.ec.repository.GoodsViewRepository;

/**
 * GoodsView APIのサービステスト
 */
@SpringBootTest
public class GoodsViewServiceTest {

	@Autowired
	GoodsViewServiceImpl goodsViewService;

	private GoodsViewRepository goodsViewRepository;

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
		goodsViewRepository = (GoodsViewRepository) ReflectionTestUtils.getField(goodsViewService,
				"goodsViewRepository");

		// DBUnit データ抽入
		Connection connection = dataSource.getConnection();
		dbUnitConnection = new DatabaseConnection(connection);
		try (InputStream is = getClass().getClassLoader().getResourceAsStream("GoodsView.xml")) {
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
		ReflectionTestUtils.setField(goodsViewService, "goodsViewRepository", goodsViewRepository);

		// DBUnit データ削除
		DatabaseOperation.DELETE_ALL.execute(dbUnitConnection, dbUnitConnection.createDataSet());
		dbUnitConnection.close();
	}

	@Test
	@DisplayName("商品閲覧作成_正常終了")
	public void createGoodsView_OK() throws Exception {
		final GoodsView goodsViewDetails = new GoodsView((long) 1, (long) 99);

		// テスト実行
		goodsViewService.createGoodsView(goodsViewDetails);

		// DB Assert
		ITable actualTable = dbUnitConnection.createDataSet().getTable("goods_view");
		assertThat(actualTable.getRowCount()).isEqualTo(5);
		assertThat(actualTable.getValue(4, "goods_view_id")).isNotNull();
		assertThat(actualTable.getValue(4, "account_id")).isEqualTo(1);
		assertThat(actualTable.getValue(4, "goods_id")).isEqualTo(99);
		assertThat(actualTable.getValue(4, "update_data")).isNotNull();
		assertThat(actualTable.getValue(4, "delete_data")).isNull();
		assertThat(actualTable.getValue(4, "delete_flag")).isEqualTo(false);
	}

	@Test
	@DisplayName("商品閲覧作成_異常終了")
	public void createGoodsView_NG() throws Exception {
		final GoodsView goodsViewDetails = new GoodsView((long) 1, (long) 99);

		// リポジトリをnullに設定
		ReflectionTestUtils.setField(goodsViewService, "goodsViewRepository", null);

		// テスト実行
		Exception exception = assertThrows(Exception.class, () -> {
			goodsViewService.createGoodsView(goodsViewDetails);
		});

		assertThat(exception.getMessage()).contains("商品閲覧の作成に失敗しました");
	}

	@Test
	@DisplayName("商品閲覧リスト取得_正常終了")
	public void getGoodsViewList_OK() throws Exception {

		Long accountId = (long) 1;

		// テスト実行
		List<GoodsView> goodsViewList = goodsViewService.getGoodsViewList(accountId);

		// Assert
		assertThat(goodsViewList.get(0).getGoodsViewId()).isNotNull();
		assertThat(goodsViewList.get(0).getAccountId()).isEqualTo(1);
		assertThat(goodsViewList.get(0).getGoodsId()).isEqualTo(1);
		assertThat(goodsViewList.get(0).getUpdateData()).isNotNull();
		assertThat(goodsViewList.get(0).getDeleteData()).isNull();
		assertThat(goodsViewList.get(0).isDeleteFlag()).isEqualTo(false);

		// Assert
		assertThat(goodsViewList.get(1).getGoodsViewId()).isNotNull();
		assertThat(goodsViewList.get(1).getAccountId()).isEqualTo(1);
		assertThat(goodsViewList.get(1).getGoodsId()).isEqualTo(2);
		assertThat(goodsViewList.get(1).getUpdateData()).isNotNull();
		assertThat(goodsViewList.get(1).getDeleteData()).isNull();
		assertThat(goodsViewList.get(1).isDeleteFlag()).isEqualTo(false);
	}

	@Test
	@DisplayName("商品閲覧リスト取得_異常終了")
	public void getGoodsViewList_NG() throws Exception {
		Long accountId = (long) 1;

		// リポジトリをnullに設定
		ReflectionTestUtils.setField(goodsViewService, "goodsViewRepository", null);

		// テスト実行
		Exception exception = assertThrows(Exception.class, () -> {
			goodsViewService.getGoodsViewList(accountId);
		});

		assertThat(exception.getMessage()).contains("商品閲覧の取得に失敗しました");
	}

	@Test
	@DisplayName("商品閲覧情報チェック処理_チェック結果が問題なし")
	public void checkGoodsViewData_OK() throws Exception {
		final GoodsView goodsViewDetails = new GoodsView((long) 1, (long) 99);

		// テスト実行
		goodsViewService.checkGoodsViewData(goodsViewDetails);
	}

	@Test
	@DisplayName("商品閲覧情報チェック処理_アカウントが未存在")
	public void checkGoodsViewData_NG1() throws Exception {
		final GoodsView goodsViewDetails = new GoodsView((long) 0, (long) 99);

		// テスト実行	
		Exception exception = assertThrows(Exception.class, () -> {
			goodsViewService.checkGoodsViewData(goodsViewDetails);
		});

		// Assert
		assertThat(exception.getMessage()).contains("アカウントが未入力です");
	}

	@Test
	@DisplayName("商品閲覧情報チェック処理_アカウントが未存在")
	public void checkGoodsViewData_NG2() throws Exception {
		final GoodsView goodsViewDetails = new GoodsView((long) 1, (long) 0);

		// テスト実行	
		Exception exception = assertThrows(Exception.class, () -> {
			goodsViewService.checkGoodsViewData(goodsViewDetails);
		});

		// Assert
		assertThat(exception.getMessage()).contains("商品が未入力です");
	}
}
