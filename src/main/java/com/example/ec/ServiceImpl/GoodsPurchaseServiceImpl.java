package com.example.ec.ServiceImpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.ec.Handler.SQLException;
import com.example.ec.entity.GoodsPurchase;
import com.example.ec.repository.GoodsPurchaseRepository;
import com.example.ec.service.GoodsPurchaseService;

@Service
public class GoodsPurchaseServiceImpl implements GoodsPurchaseService {

	@Autowired
	private GoodsPurchaseRepository goodsPurchaseRepository;

	/**
	 * 商品購入リスト取得
	 * @return 商品購入リスト情報
	 * @throws Exception エラーレスポンス
	 */
	@Override
	public List<GoodsPurchase> getGoodsPurchaseList(Long accountId) throws Exception {
		// 商品購入情報リストインスタンスを作成
		List<GoodsPurchase> goodsPurchaseList = new ArrayList<GoodsPurchase>();

		try {
			// DB→商品購入情報リスト取得
			goodsPurchaseList = goodsPurchaseRepository.findByGoodsPurchaseList(accountId);
		} catch (Exception e) {
			throw new SQLException("商品購入の取得に失敗しました", e);
		}

		return goodsPurchaseList;
	}

}
