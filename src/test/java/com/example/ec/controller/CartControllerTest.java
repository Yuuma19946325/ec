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
import com.example.ec.entity.Cart;
import com.example.ec.service.CartService;

/**
 * Cart APIのコントローラーテスト
 */
@WebMvcTest(CartController.class)
public class CartControllerTest extends CommonTest {

	@Autowired
	private MockMvc mvc;

	@MockBean
	private CartService cartService;

	@Test
	@DisplayName("カート作成API_OK")
	public void createAccount_OK() throws Exception {

		final Cart cartDetails = new Cart((long) 1, (long) 10, 3, 3000);

		ErrorResponse errorResponse = new ErrorResponse(
				HttpStatus.CREATED.value(),
				null);

		// モックの設定
		doNothing().when(cartService).checkCartData(cartDetails);
		doNothing().when(cartService).createCart(cartDetails);

		// テスト実行
		mvc.perform(MockMvcRequestBuilders.post("/api/cart")
				.content(asJsonString(cartDetails))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().json(asJsonString(errorResponse)));

	}

	@Test
	@DisplayName("カート作成API_入力情報NG")
	public void createAccount_NG1() throws Exception {

		final Cart cartDetails = new Cart((long) 0, (long) 10, 3, 3000);

		ErrorResponse errorResponse = new ErrorResponse(
				HttpStatus.BAD_REQUEST.value(),
				"アカウントが未入力です");

		// モックの設定
		doThrow(new BadRequestException("アカウントが未入力です")).when(cartService).checkCartData(cartDetails);

		// テスト実行
		mvc.perform(MockMvcRequestBuilders.post("/api/cart")
				.content(asJsonString(cartDetails))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(content().json(asJsonString(errorResponse)));

	}

	@Test
	@DisplayName("カート作成API_作成NG")
	public void createAccount_NG2() throws Exception {

		final Cart cartDetails = new Cart((long) 1, (long) 10, 3, 3000);

		ErrorResponse errorResponse = new ErrorResponse(
				HttpStatus.INTERNAL_SERVER_ERROR.value(),
				"カートの作成に失敗しました");

		// モックの設定
		doNothing().when(cartService).checkCartData(cartDetails);
		doThrow(new SQLException("カートの作成に失敗しました")).when(cartService).createCart(cartDetails);

		// テスト実行
		mvc.perform(MockMvcRequestBuilders.post("/api/cart")
				.content(asJsonString(cartDetails))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError())
				.andExpect(content().json(asJsonString(errorResponse)));

	}

	@Test
	@DisplayName("カートリスト取得API_0件")
	public void getCartList_OK1() throws Exception {

		Long accountId = (long) 1;
		List<Cart> cartList = new ArrayList<Cart>();

		// モックの設定
		doReturn(cartList).when(cartService).getCartList(accountId);

		// テスト実行
		mvc.perform(MockMvcRequestBuilders.get("/api/cart/list?accountId=" + accountId)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().json(asJsonString(cartList)));
	}

	@Test
	@DisplayName("カートリスト取得API_2件")
	public void getCartList_OK2() throws Exception {

		Long accountId = (long) 1;
		List<Cart> cartList = new ArrayList<Cart>();
		cartList.add(new Cart((long) 1, (long) 10, 3, 3000));
		cartList.add(new Cart((long) 1, (long) 20, 2, 6000));

		// モックの設定
		doReturn(cartList).when(cartService).getCartList(accountId);

		// テスト実行
		mvc.perform(MockMvcRequestBuilders.get("/api/cart/list?accountId=" + accountId)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().json(asJsonString(cartList)));
	}

	@Test
	@DisplayName("カートリスト取得API_取得失敗NG")
	public void getCartList_NG() throws Exception {

		Long accountId = (long) 1;
		ErrorResponse errorResponse = new ErrorResponse(
				HttpStatus.INTERNAL_SERVER_ERROR.value(),
				"カートの取得に失敗しました");

		// モックの設定
		doThrow(new SQLException("カートの取得に失敗しました")).when(cartService).getCartList(accountId);

		// テスト実行
		mvc.perform(MockMvcRequestBuilders.get("/api/cart/list?accountId=" + accountId)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError())
				.andExpect(content().json(asJsonString(errorResponse)));
	}

	@Test
	@DisplayName("カート更新API_OK")
	public void updataCart_OK() throws Exception {

		final Long cartId = (long) 1;

		final Cart cartDetails = new Cart((long) 1, (long) 10, 3, 3000);

		ErrorResponse errorResponse = new ErrorResponse(
				HttpStatus.OK.value(),
				null);

		// モックの設定
		doNothing().when(cartService).checkCartData(cartDetails);
		doNothing().when(cartService).updateCart(cartId, cartDetails);

		// テスト実行
		mvc.perform(MockMvcRequestBuilders.put("/api/cart/" + cartId)
				.content(asJsonString(cartDetails))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().json(asJsonString(errorResponse)));
	}

	@Test
	@DisplayName("カート更新API_入力情報NG")
	public void updataCart_NG1() throws Exception {

		final Long cartId = (long) 1;

		final Cart cartDetails = new Cart((long) 1, (long) 10, 3, 3000);

		ErrorResponse errorResponse = new ErrorResponse(
				HttpStatus.BAD_REQUEST.value(),
				"アカウントが未入力です");

		// モックの設定
		doThrow(new BadRequestException("アカウントが未入力です")).when(cartService).checkCartData(cartDetails);

		// テスト実行
		mvc.perform(MockMvcRequestBuilders.put("/api/cart/" + cartId)
				.content(asJsonString(cartDetails))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(content().json(asJsonString(errorResponse)));
	}

	@Test
	@DisplayName("カート更新API_更新NG")
	public void updataCart_NG2() throws Exception {

		final Long cartId = (long) 1;

		final Cart cartDetails = new Cart((long) 1, (long) 10, 3, 3000);

		ErrorResponse errorResponse = new ErrorResponse(
				HttpStatus.INTERNAL_SERVER_ERROR.value(),
				"カートの更新に失敗しました");

		// モックの設定
		doNothing().when(cartService).checkCartData(cartDetails);
		doThrow(new SQLException("カートの更新に失敗しました")).when(cartService)
				.updateCart(cartId, cartDetails);

		// テスト実行
		mvc.perform(MockMvcRequestBuilders.put("/api/cart/" + cartId)
				.content(asJsonString(cartDetails))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError())
				.andExpect(content().json(asJsonString(errorResponse)));
	}

	@Test
	@DisplayName("カート削除API_OK")
	public void deleteCart_OK() throws Exception {

		final Long cartId = (long) 1;

		ErrorResponse errorResponse = new ErrorResponse(
				HttpStatus.OK.value(),
				null);

		// モックの設定
		doNothing().when(cartService).deleteCart(cartId);

		// テスト実行
		mvc.perform(MockMvcRequestBuilders.delete("/api/cart/" + cartId)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().json(asJsonString(errorResponse)));

	}

	@Test
	@DisplayName("カート削除API_削除失敗")
	public void deleteCart_NG() throws Exception {

		final Long cartId = (long) 1;

		ErrorResponse errorResponse = new ErrorResponse(
				HttpStatus.INTERNAL_SERVER_ERROR.value(),
				"カートの削除に失敗しました");

		// モックの設定
		doThrow(new SQLException("カートの削除に失敗しました")).when(cartService).deleteCart(cartId);

		// テスト実行
		mvc.perform(MockMvcRequestBuilders.delete("/api/cart/" + cartId)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError())
				.andExpect(content().json(asJsonString(errorResponse)));

	}
}
