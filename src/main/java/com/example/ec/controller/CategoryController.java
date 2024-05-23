package com.example.ec.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ec.Handler.ErrorResponse;
import com.example.ec.entity.Category;
import com.example.ec.service.CategoryService;

/**
 * カテゴリコントローラー
 */
@RestController
@RequestMapping("/api/category")
public class CategoryController {

	@Autowired
	CategoryService categoryService;

	/**
	 * カテゴリ作成
	 * @param categoryDetails カテゴリ情報
	 * @return 完了レスポンス
	 * @throws Exception エラーレスポンス
	 */
	@PostMapping("")
	public ErrorResponse createCategory(@RequestBody Category categoryDetails) throws Exception {

		// カテゴリ情報チェック処理
		categoryService.checkCategoryData(categoryDetails);

		// カテゴリ存在チェック処理
		categoryService.checkExistsCategory(null, categoryDetails.getCategoryName());

		// カテゴリ作成処理
		categoryService.createCategory(categoryDetails);

		// 完了レスポンスを返却
		return new ErrorResponse(
				HttpStatus.CREATED.value(),
				null);
	}

	/**
	 * カテゴリリスト取得
	 * @return カテゴリリスト情報
	 * @throws Exception エラーレスポンス
	 */
	@GetMapping("/list")
	public List<Category> getCategoryList() throws Exception {

		// カテゴリリスト情報取得処理
		return categoryService.getCategoryList();
	}

	/**
	 * カテゴリ更新
	 * @param categoryDetails カテゴリ情報
	 * @return 完了レスポンス
	 * @throws Exception エラーレスポンス
	 */
	@PutMapping("/{categoryId}")
	public ErrorResponse updataCategory(@PathVariable(value = "categoryId") Long categoryId,
			@RequestBody Category categoryDetails) throws Exception {

		// カテゴリ情報チェック処理
		categoryService.checkCategoryData(categoryDetails);

		// カテゴリ存在チェック処理
		if (!categoryDetails.isDeleteFlag())
			categoryService.checkExistsCategory(categoryId, categoryDetails.getCategoryName());

		// カテゴリ更新処理
		categoryService.updateCategory(categoryId, categoryDetails);

		// 完了レスポンスを返却
		return new ErrorResponse(
				HttpStatus.OK.value(),
				null);
	}

	/**
	 * カテゴリ停止
	 * @param categoryId カテゴリID
	 * @return 完了レスポンス
	 * @throws Exception エラーレスポンス
	 */
	@DeleteMapping("/{categoryId}")
	public ErrorResponse deleteCategory(@PathVariable(value = "categoryId") Long categoryId) throws Exception {

		// カテゴリ削除処理
		categoryService.deleteCategory(categoryId);

		// 完了レスポンスを返却
		return new ErrorResponse(
				HttpStatus.OK.value(),
				null);
	}
}
