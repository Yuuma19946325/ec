package com.example.ec.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ec.entity.OrderState;
import com.example.ec.service.OrderStateService;

/**
 * 注文状態コントローラー
 */
@RestController
@RequestMapping("/api/orderState")
public class OrderStateController {

	@Autowired
	OrderStateService orderStateService;

	/**
	 * 注文状態取得
	 * @return 注文状態情報
	 * @throws Exception エラーレスポンス
	 */
	@GetMapping("")
	public List<OrderState> getOrderState() throws Exception {

		// 注文状態情報取得処理
		return orderStateService.getOrderState();
	}
}
