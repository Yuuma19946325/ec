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
import com.example.ec.entity.GoodsView;
import com.example.ec.service.GoodsViewService;

/**
 * GoodsView APIのコントローラーテスト
 */
@WebMvcTest(GoodsViewController.class)
public class GoodsViewControllerTest extends CommonTest {

	@Autowired
	private MockMvc mvc;

	@MockBean
	GoodsViewService goodsViewService;

	@Test
	@DisplayName("商品閲覧作成API_OK")
	public void createGoodsView_OK() throws Exception {

		final GoodsView goodsViewDetails = new GoodsView((long) 1, (long) 2);

		ErrorResponse errorResponse = new ErrorResponse(
				HttpStatus.CREATED.value(),
				null);

		// モックの設定
		doNothing().when(goodsViewService).checkGoodsViewData(goodsViewDetails);
		doNothing().when(goodsViewService).createGoodsView(goodsViewDetails);

		// テスト実行
		mvc.perform(MockMvcRequestBuilders.post("/api/goodsView")
				.content(asJsonString(goodsViewDetails))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().json(asJsonString(errorResponse)));

	}

	@Test
	@DisplayName("商品閲覧作成API_入力情報NG")
	public void createGoodsView_NG1() throws Exception {

		final GoodsView goodsViewDetails = new GoodsView((long) 0, (long) 2);

		ErrorResponse errorResponse = new ErrorResponse(
				HttpStatus.BAD_REQUEST.value(),
				"アカウントが未入力です");

		// モックの設定
		doThrow(new BadRequestException("アカウントが未入力です")).when(goodsViewService).checkGoodsViewData(goodsViewDetails);

		// テスト実行
		mvc.perform(MockMvcRequestBuilders.post("/api/goodsView")
				.content(asJsonString(goodsViewDetails))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(content().json(asJsonString(errorResponse)));

	}

	@Test
	@DisplayName("商品閲覧作成API_作成NG")
	public void createGoodsView_NG2() throws Exception {

		final GoodsView goodsViewDetails = new GoodsView((long) 1, (long) 2);

		ErrorResponse errorResponse = new ErrorResponse(
				HttpStatus.INTERNAL_SERVER_ERROR.value(),
				"商品閲覧の作成に失敗しました");

		// モックの設定
		doNothing().when(goodsViewService).checkGoodsViewData(goodsViewDetails);
		doThrow(new SQLException("商品閲覧の作成に失敗しました")).when(goodsViewService).createGoodsView(goodsViewDetails);

		// テスト実行
		mvc.perform(MockMvcRequestBuilders.post("/api/goodsView")
				.content(asJsonString(goodsViewDetails))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError())
				.andExpect(content().json(asJsonString(errorResponse)));

	}

	@Test
	@DisplayName("商品閲覧リスト取得API_0件")
	public void getGoodsViewList_OK1() throws Exception {
		Long accountId = (long) 1;
		List<GoodsView> goodsViewList = new ArrayList<GoodsView>();

		// モックの設定
		doReturn(goodsViewList).when(goodsViewService).getGoodsViewList(accountId);

		// テスト実行
		mvc.perform(MockMvcRequestBuilders.get("/api/goodsView/list?accountId=" + accountId)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().json(asJsonString(goodsViewList)));
	}

	@Test
	@DisplayName("商品閲覧リスト取得API_2件")
	public void getGoodsViewList_OK2() throws Exception {
		Long accountId = (long) 1;
		List<GoodsView> goodsViewList = new ArrayList<GoodsView>();
		goodsViewList.add(new GoodsView((long) 1, (long) 2));
		goodsViewList.add(new GoodsView((long) 1, (long) 3));

		// モックの設定
		doReturn(goodsViewList).when(goodsViewService).getGoodsViewList(accountId);

		// テスト実行
		mvc.perform(MockMvcRequestBuilders.get("/api/goodsView/list?accountId=" + accountId)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().json(asJsonString(goodsViewList)));
	}

	@Test
	@DisplayName("商品閲覧リスト取得API_取得失敗NG")
	public void getGoodsViewList_NG() throws Exception {
		Long accountId = (long) 1;
		ErrorResponse errorResponse = new ErrorResponse(
				HttpStatus.INTERNAL_SERVER_ERROR.value(),
				"商品閲覧の取得に失敗しました");

		// モックの設定
		doThrow(new SQLException("商品閲覧の取得に失敗しました")).when(goodsViewService).getGoodsViewList(accountId);

		// テスト実行
		mvc.perform(MockMvcRequestBuilders.get("/api/goodsView/list?accountId=" + accountId)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError())
				.andExpect(content().json(asJsonString(errorResponse)));
	}
}
