package com.example.ec.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.ec.Handler.ErrorResponse;
import com.example.ec.entity.OrderInformation;
import com.example.ec.service.OrderService;

/**
 * 注文コントローラー
 */
@RestController
@RequestMapping("/api/order")
@CrossOrigin(origins = "http://localhost:5173")
public class OrderController {

	@Autowired
	OrderService orderService;

	/**
	 * 注文作成
	 * @param accountDetails アカウント情報
	 * @return 完了レスポンス
	 * @throws Exception エラーレスポンス
	 */
	@PostMapping("")
	public ErrorResponse createOrder(@RequestBody OrderInformation order) throws Exception {

		// 注文情報チェック処理
		orderService.checkOrderData(order);

		// 注文詳細チェック処理
		orderService.checkOrderDetailsListData(order.getOrderDetails());

		// 注文作成処理
		OrderInformation returnOrder = orderService.createOrder(order);

		// 注文詳細作成処理
		orderService.createOrderDetails(returnOrder.getOrderId(), order.getOrderDetails());

		// 商品購入登録 or 商品購入更新処理
		orderService.createUpdateGoodsPurchase(order.getAccountId(), order.getOrderDetails());

		// 完了レスポンスを返却
		return new ErrorResponse(
				HttpStatus.CREATED.value(),
				null);
	}

	/**
	 * 注文リスト取得
	 * @return 完了レスポンス
	 * @throws Exception エラーレスポンス
	 */
	@GetMapping("/list")
	public List<OrderInformation> getOrderList() throws Exception {

		// 注文リストを返却
		return orderService.getOrder();
	}

	/**
	 * 注文更新
	 * @return 完了レスポンス
	 * @throws Exception エラーレスポンス
	 */
	@PutMapping("/{orderId}")
	public ErrorResponse updataOrder(@PathVariable(value = "orderId") Long orderId,
			@RequestParam(value = "orderState") int orderState) throws Exception {

		// 注文更新処理
		orderService.updateOrder(orderId, orderState);

		// 完了レスポンスを返却
		return new ErrorResponse(
				HttpStatus.OK.value(),
				null);
	}
}
