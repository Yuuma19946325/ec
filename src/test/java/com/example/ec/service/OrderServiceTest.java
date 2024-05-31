package com.example.ec.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
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

import com.example.ec.ServiceImpl.OrderServiceImpl;
import com.example.ec.entity.OrderDetails;
import com.example.ec.entity.OrderInformation;
import com.example.ec.repository.GoodsPurchaseRepository;
import com.example.ec.repository.OrderDetailsRepository;
import com.example.ec.repository.OrderInformationRepository;

/**
 * OrderService APIのサービステスト
 */
@SpringBootTest
public class OrderServiceTest {

	@Autowired
	OrderServiceImpl orderService;

	private OrderInformationRepository orderRepository;

	private OrderDetailsRepository orderDetailsRepository;

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
		orderRepository = (OrderInformationRepository) ReflectionTestUtils.getField(orderService,
				"orderRepository");
		orderDetailsRepository = (OrderDetailsRepository) ReflectionTestUtils.getField(orderService,
				"orderDetailsRepository");
		goodsPurchaseRepository = (GoodsPurchaseRepository) ReflectionTestUtils.getField(orderService,
				"goodsPurchaseRepository");

		// DBUnit データ抽入
		Connection connection = dataSource.getConnection();
		dbUnitConnection = new DatabaseConnection(connection);
		try (InputStream is = getClass().getClassLoader().getResourceAsStream("Order.xml")) {
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
		ReflectionTestUtils.setField(orderService, "orderRepository", orderRepository);
		ReflectionTestUtils.setField(orderService, "orderDetailsRepository", orderDetailsRepository);
		ReflectionTestUtils.setField(orderService, "goodsPurchaseRepository", goodsPurchaseRepository);

		// DBUnit データ削除
		DatabaseOperation.DELETE_ALL.execute(dbUnitConnection, dbUnitConnection.createDataSet());
		dbUnitConnection.close();
	}

	@Test
	@DisplayName("注文作成_正常終了")
	public void createOrder_OK() throws Exception {

		List<OrderDetails> orderDetailsList = new ArrayList<OrderDetails>();
		orderDetailsList.add(new OrderDetails(1, 1, 2, 20000));
		orderDetailsList.add(new OrderDetails(1, 2, 1, 1000));
		final OrderInformation orderInformation = new OrderInformation(1, "3380014", "埼玉県", 21000, 0, orderDetailsList);

		// テスト実行
		OrderInformation returnOrder = orderService.createOrder(orderInformation);

		// Assert
		assertThat(returnOrder.getOrderId()).isNotNull();

		// DB Assert
		ITable actualTable = dbUnitConnection.createDataSet().getTable("order_information");
		assertThat(actualTable.getRowCount()).isEqualTo(2);
		assertThat(actualTable.getValue(1, "order_id")).isNotNull();
		assertThat(actualTable.getValue(1, "account_id")).isEqualTo(1);
		assertThat(actualTable.getValue(1, "post_code")).isEqualTo("3380014");
		assertThat(actualTable.getValue(1, "address")).isEqualTo("埼玉県");
		assertThat(actualTable.getValue(1, "total_amount")).isEqualTo(21000);
		assertThat(actualTable.getValue(1, "order_state")).isEqualTo(0);
		assertThat(actualTable.getValue(1, "update_data")).isNotNull();
	}

	@Test
	@DisplayName("注文作成_異常終了")
	public void createOrder_NG() throws Exception {
		List<OrderDetails> orderDetailsList = new ArrayList<OrderDetails>();
		orderDetailsList.add(new OrderDetails(1, 1, 2, 20000));
		orderDetailsList.add(new OrderDetails(1, 2, 1, 1000));
		final OrderInformation orderInformation = new OrderInformation(1, "3380014", "埼玉県", 21000, 0, orderDetailsList);

		// リポジトリをnullに設定
		ReflectionTestUtils.setField(orderService, "orderRepository", null);

		// テスト実行
		Exception exception = assertThrows(Exception.class, () -> {
			orderService.createOrder(orderInformation);
		});

		assertThat(exception.getMessage()).contains("注文の作成に失敗しました");
	}

