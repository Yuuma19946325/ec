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
import com.example.ec.Handler.ErrorResponse;
import com.example.ec.Handler.SQLException;
import com.example.ec.entity.OrderState;
import com.example.ec.service.OrderStateService;

/**
 * OrderState APIのコントローラーテスト
 */
@WebMvcTest(OrderStateController.class)
public class OrderStateControllerTest extends CommonTest {

	@Autowired
	private MockMvc mvc;

	@MockBean
	OrderStateService orderStateService;

	@Test
	@DisplayName("注文状態取得API")
	public void getOrderState_OK() throws Exception {

		List<OrderState> orderState = new ArrayList<OrderState>();

		// モックの設定
		doReturn(orderState).when(orderStateService).getOrderState();
		orderState.add(new OrderState(0, "注文中"));
		orderState.add(new OrderState(1, "振込済み"));
		orderState.add(new OrderState(2, "発注済み"));
		orderState.add(new OrderState(9, "注文キャンセル(お店事情)"));

		// テスト実行
		mvc.perform(MockMvcRequestBuilders.get("/api/orderState")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().json(asJsonString(orderState)));
	}

	@Test
	@DisplayName("注文状態取得API_取得失敗NG")
	public void getOrderState_NG() throws Exception {

		ErrorResponse errorResponse = new ErrorResponse(
				HttpStatus.INTERNAL_SERVER_ERROR.value(),
				"注文状態の取得に失敗しました");

		// モックの設定
		doThrow(new SQLException("注文状態の取得に失敗しました")).when(orderStateService).getOrderState();

		// テスト実行
		mvc.perform(MockMvcRequestBuilders.get("/api/orderState")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError())
				.andExpect(content().json(asJsonString(errorResponse)));
	}
}
