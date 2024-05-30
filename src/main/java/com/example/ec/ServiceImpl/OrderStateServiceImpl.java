package com.example.ec.ServiceImpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.ec.Handler.SQLException;
import com.example.ec.entity.OrderState;
import com.example.ec.repository.OrderStateRepository;
import com.example.ec.service.OrderStateService;

@Service
public class OrderStateServiceImpl implements OrderStateService {

	@Autowired
	private OrderStateRepository orderStateRepository;

	/**
	 * 注文状態取得
	 * @return 注文状態情報
	 * @throws Exception エラーレスポンス
	 */
	@Override
	public List<OrderState> getOrderState() throws Exception {
		// 注文状態インスタンスを作成
		List<OrderState> orderStateList = new ArrayList<OrderState>();

		try {
			// DB→アカウント情報取得
			orderStateList = orderStateRepository.findAll();
		} catch (Exception e) {
			throw new SQLException("注文状態の取得に失敗しました", e);
		}

		return orderStateList;
	}

}
