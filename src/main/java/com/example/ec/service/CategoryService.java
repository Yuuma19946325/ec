package com.example.ec.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.ec.entity.Category;

@Service
public interface CategoryService {

	// カテゴリ作成処理
	public void createCategory(Category categoryDetails) throws Exception;

	// カテゴリ情報リスト取得処理
	public List<Category> getCategoryList() throws Exception;

	// カテゴリ更新処理
	public void updateCategory(Long categoryId, Category categoryDetails) throws Exception;

	// カテゴリ停止処理
	public void deleteCategory(Long categoryId) throws Exception;

	// カテゴリ情報チェック処理
	public void checkCategoryData(Category categoryDetails) throws Exception;

	// カテゴリ存在チェック処理
	public void checkExistsCategory(Long categoryId, String categoryName) throws Exception;
}
