package com.example.ec.ServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.ec.Handler.BadRequestException;
import com.example.ec.Handler.SQLException;
import com.example.ec.entity.GoodsPurchase;
import com.example.ec.entity.OrderDetails;
import com.example.ec.entity.OrderInformation;
import com.example.ec.repository.GoodsPurchaseRepository;
import com.example.ec.repository.OrderDetailsRepository;
import com.example.ec.repository.OrderInformationRepository;
import com.example.ec.service.OrderService;

import io.micrometer.common.util.StringUtils;

@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private OrderInformationRepository orderRepository;

	@Autowired
	private OrderDetailsRepository orderDetailsRepository;

	@Autowired
	private GoodsPurchaseRepository goodsPurchaseRepository;

	/**
	 * 注文作成
	 * @param order 注文情報
	 * @throws Exception エラーレスポンス
	 */
	@Override
	public OrderInformation createOrder(OrderInformation order) throws Exception {

		OrderInformation returnOrder = new OrderInformation();

		order.setUpdateDataNow();
		order.setOrder();

		try {
			// DB→注文作成
			returnOrder = orderRepository.save(order);
		} catch (Exception e) {
			throw new SQLException("注文の作成に失敗しました", e);
		}

		return returnOrder;
	}

	/**
	 * 注文詳細作成
	 * @param order 注文情報
	 * @throws Exception エラーレスポンス
	 */
	@Override
	public void createOrderDetails(Long orderId, List<OrderDetails> orderDetailsList) throws Exception {

		try {
			// 注文詳細分ループ処理
			for (OrderDetails orderDetails : orderDetailsList) {
				orderDetails.setOrderId(orderId);
				orderDetails.setUpdateDataNow();

				// DB→注文詳細作成
				orderDetailsRepository.save(orderDetails);
			}
		} catch (Exception e) {
			throw new SQLException("注文詳細の作成に失敗しました", e);
		}
	}

	/**
	 * 注文リスト取得
	 * @return 注文リスト情報
	 * @throws Exception エラーレスポンス
	 */
	@Override
	public List<OrderInformation> getOrder() throws Exception {
		// 注文情報リストインスタンスを作成
		List<OrderInformation> orderList = new ArrayList<OrderInformation>();

		try {
			// DB→注文情報リスト取得
			orderList = orderRepository.findAll();
		} catch (Exception e) {
			throw new SQLException("注文の取得に失敗しました", e);
		}

		try {
			// DB→注文詳細情報リスト取得
			for (OrderInformation order : orderList) {
				// DB→注文詳細情報リスト取得
				List<OrderDetails> orderDetailsList = orderDetailsRepository.findByOrderId(order.getOrderId());
				// 注文詳細情報リストを設定
				order.setOrderDetails(orderDetailsList);
			}
		} catch (Exception e) {
			throw new SQLException("注文詳細の取得に失敗しました", e);
		}

		return orderList;
	}

	/**
	 * 注文更新
	 * @param orderHistoryId 注文履歴ID
	 * @param orderState 注文状態
	 * @throws Exception エラーレスポンス
	 */
	@Override
	public void updateOrder(Long orderId, int orderState) throws Exception {
		try {
			// 注文情報取得
			OrderInformation order = orderRepository.findById(orderId).get();

			// 注文情報を設定
			order.setOrderState(orderState);

			// DB→注文更新
			orderRepository.save(order);
		} catch (Exception e) {
			throw new SQLException("注文の更新に失敗しました", e);
		}
	}

	/**
	 * 注文チェック処理
	 * @param order 注文情報
	 * @throws Exception エラーレスポンス
	 */
	@Override
	public void checkOrderData(OrderInformation order) throws Exception {
		// エラーメッセージ格納先
		String errorMessage = null;

		// カート情報チェック処理
		errorMessage = order.checkOrderData();
		// エラーメッセージが存在する場合
		if (StringUtils.isNotEmpty(errorMessage))
			// 処理を異常終了で終了
			throw new BadRequestException(errorMessage);
	}

	/**
	 * 注文詳細チェック処理
	 * @param orderDetails 注文情報
	 * @throws Exception エラーレスポンス
	 */
	@Override
	public void checkOrderDetailsListData(List<OrderDetails> orderDetailsList) throws Exception {
		// エラーメッセージ格納先
		List<String> errorMessageList = new ArrayList<String>();

		// 注文詳細分ループ処理
		for (OrderDetails orderDetails : orderDetailsList) {
			// 注文詳細情報チェック処理
			String errorMessage = orderDetails.checkOrderDetailsData();
			if (StringUtils.isNotEmpty(errorMessage))
				errorMessageList.add(errorMessage);
		}

		// エラーメッセージが存在する場合
		if (0 != errorMessageList.size())
			// 処理を異常終了で終了
			throw new BadRequestException(String.join("\r\n", errorMessageList));
	}

	/**
	 * 商品購入情報作成更新処理
	 * @param accountId アカウントID
	 * @param orderDetailsList 注文詳細リスト
	 * @throws Exception エラーレスポンス
	 */
	@Override
	public void createUpdateGoodsPurchase(Long accountId, List<OrderDetails> orderDetailsList) throws Exception {

		// アカウントIDが未存在の場合、処理を終了する
		if (0 == accountId)
			return;

		try {

			// 注文詳細分ループ処理
			for (OrderDetails orderDetails : orderDetailsList) {
				// 商品購入取得
				GoodsPurchase goodsPurchase = goodsPurchaseRepository.findByGoodsPurchase(accountId,
						orderDetails.getGoodsId());

				// 商品購入の存在判定
				if (Objects.isNull(goodsPurchase)) {
					// 未存在の場合、新規作成処理
					GoodsPurchase goodsPurchaseDetails = new GoodsPurchase();

					goodsPurchaseDetails.setAccountId(accountId);
					goodsPurchaseDetails.setGoodsId(orderDetails.getGoodsId());
					goodsPurchaseDetails.setPurchasNumber(orderDetails.getQuantity());
					goodsPurchaseDetails.setUpdateDataNow();

					// DB→商品購入作成
					goodsPurchaseRepository.save(goodsPurchaseDetails);
				} else {
					// 存在の場合、更新処理

					// 現在の購入回数に数量を足す
					goodsPurchase.setPurchasNumber(goodsPurchase.getPurchasNumber() + orderDetails.getQuantity());

					// DB→商品購入更新
					goodsPurchaseRepository.save(goodsPurchase);
				}
			}

		} catch (Exception e) {
			throw new SQLException("商品購入情報の作成に失敗しました", e);
		}

	}

}