	@Test
	@DisplayName("注文詳細作成_正常終了")
	public void createOrderDetails_OK() throws Exception {

		List<OrderDetails> orderDetailsList = new ArrayList<OrderDetails>();
		orderDetailsList.add(new OrderDetails(1, 1, 2, 20000));
		orderDetailsList.add(new OrderDetails(1, 2, 1, 1000));

		// テスト実行
		orderService.createOrderDetails((long) 1, orderDetailsList);

		// DB Assert
		ITable actualTable = dbUnitConnection.createDataSet().getTable("order_details");
		assertThat(actualTable.getRowCount()).isEqualTo(4);
		assertThat(actualTable.getValue(2, "order_details_id")).isNotNull();
		assertThat(actualTable.getValue(2, "order_id")).isEqualTo(1);
		assertThat(actualTable.getValue(2, "goods_id")).isEqualTo(1);
		assertThat(actualTable.getValue(2, "quantity")).isEqualTo(2);
		assertThat(actualTable.getValue(2, "quantity_amount")).isEqualTo(20000);
		assertThat(actualTable.getValue(2, "update_data")).isNotNull();

		assertThat(actualTable.getValue(3, "order_details_id")).isNotNull();
		assertThat(actualTable.getValue(3, "order_id")).isEqualTo(1);
		assertThat(actualTable.getValue(3, "goods_id")).isEqualTo(2);
		assertThat(actualTable.getValue(3, "quantity")).isEqualTo(1);
		assertThat(actualTable.getValue(3, "quantity_amount")).isEqualTo(1000);
		assertThat(actualTable.getValue(3, "update_data")).isNotNull();
	}

	@Test
	@DisplayName("注文詳細作成_異常終了")
	public void createOrderDetails_NG() throws Exception {
		List<OrderDetails> orderDetailsList = new ArrayList<OrderDetails>();
		orderDetailsList.add(new OrderDetails(1, 1, 2, 20000));
		orderDetailsList.add(new OrderDetails(1, 2, 1, 1000));

		// リポジトリをnullに設定
		ReflectionTestUtils.setField(orderService, "orderDetailsRepository", null);

		// テスト実行
		Exception exception = assertThrows(Exception.class, () -> {
			orderService.createOrderDetails((long) 1, orderDetailsList);
		});

		assertThat(exception.getMessage()).contains("注文詳細の作成に失敗しました");
	}

	@Test
	@DisplayName("注文リスト取得_正常終了")
	public void getOrder_OK() throws Exception {
		// テスト実行
		List<OrderInformation> order = orderService.getOrder();

		// Assert order_information
		assertThat(order.get(0).getOrderId()).isEqualTo(1);
		assertThat(order.get(0).getAccountId()).isEqualTo(2);
		assertThat(order.get(0).getPostCode()).isEqualTo("3380015");
		assertThat(order.get(0).getAddress()).isEqualTo("東京都");
		assertThat(order.get(0).getTotalAmount()).isEqualTo(31000);
		assertThat(order.get(0).getOrderState()).isEqualTo(0);
		assertThat(order.get(0).getUpdateData()).isNotNull();

		// Assert order_details
		assertThat(order.get(0).getOrderDetails().get(0).getOrderDetailsId()).isEqualTo(1);
		assertThat(order.get(0).getOrderDetails().get(0).getOrderId()).isEqualTo(1);
		assertThat(order.get(0).getOrderDetails().get(0).getGoodsId()).isEqualTo(1);
		assertThat(order.get(0).getOrderDetails().get(0).getQuantity()).isEqualTo(2);
		assertThat(order.get(0).getOrderDetails().get(0).getQuantityAmount()).isEqualTo(20000);
		assertThat(order.get(0).getOrderDetails().get(0).getUpdateData()).isNotNull();

		// Assert order_details
		assertThat(order.get(0).getOrderDetails().get(1).getOrderDetailsId()).isEqualTo(2);
		assertThat(order.get(0).getOrderDetails().get(1).getOrderId()).isEqualTo(1);
		assertThat(order.get(0).getOrderDetails().get(1).getGoodsId()).isEqualTo(2);
		assertThat(order.get(0).getOrderDetails().get(1).getQuantity()).isEqualTo(1);
		assertThat(order.get(0).getOrderDetails().get(1).getQuantityAmount()).isEqualTo(11000);
		assertThat(order.get(0).getOrderDetails().get(1).getUpdateData()).isNotNull();
	}

