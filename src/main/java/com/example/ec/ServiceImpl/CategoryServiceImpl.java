package com.example.ec.ServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.ec.Handler.BadRequestException;
import com.example.ec.Handler.SQLException;
import com.example.ec.entity.Category;
import com.example.ec.repository.CategoryRepository;
import com.example.ec.service.CategoryService;

import io.micrometer.common.util.StringUtils;

@Service
public class CategoryServiceImpl implements CategoryService {

	@Autowired
	private CategoryRepository categoryRepository;

	/**
	 * カテゴリ作成
	 * @param categoryDetails カテゴリ情報
	 * @throws Exception エラーレスポンス
	 */
	@Override
	public void createCategory(Category categoryDetails) throws Exception {
		categoryDetails.setUpdateDataNow();

		try {
			// DB→カテゴリ作成
			categoryRepository.save(categoryDetails);
		} catch (Exception e) {
			throw new SQLException("カテゴリの作成に失敗しました", e);
		}
	}

	/**
	 * カテゴリリスト取得
	 * @return カテゴリリスト情報
	 * @throws Exception エラーレスポンス
	 */
	@Override
	public List<Category> getCategoryList() throws Exception {
		// カテゴリ情報リストインスタンスを作成
		List<Category> categoryList = new ArrayList<Category>();

		try {
			// DB→カテゴリ情報リスト取得
			categoryList = categoryRepository.findByCategoryList();
		} catch (Exception e) {
			throw new SQLException("カテゴリの取得に失敗しました", e);
		}

		return categoryList;
	}

	/**
	 * カテゴリ更新
	 * @param categoryId カテゴリID
	 * @param categoryDetails カテゴリ情報
	 */
	@Override
	public void updateCategory(Long categoryId, Category categoryDetails) throws Exception {
		try {
			// DB→カテゴリ情報取得
			Category category = categoryRepository.findById(categoryId).get();

			// カテゴリ情報を設定
			category.setCategoryName(categoryDetails.getCategoryName());
			category.setUpdateDataNow();

			// カテゴリ情報の削除フラグがfalseの場合、カテゴリ情報を復活させる
			if (!categoryDetails.isDeleteFlag())
				category.setNotDelet();

			// DB→カテゴリ更新
			categoryRepository.save(category);
		} catch (Exception e) {
			throw new SQLException("カテゴリの更新に失敗しました", e);
		}
	}

	/**
	 * カテゴリ削除
	 * @param categoryId カテゴリID
	 */
	@Override
	public void deleteCategory(Long categoryId) throws Exception {
		try {
			// DB→カテゴリ情報取得
			Category category = categoryRepository.findById(categoryId).get();

			// カテゴリ情報を削除設定
			category.setDelet();

			// DB→カテゴリ更新
			categoryRepository.save(category);
		} catch (Exception e) {
			throw new SQLException("カテゴリの停止に失敗しました", e);
		}
	}

	/**
	 * カテゴリ情報チェック処理
	 * @param categoryDetails カテゴリ情報
	 * @throws Exception エラーレスポンス
	 */
	@Override
	public void checkCategoryData(Category categoryDetails) throws Exception {
		// カテゴリ情報チェック処理
		String errorMessage = categoryDetails.checkCategoryData();
		// エラーメッセージが存在する場合
		if (StringUtils.isNotEmpty(errorMessage))
			// 処理を異常終了で終了
			throw new BadRequestException(errorMessage);
	}

	/**
	 * カテゴリ存在チェック処理
	 * @param categoryId カテゴリID
	 * @param categoryDetails カテゴリ情報
	 * @throws Exception エラーレスポンス
	 */
	@Override
	public void checkExistsCategory(Long categoryId, Category categoryDetails) throws Exception {
		int categoryNameCount = 0;

		try {
			// カテゴリIDのnullチェック
			if (Objects.isNull(categoryId)) {
				// カテゴリIDがnullの場合(カテゴリが新規作成の場合)

				// DB→カテゴリ情報取得
				categoryNameCount = categoryRepository.findByCategoryName(categoryDetails.getCategoryName());
			} else {
				// カテゴリIDがnull以外の場合(カテゴリが更新の場合)

				// DB→カテゴリ情報取得
				categoryNameCount = categoryRepository.findByCategoryIdAndCategoryName(categoryId,
						categoryDetails.getCategoryName());
			}
		} catch (Exception e) {

			throw new SQLException("カテゴリの取得に失敗しました", e);
		}

		if (categoryNameCount > 0)
			throw new SQLException("このカテゴリ名は既に存在しています");

	}
}
