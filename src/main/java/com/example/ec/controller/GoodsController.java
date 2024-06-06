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
import org.springframework.web.bind.annotation.RestController;

import com.example.ec.Handler.ErrorResponse;
import com.example.ec.entity.Goods;
import com.example.ec.service.GoodsService;

/**
 * 商品コントローラー
 */
@RestController
@RequestMapping("/api/goods")
public class GoodsController {

	@Autowired
	GoodsService goodsService;

	/**
	 * 商品作成
	 * @param goodsDetails 商品情報
	 * @return 完了レスポンス
	 * @throws Exception エラーレスポンス
	 */
	@PostMapping("")
	public ErrorResponse createGoods(@RequestBody Goods goodsDetails) throws Exception {

		// 商品情報チェック処理
		goodsService.checkGoodsData(goodsDetails);

		// 商品作成処理
		goodsService.createGoods(goodsDetails);

		// 完了レスポンスを返却
		return new ErrorResponse(
				HttpStatus.CREATED.value(),
				null);
	}

	/**
	 * 商品リスト取得
	 * @return 商品リスト情報
	 * @throws Exception エラーレスポンス
	 */
	@GetMapping("/list")
	public List<Goods> getGoodsList() throws Exception {

		// 商品リストを返却
		return goodsService.getGoodsList();
	}

	/**
	 * 商品更新
	 * @return 完了レスポンス
	 * @throws Exception エラーレスポンス
	 */
	@PutMapping("/{goodsId}")
	public ErrorResponse updataGoods(@PathVariable(value = "goodsId") Long goodsId,
			@RequestBody Goods goodsDetails) throws Exception {

		// 商品情報チェック処理
		goodsService.checkGoodsData(goodsDetails);

		// 商品更新処理
		goodsService.updateGoods(goodsId, goodsDetails);

		// 完了レスポンスを返却
		return new ErrorResponse(
				HttpStatus.OK.value(),
				null);
	}

	/**
	 * 商品削除
	 * @return 完了レスポンス
	 * @throws Exception エラーレスポンス
	 */
	@DeleteMapping("/{goodsId}")
	public ErrorResponse deleteGoods(@PathVariable(value = "goodsId") Long goodsId) throws Exception {

		// 商品削除処理
		goodsService.deleteGoods(goodsId);

		// 完了レスポンスを返却
		return new ErrorResponse(
				HttpStatus.OK.value(),
				null);
	}
}
