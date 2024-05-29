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

import com.example.ec.ServiceImpl.GoodsPurchaseServiceImpl;
import com.example.ec.entity.GoodsPurchase;
import com.example.ec.repository.GoodsPurchaseRepository;

/**
 * GoodsPurchase APIのサービステスト
 */
@SpringBootTest
public class GoodsPurchaseServiceTest {

	@Autowired
	GoodsPurchaseServiceImpl goodsPurchaseService;

	private GoodsPurchaseRepository goodsPurchaseRepository;

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
		goodsPurchaseRepository = (GoodsPurchaseRepository) ReflectionTestUtils.getField(goodsPurchaseService,
				"goodsPurchaseRepository");

		// DBUnit データ抽入
		Connection connection = dataSource.getConnection();
		dbUnitConnection = new DatabaseConnection(connection);
		try (InputStream is = getClass().getClassLoader().getResourceAsStream("GoodsPurchase.xml")) {
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
		ReflectionTestUtils.setField(goodsPurchaseService, "goodsPurchaseRepository", goodsPurchaseRepository);

		// DBUnit データ削除
		DatabaseOperation.DELETE_ALL.execute(dbUnitConnection, dbUnitConnection.createDataSet());
		dbUnitConnection.close();
	}

	@Test
	@DisplayName("商品購入リスト取得_正常終了")
	public void getGoodsPurchaseList_OK() throws Exception {

		Long accountId = (long) 1;

		// テスト実行
		List<GoodsPurchase> goodsPurchaseList = goodsPurchaseService.getGoodsPurchaseList(accountId);

		// Assert
		assertThat(goodsPurchaseList.get(0).getGoodsPurchaseId()).isNotNull();
		assertThat(goodsPurchaseList.get(0).getAccountId()).isEqualTo(1);
		assertThat(goodsPurchaseList.get(0).getGoodsId()).isEqualTo(2);
		assertThat(goodsPurchaseList.get(0).getPurchasNumber()).isEqualTo(3);
		assertThat(goodsPurchaseList.get(0).getUpdateData()).isNotNull();
		assertThat(goodsPurchaseList.get(0).getDeleteData()).isNull();
		assertThat(goodsPurchaseList.get(0).isDeleteFlag()).isEqualTo(false);

		// Assert
		assertThat(goodsPurchaseList.get(1).getGoodsPurchaseId()).isNotNull();
		assertThat(goodsPurchaseList.get(1).getAccountId()).isEqualTo(1);
		assertThat(goodsPurchaseList.get(1).getGoodsId()).isEqualTo(1);
		assertThat(goodsPurchaseList.get(1).getPurchasNumber()).isEqualTo(4);
		assertThat(goodsPurchaseList.get(1).getUpdateData()).isNotNull();
		assertThat(goodsPurchaseList.get(1).getDeleteData()).isNull();
		assertThat(goodsPurchaseList.get(1).isDeleteFlag()).isEqualTo(false);
	}

	@Test
	@DisplayName("商品購入リスト取得_異常終了")
	public void getGoodsPurchaseList_NG() throws Exception {
		Long accountId = (long) 1;

		// リポジトリをnullに設定
		ReflectionTestUtils.setField(goodsPurchaseService, "goodsPurchaseRepository", null);

		// テスト実行
		Exception exception = assertThrows(Exception.class, () -> {
			goodsPurchaseService.getGoodsPurchaseList(accountId);
		});

		assertThat(exception.getMessage()).contains("商品購入の取得に失敗しました");
	}
}
