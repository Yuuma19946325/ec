package com.example.ec.ServiceImpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.ec.Handler.BadRequestException;
import com.example.ec.Handler.SQLException;
import com.example.ec.entity.Cart;
import com.example.ec.entity.Goods;
import com.example.ec.repository.CartRepository;
import com.example.ec.repository.GoodsRepository;
import com.example.ec.service.CartService;

import io.micrometer.common.util.StringUtils;

@Service
public class CartServiceImpl implements CartService {

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private GoodsRepository goodsRepository;

	/**
	 * カート作成
	 * @param cartDetails カート情報
	 * @throws Exception エラーレスポンス
	 */
	@Override
	public void createCart(Cart cartDetails) throws Exception {
		try {
			// DB→カート作成
			cartRepository.save(cartDetails);
		} catch (Exception e) {
			throw new SQLException("カートの作成に失敗しました", e);
		}
	}

	/**
	 * カートリスト取得
	 * @param accountId アカウントID
	 * @return カートリスト情報
	 * @throws Exception エラーレスポンス
	 */
	@Override
	public List<Cart> getCartList(Long accountId) throws Exception {
		// カート情報リストインスタンスを作成
		List<Cart> cartList = new ArrayList<Cart>();

		try {
			// DB→カート情報リスト取得
			cartList = cartRepository.findByCartList(accountId);
		} catch (Exception e) {
			throw new SQLException("カートの取得に失敗しました", e);
		}
		return cartList;
	}

	/**
	 * カート更新
	 * @param cartId カートID
	 * @param cartDetails カート情報
	 * @throws Exception エラーレスポンス
	 */
	@Override
	public void updateCart(Long cartId, Cart cartDetails) throws Exception {
		try {
			// カート情報取得
			Cart cart = cartRepository.findById(cartId).get();

			// カート情報を設定
			cart.setQuantity(cartDetails.getQuantity());
			cart.setTotalAmount(cartDetails.getTotalAmount());

			// DB→カート更新
			cartRepository.save(cart);
		} catch (Exception e) {
			throw new SQLException("カートの更新に失敗しました", e);
		}

	}

	/**
	 * カート削除
	 * @param cartId カートID
	 * @throws Exception エラーレスポンス
	 */
	@Override
	public void deleteCart(Long cartId) throws Exception {
		try {
			// DB→カート削除
			cartRepository.deleteById(cartId);
		} catch (Exception e) {
			throw new SQLException("カートの削除に失敗しました", e);
		}
	}

	/**
	 * カートチェック処理
	 * @param cartDetails カート情報
	 * @throws Exception エラーレスポンス
	 */
	@Override
	public void checkCartData(Cart cartDetails) throws Exception {

		// エラーメッセージ格納先
		String errorMessage = null;

		// カート情報チェック処理
		errorMessage = cartDetails.checkCartData();
		// エラーメッセージが存在する場合
		if (StringUtils.isNotEmpty(errorMessage))
			// 処理を異常終了で終了
			throw new BadRequestException(errorMessage);

		// 商品情報インスタンスを作成
		Goods goods = new Goods();
		try {
			// 商品情報取得
			goods = goodsRepository.findById(cartDetails.getGoodsId()).get();
		} catch (Exception e) {
			throw new SQLException("商品が未存在です", e);
		}

		// 数量合計金額チェック処理
		errorMessage = cartDetails.checkTotalAmount(goods.getAmount());
		// エラーメッセージが存在する場合
		if (StringUtils.isNotEmpty(errorMessage))
			// 処理を異常終了で終了
			throw new BadRequestException(errorMessage);
	}

}
