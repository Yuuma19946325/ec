package com.example.ec.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.ec.entity.Cart;

@Service
public interface CartService {

	// カート作成処理
	public void createCart(Cart cartDetails) throws Exception;

	// カート情報リスト取得処理
	public List<Cart> getCartList(Long accountId) throws Exception;

	// カート更新処理
	public void updateCart(Long cartId, Cart cartDetails) throws Exception;

	// カート削除処理
	public void deleteCart(Long cartId) throws Exception;

	// カート情報チェック処理
	public void checkCartData(Cart cartDetails) throws Exception;
}
