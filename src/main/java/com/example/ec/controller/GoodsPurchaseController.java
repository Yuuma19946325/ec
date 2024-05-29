package com.example.ec.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.ec.entity.GoodsPurchase;
import com.example.ec.service.GoodsPurchaseService;

/**
 * 商品購入コントローラー
 */
@RestController
@RequestMapping("/api/goodsPurchase")
public class GoodsPurchaseController {

	@Autowired
	GoodsPurchaseService goodsPurchaseService;

	/**
	 * 商品購入リスト取得
	 * @return 完了レスポンス
	 * @throws Exception エラーレスポンス
	 */
	@GetMapping("/list")
	public List<GoodsPurchase> getGoodsPurchaseList(@RequestParam(value = "accountId") Long accountId)
			throws Exception {

		// カートリストを返却
		return goodsPurchaseService.getGoodsPurchaseList(accountId);
	}
}
