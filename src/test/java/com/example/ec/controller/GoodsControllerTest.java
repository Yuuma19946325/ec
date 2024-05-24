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
import com.example.ec.entity.Goods;
import com.example.ec.service.GoodsService;

/**
 * Goods APIのコントローラーテスト
 */
@WebMvcTest(GoodsController.class)
public class GoodsControllerTest extends CommonTest {

	@Autowired
	private MockMvc mvc;

	@MockBean
	private GoodsService goodsService;

	@Test
	@DisplayName("商品作成API_OK")
	public void createGoods_OK() throws Exception {

		final Goods goodsDetails = new Goods("商品名", 1, 1000, 10, 2,
				"ステンレス", "レフレム", "ブラックラビット", 18, 200);

		ErrorResponse errorResponse = new ErrorResponse(
				HttpStatus.CREATED.value(),
				null);

		// モックの設定
		doNothing().when(goodsService).checkGoodsData(goodsDetails);
		doNothing().when(goodsService).createGoods(goodsDetails);

		// テスト実行
		mvc.perform(MockMvcRequestBuilders.post("/api/goods")
				.content(asJsonString(goodsDetails))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().json(asJsonString(errorResponse)));

	}

	@Test
	@DisplayName("商品作成API_入力情報NG")
	public void createGoods_NG1() throws Exception {

		final Goods goodsDetails = new Goods(null, 1, 1000, 10, 2,
				"ステンレス", "レフレム", "ブラックラビット", 18, 200);

		ErrorResponse errorResponse = new ErrorResponse(
				HttpStatus.BAD_REQUEST.value(),
				"商品名が未入力です");

		// モックの設定
		doThrow(new BadRequestException("商品名が未入力です")).when(goodsService).checkGoodsData(goodsDetails);

		// テスト実行
		mvc.perform(MockMvcRequestBuilders.post("/api/goods")
				.content(asJsonString(goodsDetails))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(content().json(asJsonString(errorResponse)));

	}

	@Test
	@DisplayName("商品作成API_作成NG")
	public void createGoods_NG2() throws Exception {

		final Goods goodsDetails = new Goods(null, 1, 1000, 10, 2,
				"ステンレス", "レフレム", "ブラックラビット", 18, 200);

		ErrorResponse errorResponse = new ErrorResponse(
				HttpStatus.INTERNAL_SERVER_ERROR.value(),
				"商品の作成に失敗しました");

		// モックの設定
		doNothing().when(goodsService).checkGoodsData(goodsDetails);
		doThrow(new SQLException("商品の作成に失敗しました")).when(goodsService).createGoods(goodsDetails);

		// テスト実行
		mvc.perform(MockMvcRequestBuilders.post("/api/goods")
				.content(asJsonString(goodsDetails))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError())
				.andExpect(content().json(asJsonString(errorResponse)));
	}

	@Test
	@DisplayName("商品リスト取得API_0件")
	public void getGoodsList_OK1() throws Exception {

		List<Goods> goodsList = new ArrayList<Goods>();

		// モックの設定
		doReturn(goodsList).when(goodsService).getGoodsList();

		// テスト実行
		mvc.perform(MockMvcRequestBuilders.get("/api/goods/list")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().json(asJsonString(goodsList)));
	}

	@Test
	@DisplayName("商品リスト取得API_2件")
	public void getGoodsList_OK2() throws Exception {

		List<Goods> goodsList = new ArrayList<Goods>();
		goodsList.add(new Goods("テスト商品", 1, 1000, 10, 2,
				"ステンレス", "レフレム", "ブラックラビット", 18, 200));
		goodsList.add(new Goods("テスト商品2", 1, 1000, 10, 2,
				"ステンレス", "レフレム", "ブラックラビット", 18, 200));

		// モックの設定
		doReturn(goodsList).when(goodsService).getGoodsList();

		// テスト実行
		mvc.perform(MockMvcRequestBuilders.get("/api/goods/list")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().json(asJsonString(goodsList)));
	}

	@Test
	@DisplayName("商品リスト取得API_取得失敗NG")
	public void getCategoryList_NG() throws Exception {

		ErrorResponse errorResponse = new ErrorResponse(
				HttpStatus.INTERNAL_SERVER_ERROR.value(),
				"商品の取得に失敗しました");

		// モックの設定
		doThrow(new SQLException("商品の取得に失敗しました")).when(goodsService).getGoodsList();

		// テスト実行
		mvc.perform(MockMvcRequestBuilders.get("/api/goods/list")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError())
				.andExpect(content().json(asJsonString(errorResponse)));
	}

	@Test
	@DisplayName("商品更新API_OK")
	public void updataGoods_OK() throws Exception {

		final Long goodsId = (long) 1;

		final Goods goodsDetails = new Goods("テスト商品", 1, 1000, 10, 2,
				"ステンレス", "レフレム", "ブラックラビット", 18, 200);

		ErrorResponse errorResponse = new ErrorResponse(
				HttpStatus.OK.value(),
				null);

		// モックの設定
		doNothing().when(goodsService).checkGoodsData(goodsDetails);
		doNothing().when(goodsService).updateGoods(goodsId, goodsDetails);

		// テスト実行
		mvc.perform(MockMvcRequestBuilders.put("/api/goods/" + goodsId)
				.content(asJsonString(goodsDetails))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().json(asJsonString(errorResponse)));
	}

	@Test
	@DisplayName("商品更新API_入力情報NG")
	public void updataGoods_NG1() throws Exception {

		final Long goodsId = (long) 1;

		final Goods goodsDetails = new Goods(null, 1, 1000, 10, 2,
				"ステンレス", "レフレム", "ブラックラビット", 18, 200);

		ErrorResponse errorResponse = new ErrorResponse(
				HttpStatus.BAD_REQUEST.value(),
				"商品名が未入力です");

		// モックの設定
		doThrow(new BadRequestException("商品名が未入力です")).when(goodsService).checkGoodsData(goodsDetails);

		// テスト実行
		mvc.perform(MockMvcRequestBuilders.put("/api/goods/" + goodsId)
				.content(asJsonString(goodsDetails))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(content().json(asJsonString(errorResponse)));
	}

	@Test
	@DisplayName("商品更新API_更新NG")
	public void updataGoods_NG2() throws Exception {

		final Long goodsId = (long) 1;

		final Goods goodsDetails = new Goods("テスト商品", 1, 1000, 10, 2,
				"ステンレス", "レフレム", "ブラックラビット", 18, 200);

		ErrorResponse errorResponse = new ErrorResponse(
				HttpStatus.INTERNAL_SERVER_ERROR.value(),
				"商品の更新に失敗しました");

		// モックの設定
		doNothing().when(goodsService).checkGoodsData(goodsDetails);
		doThrow(new SQLException("商品の更新に失敗しました")).when(goodsService)
				.updateGoods(goodsId, goodsDetails);

		// テスト実行
		mvc.perform(MockMvcRequestBuilders.put("/api/goods/" + goodsId)
				.content(asJsonString(goodsDetails))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError())
				.andExpect(content().json(asJsonString(errorResponse)));
	}

	@Test
	@DisplayName("商品削除API_OK")
	public void deleteGoods_OK() throws Exception {

		final Long goodsId = (long) 1;

		ErrorResponse errorResponse = new ErrorResponse(
				HttpStatus.OK.value(),
				null);

		// モックの設定
		doNothing().when(goodsService).deleteGoods(goodsId);

		// テスト実行
		mvc.perform(MockMvcRequestBuilders.delete("/api/goods/" + goodsId)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().json(asJsonString(errorResponse)));

	}

	@Test
	@DisplayName("商品削除API_削除失敗")
	public void deleteGoods_NG() throws Exception {

		final Long goodsId = (long) 1;

		ErrorResponse errorResponse = new ErrorResponse(
				HttpStatus.INTERNAL_SERVER_ERROR.value(),
				"商品の削除に失敗しました");

		// モックの設定
		doThrow(new SQLException("商品の削除に失敗しました")).when(goodsService).deleteGoods(goodsId);

		// テスト実行
		mvc.perform(MockMvcRequestBuilders.delete("/api/goods/" + goodsId)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError())
				.andExpect(content().json(asJsonString(errorResponse)));

	}
}
