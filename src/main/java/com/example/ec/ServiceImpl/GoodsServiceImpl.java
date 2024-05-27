package com.example.ec.ServiceImpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.ec.Handler.BadRequestException;
import com.example.ec.Handler.SQLException;
import com.example.ec.entity.Goods;
import com.example.ec.repository.GoodsRepository;
import com.example.ec.service.GoodsService;

import io.micrometer.common.util.StringUtils;

@Service
public class GoodsServiceImpl implements GoodsService {

	@Autowired
	private GoodsRepository goodsRepository;

	/**
	 * 商品作成
	 * @param goodsDetails 商品情報
	 * @throws Exception エラーレスポンス
	 */
	@Override
	public void createGoods(Goods goodsDetails) throws Exception {
		goodsDetails.setUpdateDataNow();

		try {
			// DB→商品作成
			goodsRepository.save(goodsDetails);
		} catch (Exception e) {
			throw new SQLException("商品の作成に失敗しました", e);
		}
	}

	/**
	 * 商品リスト取得
	 * @return 商品リスト情報
	 * @throws Exception エラーレスポンス
	 */
	@Override
	public List<Goods> getGoodsList() throws Exception {
		// 商品情報リストインスタンスを作成
		List<Goods> goodsList = new ArrayList<Goods>();

		try {
			// DB→商品情報リスト取得
			goodsList = goodsRepository.findByGoodsList();
		} catch (Exception e) {
			throw new SQLException("商品の取得に失敗しました", e);
		}

		return goodsList;
	}

	/**
	 * 商品更新
	 * @param goodsId 商品ID
	 * @param goodsDetails 商品情報
	 * @throws Exception エラーレスポンス
	 */
	@Override
	public void updateGoods(Long goodsId, Goods goodsDetails) throws Exception {

		try {
			// 商品情報取得
			Goods goods = goodsRepository.findById(goodsId).get();

			// 商品情報を設定
			goods.setGoodsName(goodsDetails.getGoodsName());
			goods.setCategoryId(goodsDetails.getCategoryId());
			goods.setAmount(goodsDetails.getAmount());
			goods.setStock(goodsDetails.getStock());
			goods.setSet(goodsDetails.getSet());
			goods.setMaterial(goodsDetails.getMaterial());
			goods.setBrand(goodsDetails.getBrand());
			goods.setTheme(goodsDetails.getTheme());
			goods.setTarget(goodsDetails.getTarget());
			goods.setPoint(goodsDetails.getPoint());
			goods.setImage(goodsDetails.getImage());
			goods.setUpdateDataNow();

			// DB→商品更新
			goodsRepository.save(goods);
		} catch (Exception e) {
			throw new SQLException("商品の更新に失敗しました", e);
		}
	}

	/**
	 * 商品削除
	 * @param goodsId 商品ID
	 * @throws Exception エラーレスポンス
	 */
	@Override
	public void deleteGoods(Long goodsId) throws Exception {

		try {
			// 商品情報取得
			Goods goods = goodsRepository.findById(goodsId).get();

			// 商品情報を削除設定
			goods.setDelet();

			// DB→商品更新
			goodsRepository.save(goods);
		} catch (Exception e) {
			throw new SQLException("商品の削除に失敗しました", e);
		}
	}

	/**
	 * 商品情報チェック処理
	 * @param goodsDetails 商品情報
	 * @throws Exception エラーレスポンス
	 */
	@Override
	public void checkGoodsData(Goods goodsDetails) throws Exception {
		// 商品情報チェック処理
		String errorMessage = goodsDetails.checkGoodsData();
		// エラーメッセージが存在する場合
		if (StringUtils.isNotEmpty(errorMessage))
			// 処理を異常終了で終了
			throw new BadRequestException(errorMessage);
	}

}
