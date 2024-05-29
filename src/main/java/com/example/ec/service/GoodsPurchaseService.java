package com.example.ec.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.ec.entity.GoodsPurchase;

@Service
public interface GoodsPurchaseService {

	// 商品購入情報リスト取得処理
	public List<GoodsPurchase> getGoodsPurchaseList(Long accountId) throws Exception;
}