	@Test
	@DisplayName("注文リスト取得_注文情報_異常終了")
	public void getOrder_NG1() throws Exception {

		// リポジトリをnullに設定
		ReflectionTestUtils.setField(orderService, "orderRepository", null);

		// テスト実行
		Exception exception = assertThrows(Exception.class, () -> {
			orderService.getOrder();
		});

		assertThat(exception.getMessage()).contains("注文の取得に失敗しました");
	}

	@Test
	@DisplayName("注文リスト取得_注文詳細_異常終了")
	public void getOrder_NG2() throws Exception {

		// リポジトリをnullに設定
		ReflectionTestUtils.setField(orderService, "orderDetailsRepository", null);

		// テスト実行
		Exception exception = assertThrows(Exception.class, () -> {
			orderService.getOrder();
		});

		assertThat(exception.getMessage()).contains("注文詳細の取得に失敗しました");
	}

	@Test
	@DisplayName("注文更新_正常終了")
	public void updateAccount_OK() throws Exception {

		// テスト実行
		orderService.updateOrder((long) 1, 1);

		// DB Assert
		ITable actualTable = dbUnitConnection.createDataSet().getTable("order_information");
		assertThat(actualTable.getRowCount()).isEqualTo(1);
		assertThat(actualTable.getValue(0, "order_id")).isNotNull();
		assertThat(actualTable.getValue(0, "account_id")).isEqualTo(2);
		assertThat(actualTable.getValue(0, "post_code")).isEqualTo("3380015");
		assertThat(actualTable.getValue(0, "address")).isEqualTo("東京都");
		assertThat(actualTable.getValue(0, "total_amount")).isEqualTo(31000);
		assertThat(actualTable.getValue(0, "order_state")).isEqualTo(1);
		assertThat(actualTable.getValue(0, "update_data")).isNotNull();
	}

	@Test
	@DisplayName("注文更新_異常終了")
	public void updateAccount_NG() throws Exception {

		// リポジトリをnullに設定
		ReflectionTestUtils.setField(orderService, "orderRepository", null);

		// テスト実行
		Exception exception = assertThrows(Exception.class, () -> {
			orderService.updateOrder((long) 0, 1);
		});

		assertThat(exception.getMessage()).contains("注文の更新に失敗しました");
	}

	@Test
	@DisplayName("注文チェック処理_チェック結果が問題なし")
	public void checkOrderData_OK() throws Exception {
		List<OrderDetails> orderDetailsList = new ArrayList<OrderDetails>();
		orderDetailsList.add(new OrderDetails(1, 1, 2, 20000));
		orderDetailsList.add(new OrderDetails(1, 2, 1, 1000));
		final OrderInformation orderInformation = new OrderInformation(1, "3380014", "埼玉県", 21000, 0, orderDetailsList);

		// テスト実行
		orderService.checkOrderData(orderInformation);
	}

	@Test
	@DisplayName("注文チェック処理_郵便番号が未存在")
	public void checkOrderData_NG1() throws Exception {
		List<OrderDetails> orderDetailsList = new ArrayList<OrderDetails>();
		orderDetailsList.add(new OrderDetails(1, 1, 2, 20000));
		orderDetailsList.add(new OrderDetails(1, 2, 1, 1000));
		final OrderInformation orderInformation = new OrderInformation(1, null, "埼玉県", 21000, 0, orderDetailsList);
		// テスト実行	
		Exception exception = assertThrows(Exception.class, () -> {
			orderService.checkOrderData(orderInformation);
		});

		// Assert
		assertThat(exception.getMessage()).contains("郵便番号が未入力です");
	}

	@Test
	@DisplayName("注文チェック処理_住所が未存在")
	public void checkOrderData_NG2() throws Exception {
		List<OrderDetails> orderDetailsList = new ArrayList<OrderDetails>();
		orderDetailsList.add(new OrderDetails(1, 1, 2, 20000));
		orderDetailsList.add(new OrderDetails(1, 2, 1, 1000));
		final OrderInformation orderInformation = new OrderInformation(1, "3380014", null, 21000, 0, orderDetailsList);
		// テスト実行	
		Exception exception = assertThrows(Exception.class, () -> {
			orderService.checkOrderData(orderInformation);
		});

		// Assert
		assertThat(exception.getMessage()).contains("住所が未入力です");
	}

