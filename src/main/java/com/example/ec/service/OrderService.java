package com.example.ec.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.ec.entity.OrderInformation;
import com.example.ec.entity.OrderDetails;

@Service
public interface OrderService {

	// 注文作成処理
	public OrderInformation createOrder(OrderInformation order) throws Exception;

	// 注文詳細
	public void createOrderDetails(Long orderId, List<OrderDetails> orderDetailsList) throws Exception;

	// 注文情報取得処理
	public List<OrderInformation> getOrder() throws Exception;

	// 注文更新処理
	public void updateOrder(Long orderId, int orderState) throws Exception;

	// 注文情報チェック処理
	public void checkOrderData(OrderInformation order) throws Exception;

	// 注文詳細リストチェック処理
	public void checkOrderDetailsListData(List<OrderDetails> orderDetailsList) throws Exception;

	// 商品購入登録 or 商品購入更新処理
	public void createUpdateGoodsPurchase(Long accountId, List<OrderDetails> orderDetailsList) throws Exception;
}
