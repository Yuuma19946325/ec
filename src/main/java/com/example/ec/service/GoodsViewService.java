package com.example.ec.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.ec.entity.GoodsView;

@Service
public interface GoodsViewService {

	// 商品閲覧作成処理
	public void createGoodsView(GoodsView goodsViewDetails) throws Exception;

	// 商品閲覧情報リスト取得処理
	public List<GoodsView> getGoodsViewList(Long accountId) throws Exception;

	// 商品閲覧情報チェック処理
	public void checkGoodsViewData(GoodsView goodsViewDetails) throws Exception;
}