	@Test
	@DisplayName("注文チェック処理_合計金額が未存在")
	public void checkOrderData_NG3() throws Exception {
		List<OrderDetails> orderDetailsList = new ArrayList<OrderDetails>();
		orderDetailsList.add(new OrderDetails(1, 1, 2, 20000));
		orderDetailsList.add(new OrderDetails(1, 2, 1, 1000));
		final OrderInformation orderInformation = new OrderInformation(1, "3380014", "埼玉県", 0, 0, orderDetailsList);
		// テスト実行	
		Exception exception = assertThrows(Exception.class, () -> {
			orderService.checkOrderData(orderInformation);
		});

		// Assert
		assertThat(exception.getMessage()).contains("合計金額が未入力です");
	}

	@Test
	@DisplayName("注文詳細チェック処理_チェック結果が問題なし")
	public void checkOrderDetailsListData_OK() throws Exception {
		List<OrderDetails> orderDetailsList = new ArrayList<OrderDetails>();
		orderDetailsList.add(new OrderDetails(1, 1, 2, 20000));
		orderDetailsList.add(new OrderDetails(1, 2, 1, 1000));

		// テスト実行
		orderService.checkOrderDetailsListData(orderDetailsList);
	}

	@Test
	@DisplayName("注文詳細チェック処理_商品が未存在")
	public void checkOrderDetailsListData_NG1() throws Exception {
		List<OrderDetails> orderDetailsList = new ArrayList<OrderDetails>();
		orderDetailsList.add(new OrderDetails(1, 0, 2, 20000));
		orderDetailsList.add(new OrderDetails(1, 2, 1, 1000));

		// テスト実行	
		Exception exception = assertThrows(Exception.class, () -> {
			orderService.checkOrderDetailsListData(orderDetailsList);
		});

		// Assert
		assertThat(exception.getMessage()).contains("商品ID:0=商品が未入力です");
	}

	@Test
	@DisplayName("注文詳細チェック処理_数量が未存在")
	public void checkOrderDetailsListData_NG2() throws Exception {
		List<OrderDetails> orderDetailsList = new ArrayList<OrderDetails>();
		orderDetailsList.add(new OrderDetails(1, 1, 0, 20000));
		orderDetailsList.add(new OrderDetails(1, 2, 1, 1000));

		// テスト実行	
		Exception exception = assertThrows(Exception.class, () -> {
			orderService.checkOrderDetailsListData(orderDetailsList);
		});

		// Assert
		assertThat(exception.getMessage()).contains("商品ID:1=数量が未入力です");
	}

	@Test
	@DisplayName("注文詳細チェック処理_数量合計金額が未存在")
	public void checkOrderDetailsListData_NG3() throws Exception {
		List<OrderDetails> orderDetailsList = new ArrayList<OrderDetails>();
		orderDetailsList.add(new OrderDetails(1, 1, 2, 0));
		orderDetailsList.add(new OrderDetails(1, 2, 1, 1000));

		// テスト実行	
		Exception exception = assertThrows(Exception.class, () -> {
			orderService.checkOrderDetailsListData(orderDetailsList);
		});

		// Assert
		assertThat(exception.getMessage()).contains("商品ID:1=数量合計金額が未入力です");
	}

	@Test
	@DisplayName("注文詳細チェック処理_複数行が未存在")
	public void checkOrderDetailsListData_NG4() throws Exception {
		List<OrderDetails> orderDetailsList = new ArrayList<OrderDetails>();
		orderDetailsList.add(new OrderDetails(1, 1, 0, 20000));
		orderDetailsList.add(new OrderDetails(1, 2, 1, 0));

		// テスト実行	
		Exception exception = assertThrows(Exception.class, () -> {
			orderService.checkOrderDetailsListData(orderDetailsList);
		});

		// Assert
		assertThat(exception.getMessage()).contains("商品ID:1=数量が未入力です\r\n商品ID:2=数量合計金額が未入力です");
	}

