package com.example.ec.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.ec.Handler.ErrorResponse;
import com.example.ec.entity.Cart;
import com.example.ec.service.CartService;

/**
 * カートコントローラー
 */
@RestController
@RequestMapping("/api/cart")
public class CartController {

	@Autowired
	CartService cartService;

	/**
	 * カート作成
	 * @param cartDetails カート情報
	 * @return 完了レスポンス
	 * @throws Exception エラーレスポンス
	 */
	@PostMapping("")
	public ErrorResponse createCart(@RequestBody Cart cartDetails) throws Exception {

		// カート情報チェック処理
		cartService.checkCartData(cartDetails);

		// カート作成処理
		cartService.createCart(cartDetails);

		// 完了レスポンスを返却
		return new ErrorResponse(
				HttpStatus.CREATED.value(),
				null);
	}

	/**
	 * カートリスト取得
	 * @return 完了レスポンス
	 * @throws Exception エラーレスポンス
	 */
	@GetMapping("/list")
	public List<Cart> getCartList(@RequestParam(value = "accountId") Long accountId) throws Exception {

		// カートリストを返却
		return cartService.getCartList(accountId);
	}

	/**
	 * カート更新
	 * @return 完了レスポンス
	 * @throws Exception エラーレスポンス
	 */
	@PutMapping("/{cartId}")
	public ErrorResponse updataCart(@PathVariable(value = "cartId") Long cartId,
			@RequestBody Cart cartDetails) throws Exception {

		// カート情報チェック処理
		cartService.checkCartData(cartDetails);

		// カート更新処理
		cartService.updateCart(cartId, cartDetails);

		// 完了レスポンスを返却
		return new ErrorResponse(
				HttpStatus.OK.value(),
				null);
	}

	/**
	 * カート削除
	 * @return 完了レスポンス
	 * @throws Exception エラーレスポンス
	 */
	@DeleteMapping("/{cartId}")
	public ErrorResponse deleteCart(@PathVariable(value = "cartId") Long cartId) throws Exception {

		// カート削除処理
		cartService.deleteCart(cartId);

		// 完了レスポンスを返却
		return new ErrorResponse(
				HttpStatus.OK.value(),
				null);
	}
}
