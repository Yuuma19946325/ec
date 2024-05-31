package com.example.ec.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.example.ec.CommonTest;
import com.example.ec.Handler.BadRequestException;
import com.example.ec.Handler.ErrorResponse;
import com.example.ec.Handler.SQLException;
import com.example.ec.entity.OrderDetails;
import com.example.ec.entity.OrderInformation;
import com.example.ec.service.OrderService;

/**
 * Order APIのコントローラーテスト
 */
@WebMvcTest(OrderController.class)
public class OrderControllerTest extends CommonTest {

	@Autowired
	private MockMvc mvc;

	@MockBean
	OrderService orderService;

	@Test
	@DisplayName("注文作成作成API_OK")
	public void createOrder_OK() throws Exception {
		List<OrderDetails> orderDetailsList = new ArrayList<OrderDetails>();
		orderDetailsList.add(new OrderDetails(1, 1, 2, 20000));
		orderDetailsList.add(new OrderDetails(1, 2, 1, 1000));
		final OrderInformation orderInformation = new OrderInformation(1, "3380014", "埼玉県", 21000, 0, orderDetailsList);

		ErrorResponse errorResponse = new ErrorResponse(
				HttpStatus.CREATED.value(),
				null);

		// モックの設定
		doNothing().when(orderService).checkOrderData(orderInformation);
		doNothing().when(orderService).checkOrderDetailsListData(orderInformation.getOrderDetails());
		doReturn(orderInformation).when(orderService).createOrder(orderInformation);
		doNothing().when(orderService).createOrderDetails((long) 1, orderInformation.getOrderDetails());
		doNothing().when(orderService).createUpdateGoodsPurchase(orderInformation.getAccountId(), orderDetailsList);

		// テスト実行;
		mvc.perform(MockMvcRequestBuilders.post("/api/order")
				.content(asJsonString(orderInformation))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().json(asJsonString(errorResponse)));

	}