	@Test
	@DisplayName("商品購入情報作成更新処理_アカウントIDが未存在")
	public void createUpdateGoodsPurchase_OK1() throws Exception {
		List<OrderDetails> orderDetailsList = new ArrayList<OrderDetails>();
		orderDetailsList.add(new OrderDetails(1, 1, 2, 20000));

		// テスト実行
		orderService.createUpdateGoodsPurchase((long) 0, orderDetailsList);

		// DB Assert
		ITable actualTable = dbUnitConnection.createDataSet().getTable("goods_purchase");
		assertThat(actualTable.getRowCount()).isEqualTo(2);
		assertThat(actualTable.getValue(0, "goods_purchase_id")).isNotNull();
		assertThat(actualTable.getValue(0, "account_id")).isEqualTo(2);
		assertThat(actualTable.getValue(0, "goods_id")).isEqualTo(1);
		assertThat(actualTable.getValue(0, "purchas_number")).isEqualTo(2);
		assertThat(actualTable.getValue(0, "update_data")).isNotNull();
		assertThat(actualTable.getValue(0, "delete_data")).isNull();
		assertThat(actualTable.getValue(0, "delete_flag")).isEqualTo(false);

		assertThat(actualTable.getValue(1, "goods_purchase_id")).isNotNull();
		assertThat(actualTable.getValue(1, "account_id")).isEqualTo(2);
		assertThat(actualTable.getValue(1, "goods_id")).isEqualTo(2);
		assertThat(actualTable.getValue(1, "purchas_number")).isEqualTo(1);
		assertThat(actualTable.getValue(1, "update_data")).isNotNull();
		assertThat(actualTable.getValue(1, "delete_data")).isNull();
		assertThat(actualTable.getValue(1, "delete_flag")).isEqualTo(false);
	}

	@Test
	@DisplayName("商品購入情報作成更新処理_商品購入情報作成")
	public void createUpdateGoodsPurchase_OK2() throws Exception {
		List<OrderDetails> orderDetailsList = new ArrayList<OrderDetails>();
		orderDetailsList.add(new OrderDetails(1, 3, 4, 20000));

		// テスト実行
		orderService.createUpdateGoodsPurchase((long) 2, orderDetailsList);

		// DB Assert
		ITable actualTable = dbUnitConnection.createDataSet().getTable("goods_purchase");
		assertThat(actualTable.getRowCount()).isEqualTo(3);
		assertThat(actualTable.getValue(0, "goods_purchase_id")).isNotNull();
		assertThat(actualTable.getValue(0, "account_id")).isEqualTo(2);
		assertThat(actualTable.getValue(0, "goods_id")).isEqualTo(1);
		assertThat(actualTable.getValue(0, "purchas_number")).isEqualTo(2);
		assertThat(actualTable.getValue(0, "update_data")).isNotNull();
		assertThat(actualTable.getValue(0, "delete_data")).isNull();
		assertThat(actualTable.getValue(0, "delete_flag")).isEqualTo(false);

		assertThat(actualTable.getValue(1, "goods_purchase_id")).isNotNull();
		assertThat(actualTable.getValue(1, "account_id")).isEqualTo(2);
		assertThat(actualTable.getValue(1, "goods_id")).isEqualTo(2);
		assertThat(actualTable.getValue(1, "purchas_number")).isEqualTo(1);
		assertThat(actualTable.getValue(1, "update_data")).isNotNull();
		assertThat(actualTable.getValue(1, "delete_data")).isNull();
		assertThat(actualTable.getValue(1, "delete_flag")).isEqualTo(false);

		assertThat(actualTable.getValue(2, "goods_purchase_id")).isNotNull();
		assertThat(actualTable.getValue(2, "account_id")).isEqualTo(2);
		assertThat(actualTable.getValue(2, "goods_id")).isEqualTo(3);
		assertThat(actualTable.getValue(2, "purchas_number")).isEqualTo(4);
		assertThat(actualTable.getValue(2, "update_data")).isNotNull();
		assertThat(actualTable.getValue(2, "delete_data")).isNull();
		assertThat(actualTable.getValue(2, "delete_flag")).isEqualTo(false);
	}

