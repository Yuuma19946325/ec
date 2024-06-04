package com.example.ec.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.ec.Handler.ErrorResponse;
import com.example.ec.entity.GoodsView;
import com.example.ec.service.GoodsViewService;

/**
 * 商品閲覧コントローラー
 */
@RestController
@RequestMapping("/api/goodsView")
@CrossOrigin(origins = "http://localhost:5173")
public class GoodsViewController {

	@Autowired
	GoodsViewService goodsViewService;

	/**
	 * 商品閲覧作成
	 * @param goodsViewDetails 商品閲覧情報
	 * @return 完了レスポンス
	 * @throws Exception エラーレスポンス
	 */
	@PostMapping("")
	public ErrorResponse createGoodsView(@RequestBody GoodsView goodsViewDetails) throws Exception {

		// 商品閲覧情報チェック処理
		goodsViewService.checkGoodsViewData(goodsViewDetails);

		// 商品閲覧作成処理
		goodsViewService.createGoodsView(goodsViewDetails);

		// 完了レスポンスを返却
		return new ErrorResponse(
				HttpStatus.CREATED.value(),
				null);
	}

	/**
	 * 商品閲覧リスト取得
	 * @param accountId アカウントID
	 * @return アカウント情報
	 * @throws Exception エラーレスポンス
	 */
	@GetMapping("/list")
	public List<GoodsView> getGoodsViewList(@RequestParam(value = "accountId") Long accountId) throws Exception {

		// 商品閲覧リスト情報取得処理
		return goodsViewService.getGoodsViewList(accountId);
	}
}
