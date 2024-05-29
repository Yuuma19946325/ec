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
import com.example.ec.entity.GoodsPurchase;
import com.example.ec.service.GoodsPurchaseService;

/**
 * GoodsPurchase APIのコントローラーテスト
 */
@WebMvcTest(GoodsPurchaseController.class)
public class GoodsPurchaseControllerTest extends CommonTest {

	@Autowired
	private MockMvc mvc;

	@MockBean
	GoodsPurchaseService goodsPurchaseService;

	@Test
	@DisplayName("商品購入リスト取得API_0件")
	public void getGoodsPurchaseList_OK1() throws Exception {

		Long accountId = (long) 1;
		List<GoodsPurchase> goodsPurchaseList = new ArrayList<GoodsPurchase>();

		// モックの設定
		doReturn(goodsPurchaseList).when(goodsPurchaseService).getGoodsPurchaseList(accountId);

		// テスト実行
		mvc.perform(MockMvcRequestBuilders.get("/api/goodsPurchase/list?accountId=" + accountId)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().json(asJsonString(goodsPurchaseList)));
	}

	@Test
	@DisplayName("商品購入リスト取得API_2件")
	public void getGoodsPurchaseList_OK2() throws Exception {

		Long accountId = (long) 1;
		List<GoodsPurchase> goodsPurchaseList = new ArrayList<GoodsPurchase>();
		goodsPurchaseList.add(new GoodsPurchase((long) 1, (long) 1, 3));
		goodsPurchaseList.add(new GoodsPurchase((long) 1, (long) 2, 4));

		// モックの設定
		doReturn(goodsPurchaseList).when(goodsPurchaseService).getGoodsPurchaseList(accountId);

		// テスト実行
		mvc.perform(MockMvcRequestBuilders.get("/api/goodsPurchase/list?accountId=" + accountId)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().json(asJsonString(goodsPurchaseList)));
	}

	@Test
	@DisplayName("商品購入リスト取得API_取得失敗NG")
	public void getGoodsPurchaseList_NG() throws Exception {

		Long accountId = (long) 1;
		ErrorResponse errorResponse = new ErrorResponse(
				HttpStatus.INTERNAL_SERVER_ERROR.value(),
				"商品購入の取得に失敗しました");

		// モックの設定
		doThrow(new SQLException("商品購入の取得に失敗しました")).when(goodsPurchaseService).getGoodsPurchaseList(accountId);

		// テスト実行
		mvc.perform(MockMvcRequestBuilders.get("/api/goodsPurchase/list?accountId=" + accountId)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError())
				.andExpect(content().json(asJsonString(errorResponse)));
	}
}