	@Test
	@DisplayName("商品購入情報作成更新処理_商品購入情報更新")
	public void createUpdateGoodsPurchase_OK3() throws Exception {
		List<OrderDetails> orderDetailsList = new ArrayList<OrderDetails>();
		orderDetailsList.add(new OrderDetails(1, 2, 2, 20000));

		// テスト実行
		orderService.createUpdateGoodsPurchase((long) 2, orderDetailsList);

		// DB Assert
		ITable actualTable = dbUnitConnection.createDataSet().getTable("goods_purchase");
		assertThat(actualTable.getRowCount()).isEqualTo(2);
		assertThat(actualTable.getValue(0, "goods_purchase_id")).isNotNull();
		assertThat(actualTable.getValue(0, "account_id")).isEqualTo(2);
		assertThat(actualTable.getValue(0, "goods_id")).isEqualTo(1);
		assertThat(actualTable.getValue(0, "purchas_number")).isEqualTo(2);
		assertThat(actualTable.getValue(0, "update_data")).isNotNull();
		assertThat(actualTable.getValue(0, "delete_data")).isNull();
		assertThat(actualTable.getValue(0, "delete_flag")).isEqualTo(false);

		assertThat(actualTable.getValue(1, "goods_purchase_id")).isNotNull();
		assertThat(actualTable.getValue(1, "account_id")).isEqualTo(2);
		assertThat(actualTable.getValue(1, "goods_id")).isEqualTo(2);
		assertThat(actualTable.getValue(1, "purchas_number")).isEqualTo(3);
		assertThat(actualTable.getValue(1, "update_data")).isNotNull();
		assertThat(actualTable.getValue(1, "delete_data")).isNull();
		assertThat(actualTable.getValue(1, "delete_flag")).isEqualTo(false);
	}

	@Test
	@DisplayName("商品購入情報作成更新処理_複数作成更新")
	public void createUpdateGoodsPurchase_OK4() throws Exception {
		List<OrderDetails> orderDetailsList = new ArrayList<OrderDetails>();
		orderDetailsList.add(new OrderDetails(1, 3, 4, 20000));
		orderDetailsList.add(new OrderDetails(1, 2, 2, 20000));

		// テスト実行
		orderService.createUpdateGoodsPurchase((long) 2, orderDetailsList);

		// DB Assert
		ITable actualTable = dbUnitConnection.createDataSet().getTable("goods_purchase");
		assertThat(actualTable.getRowCount()).isEqualTo(3);
		assertThat(actualTable.getValue(0, "goods_purchase_id")).isNotNull();
		assertThat(actualTable.getValue(0, "account_id")).isEqualTo(2);
		assertThat(actualTable.getValue(0, "goods_id")).isEqualTo(1);
		assertThat(actualTable.getValue(0, "purchas_number")).isEqualTo(2);
		assertThat(actualTable.getValue(0, "update_data")).isNotNull();
		assertThat(actualTable.getValue(0, "delete_data")).isNull();
		assertThat(actualTable.getValue(0, "delete_flag")).isEqualTo(false);

		assertThat(actualTable.getValue(1, "goods_purchase_id")).isNotNull();
		assertThat(actualTable.getValue(1, "account_id")).isEqualTo(2);
		assertThat(actualTable.getValue(1, "goods_id")).isEqualTo(2);
		assertThat(actualTable.getValue(1, "purchas_number")).isEqualTo(3);
		assertThat(actualTable.getValue(1, "update_data")).isNotNull();
		assertThat(actualTable.getValue(1, "delete_data")).isNull();
		assertThat(actualTable.getValue(1, "delete_flag")).isEqualTo(false);

		assertThat(actualTable.getValue(2, "goods_purchase_id")).isNotNull();
		assertThat(actualTable.getValue(2, "account_id")).isEqualTo(2);
		assertThat(actualTable.getValue(2, "goods_id")).isEqualTo(3);
		assertThat(actualTable.getValue(2, "purchas_number")).isEqualTo(4);
		assertThat(actualTable.getValue(2, "update_data")).isNotNull();
		assertThat(actualTable.getValue(2, "delete_data")).isNull();
		assertThat(actualTable.getValue(2, "delete_flag")).isEqualTo(false);
	}

	@Test
	@DisplayName("商品購入情報作成更新処理_作成更新NG")
	public void createUpdateGoodsPurchase_NG() throws Exception {
		List<OrderDetails> orderDetailsList = new ArrayList<OrderDetails>();
		orderDetailsList.add(new OrderDetails(1, 3, 4, 20000));
		orderDetailsList.add(new OrderDetails(1, 2, 2, 20000));

		// リポジトリをnullに設定
		ReflectionTestUtils.setField(orderService, "goodsPurchaseRepository", null);

		// テスト実行	
		Exception exception = assertThrows(Exception.class, () -> {
			orderService.createUpdateGoodsPurchase((long) 2, orderDetailsList);
		});

		// Assert
		assertThat(exception.getMessage()).contains("商品購入情報の作成に失敗しました");
	}
}
