package com.example.ec.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.ec.entity.OrderState;

@Service
public interface OrderStateService {

	// 注文状態情報取得処理
	public List<OrderState> getOrderState() throws Exception;
}