	@Test
	@DisplayName("注文作成作成API_注文情報_入力情報NG")
	public void createOrder_NG1() throws Exception {
		List<OrderDetails> orderDetailsList = new ArrayList<OrderDetails>();
		orderDetailsList.add(new OrderDetails(1, 1, 2, 20000));
		orderDetailsList.add(new OrderDetails(1, 2, 1, 1000));
		final OrderInformation orderInformation = new OrderInformation(1, null, "埼玉県", 21000, 0, orderDetailsList);

		ErrorResponse errorResponse = new ErrorResponse(
				HttpStatus.BAD_REQUEST.value(),
				"郵便番号が未入力です");

		// モックの設定
		doThrow(new BadRequestException("郵便番号が未入力です")).when(orderService).checkOrderData(orderInformation);

		// テスト実行;
		mvc.perform(MockMvcRequestBuilders.post("/api/order")
				.content(asJsonString(orderInformation))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(content().json(asJsonString(errorResponse)));

	}

	@Test
	@DisplayName("注文作成作成API_注文詳細_入力情報NG")
	public void createOrder_NG2() throws Exception {
		List<OrderDetails> orderDetailsList = new ArrayList<OrderDetails>();
		orderDetailsList.add(new OrderDetails(1, 1, 0, 20000));
		orderDetailsList.add(new OrderDetails(1, 2, 1, 1000));
		final OrderInformation orderInformation = new OrderInformation(1, "3380014", "埼玉県", 21000, 0, orderDetailsList);

		ErrorResponse errorResponse = new ErrorResponse(
				HttpStatus.BAD_REQUEST.value(),
				"商品ID:1=数量が未入力です");

		// モックの設定
		doNothing().when(orderService).checkOrderData(orderInformation);
		doThrow(new BadRequestException("商品ID:1=数量が未入力です")).when(orderService)
				.checkOrderDetailsListData(orderInformation.getOrderDetails());

		// テスト実行;
		mvc.perform(MockMvcRequestBuilders.post("/api/order")
				.content(asJsonString(orderInformation))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(content().json(asJsonString(errorResponse)));

	}

	@Test
	@DisplayName("注文作成作成API_注文情報_作成NG")
	public void createOrder_NG3() throws Exception {
		List<OrderDetails> orderDetailsList = new ArrayList<OrderDetails>();
		orderDetailsList.add(new OrderDetails(1, 1, 2, 20000));
		orderDetailsList.add(new OrderDetails(1, 2, 1, 1000));
		final OrderInformation orderInformation = new OrderInformation(1, "3380014", "埼玉県", 21000, 0, orderDetailsList);

		ErrorResponse errorResponse = new ErrorResponse(
				HttpStatus.INTERNAL_SERVER_ERROR.value(),
				"注文の作成に失敗しました");

		// モックの設定
		doNothing().when(orderService).checkOrderData(orderInformation);
		doNothing().when(orderService).checkOrderDetailsListData(orderInformation.getOrderDetails());
		doThrow(new SQLException("注文の作成に失敗しました")).when(orderService).createOrder(orderInformation);

		// テスト実行;
		mvc.perform(MockMvcRequestBuilders.post("/api/order")
				.content(asJsonString(orderInformation))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError())
				.andExpect(content().json(asJsonString(errorResponse)));

	}

	@Test
	@DisplayName("注文作成作成API_注文詳細_作成NG")
	public void createOrder_NG4() throws Exception {
		List<OrderDetails> orderDetailsList = new ArrayList<OrderDetails>();
		orderDetailsList.add(new OrderDetails(1, 1, 2, 20000));
		orderDetailsList.add(new OrderDetails(1, 2, 1, 1000));
		final OrderInformation orderInformation = new OrderInformation(1, "3380014", "埼玉県", 21000, 0, orderDetailsList);

		ErrorResponse errorResponse = new ErrorResponse(
				HttpStatus.INTERNAL_SERVER_ERROR.value(),
				"注文詳細の作成に失敗しました");

		// モックの設定
		doNothing().when(orderService).checkOrderData(orderInformation);
		doNothing().when(orderService).checkOrderDetailsListData(orderInformation.getOrderDetails());
		doReturn(orderInformation).when(orderService).createOrder(orderInformation);
		doThrow(new SQLException("注文詳細の作成に失敗しました")).when(orderService).createOrderDetails(null,
				orderInformation.getOrderDetails());

		// テスト実行;
		mvc.perform(MockMvcRequestBuilders.post("/api/order")
				.content(asJsonString(orderInformation))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError())
				.andExpect(content().json(asJsonString(errorResponse)));

	}

	@Test
	@DisplayName("注文作成作成API_商品購入情報_作成NG")
	public void createOrder_NG5() throws Exception {
		List<OrderDetails> orderDetailsList = new ArrayList<OrderDetails>();
		orderDetailsList.add(new OrderDetails(1, 1, 2, 20000));
		orderDetailsList.add(new OrderDetails(1, 2, 1, 1000));
		final OrderInformation orderInformation = new OrderInformation(1, "3380014", "埼玉県", 21000, 0, orderDetailsList);

		ErrorResponse errorResponse = new ErrorResponse(
				HttpStatus.INTERNAL_SERVER_ERROR.value(),
				"商品購入情報の作成に失敗しました");

		// モックの設定
		doNothing().when(orderService).checkOrderData(orderInformation);
		doNothing().when(orderService).checkOrderDetailsListData(orderInformation.getOrderDetails());
		doReturn(orderInformation).when(orderService).createOrder(orderInformation);
		doNothing().when(orderService).createOrderDetails((long) 1, orderInformation.getOrderDetails());
		doThrow(new SQLException("商品購入情報の作成に失敗しました")).when(orderService)
				.createUpdateGoodsPurchase(orderInformation.getAccountId(), orderInformation.getOrderDetails());

		// テスト実行;
		mvc.perform(MockMvcRequestBuilders.post("/api/order")
				.content(asJsonString(orderInformation))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError())
				.andExpect(content().json(asJsonString(errorResponse)));

	}

	@Test
	@DisplayName("注文リスト取得API_OK")
	public void getOrderList_OK() throws Exception {
		List<OrderDetails> orderDetailsList = new ArrayList<OrderDetails>();
		orderDetailsList.add(new OrderDetails(1, 1, 2, 20000));
		orderDetailsList.add(new OrderDetails(1, 2, 1, 1000));
		List<OrderInformation> orderInformationList = new ArrayList<OrderInformation>();
		orderInformationList.add(new OrderInformation(1, "3380014", "埼玉県", 21000, 0, orderDetailsList));

		// モックの設定
		doReturn(orderInformationList).when(orderService).getOrder();

		// テスト実行;
		mvc.perform(MockMvcRequestBuilders.get("/api/order/list")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().json(asJsonString(orderInformationList)));

	}

	@Test
	@DisplayName("注文リスト取得API_注文情報取得_NG")
	public void getOrderList_NG1() throws Exception {

		ErrorResponse errorResponse = new ErrorResponse(
				HttpStatus.INTERNAL_SERVER_ERROR.value(),
				"注文の取得に失敗しました");

		// モックの設定
		doThrow(new SQLException("注文の取得に失敗しました")).when(orderService).getOrder();

		// テスト実行;
		mvc.perform(MockMvcRequestBuilders.get("/api/order/list")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError())
				.andExpect(content().json(asJsonString(errorResponse)));

	}

	@Test
	@DisplayName("注文リスト取得API_注文詳細取得_NG")
	public void getOrderList_NG2() throws Exception {

		ErrorResponse errorResponse = new ErrorResponse(
				HttpStatus.INTERNAL_SERVER_ERROR.value(),
				"注文詳細の取得に失敗しました");

		// モックの設定
		doThrow(new SQLException("注文詳細の取得に失敗しました")).when(orderService).getOrder();

		// テスト実行;
		mvc.perform(MockMvcRequestBuilders.get("/api/order/list")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError())
				.andExpect(content().json(asJsonString(errorResponse)));

	}

	@Test
	@DisplayName("注文更新API_OK")
	public void updataOrder_OK() throws Exception {
		final long orderId = 1;
		final int orderState = 2;

		ErrorResponse errorResponse = new ErrorResponse(
				HttpStatus.OK.value(),
				null);

		// モックの設定
		doNothing().when(orderService).updateOrder(orderId, orderState);

		// テスト実行
		mvc.perform(MockMvcRequestBuilders.put("/api/order/" + orderId + "?orderState=" + orderState)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().json(asJsonString(errorResponse)));

	}

	@Test
	@DisplayName("注文更新API_更新NG")
	public void updataOrder_NG() throws Exception {
		final long orderId = 1;
		final int orderState = 2;

		ErrorResponse errorResponse = new ErrorResponse(
				HttpStatus.INTERNAL_SERVER_ERROR.value(),
				"注文の更新に失敗しました");

		// モックの設定
		doThrow(new SQLException("注文の更新に失敗しました")).when(orderService).updateOrder(orderId, orderState);

		// テスト実行
		mvc.perform(MockMvcRequestBuilders.put("/api/order/" + orderId + "?orderState=" + orderState)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError())
				.andExpect(content().json(asJsonString(errorResponse)));

	}
}
