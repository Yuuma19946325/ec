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

import com.example.ec.ServiceImpl.OrderStateServiceImpl;
import com.example.ec.entity.OrderState;
import com.example.ec.repository.OrderStateRepository;

/**
 * OrderState APIのサービステスト
 */
@SpringBootTest
public class OrderStateServiceTest {

	@Autowired
	OrderStateServiceImpl orderStateService;

	private OrderStateRepository orderStateRepository;

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
		orderStateRepository = (OrderStateRepository) ReflectionTestUtils.getField(orderStateService,
				"orderStateRepository");

		// DBUnit データ抽入
		Connection connection = dataSource.getConnection();
		dbUnitConnection = new DatabaseConnection(connection);
		try (InputStream is = getClass().getClassLoader().getResourceAsStream("OrderState.xml")) {
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
		ReflectionTestUtils.setField(orderStateService, "orderStateRepository", orderStateRepository);

		// DBUnit データ削除
		DatabaseOperation.DELETE_ALL.execute(dbUnitConnection, dbUnitConnection.createDataSet());
		dbUnitConnection.close();
	}

	@Test
	@DisplayName("注文状態取得_正常終了")
	public void getOrderState_OK() throws Exception {

		// テスト実行
		List<OrderState> orderState = orderStateService.getOrderState();

		// Assert
		assertThat(orderState.get(0).getOrderState()).isEqualTo(0);
		assertThat(orderState.get(0).getOrderStateName()).isEqualTo("注文中");

		// Assert
		assertThat(orderState.get(1).getOrderState()).isEqualTo(1);
		assertThat(orderState.get(1).getOrderStateName()).isEqualTo("振込済み");

		// Assert
		assertThat(orderState.get(2).getOrderState()).isEqualTo(2);
		assertThat(orderState.get(2).getOrderStateName()).isEqualTo("発注済み");

		// Assert
		assertThat(orderState.get(3).getOrderState()).isEqualTo(9);
		assertThat(orderState.get(3).getOrderStateName()).isEqualTo("注文キャンセル(お店事情)");
	}

	@Test
	@DisplayName("注文状態取得_異常終了")
	public void getOrderState_NG() throws Exception {

		// リポジトリをnullに設定
		ReflectionTestUtils.setField(orderStateService, "orderStateRepository", null);

		// テスト実行
		Exception exception = assertThrows(Exception.class, () -> {
			orderStateService.getOrderState();
		});

		assertThat(exception.getMessage()).contains("注文状態の取得に失敗しました");
	}
}
