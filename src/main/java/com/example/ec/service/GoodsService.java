package com.example.ec.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.ec.entity.Goods;

@Service
public interface GoodsService {

	// 商品作成処理
	public void createGoods(Goods goodsDetails) throws Exception;

	// 商品情報リスト取得処理
	public List<Goods> getGoodsList() throws Exception;

	// 商品更新処理
	public void updateGoods(Long goodsId, Goods goodsDetails) throws Exception;

	// 商品削除処理
	public void deleteGoods(Long goodsId) throws Exception;

	// 商品情報チェック処理
	public void checkGoodsData(Goods goodsDetails) throws Exception;
}
