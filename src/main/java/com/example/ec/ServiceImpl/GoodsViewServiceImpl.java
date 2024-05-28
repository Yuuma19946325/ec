package com.example.ec.ServiceImpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.ec.Handler.BadRequestException;
import com.example.ec.Handler.SQLException;
import com.example.ec.entity.GoodsView;
import com.example.ec.repository.GoodsViewRepository;
import com.example.ec.service.GoodsViewService;

import io.micrometer.common.util.StringUtils;

@Service
public class GoodsViewServiceImpl implements GoodsViewService {

	@Autowired
	private GoodsViewRepository goodsViewRepository;

	/**
	 * 商品閲覧作成
	 * @param goodsViewDetails 商品閲覧情報
	 * @throws Exception エラーレスポンス
	 */
	@Override
	public void createGoodsView(GoodsView goodsViewDetails) throws Exception {
		goodsViewDetails.setUpdateDataNow();

		try {
			// DB→商品閲覧作成
			goodsViewRepository.save(goodsViewDetails);
		} catch (Exception e) {
			throw new SQLException("商品閲覧の作成に失敗しました", e);
		}
	}

	/**
	 * 商品閲覧リスト取得
	 * @param accountId アカウントID
	 * @return 商品閲覧リスト情報
	 * @throws Exception エラーレスポンス
	 */
	@Override
	public List<GoodsView> getGoodsViewList(Long accountId) throws Exception {
		// 商品閲覧情報リストインスタンスを作成
		List<GoodsView> goodsViewList = new ArrayList<GoodsView>();

		try {
			// DB→商品閲覧情報リスト取得
			goodsViewList = goodsViewRepository.findByGoodsViewList(accountId);
		} catch (Exception e) {
			throw new SQLException("商品閲覧の取得に失敗しました", e);
		}

		return goodsViewList;
	}

	/**
	 * 商品閲覧情報チェック処理
	 * @param goodsViewDetails 商品閲覧情報
	 * @throws Exception エラーレスポンス
	 */
	@Override
	public void checkGoodsViewData(GoodsView goodsViewDetails) throws Exception {
		// 商品閲覧情報チェック処理
		String errorMessage = goodsViewDetails.checkGoodsViewData();
		// エラーメッセージが存在する場合
		if (StringUtils.isNotEmpty(errorMessage))
			// 処理を異常終了で終了
			throw new BadRequestException(errorMessage);

	}

}
