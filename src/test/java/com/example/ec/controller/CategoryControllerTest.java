package com.example.ec.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.example.ec.CommonTest;
import com.example.ec.Handler.BadRequestException;
import com.example.ec.Handler.ErrorResponse;
import com.example.ec.Handler.SQLException;
import com.example.ec.entity.Category;
import com.example.ec.service.CategoryService;

/**
 * Category APIのコントローラーテスト
 */
@WebMvcTest(CategoryController.class)
public class CategoryControllerTest extends CommonTest {

	@Autowired
	private MockMvc mvc;

	@MockBean
	private CategoryService categoryService;

	@Test
	@DisplayName("カテゴリ作成API_OK")
	public void createAccount_OK() throws Exception {

		final Category category = new Category("ネックレス");

		ErrorResponse errorResponse = new ErrorResponse(
				HttpStatus.CREATED.value(),
				null);

		// モックの設定
		doNothing().when(categoryService).checkCategoryData(category);
		doNothing().when(categoryService).checkExistsCategory(null, category);
		doNothing().when(categoryService).createCategory(category);

		// テスト実行
		mvc.perform(MockMvcRequestBuilders.post("/api/category")
				.content(asJsonString(category))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().json(asJsonString(errorResponse)));

	}

	@Test
	@DisplayName("カテゴリ作成API_入力情報NG")
	public void createAccount_NG1() throws Exception {

		final Category category = new Category();

		ErrorResponse errorResponse = new ErrorResponse(
				HttpStatus.BAD_REQUEST.value(),
				"カテゴリ名が未入力です");

		// モックの設定
		doThrow(new BadRequestException("カテゴリ名が未入力です")).when(categoryService).checkCategoryData(category);

		// テスト実行
		mvc.perform(MockMvcRequestBuilders.post("/api/category")
				.content(asJsonString(category))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(content().json(asJsonString(errorResponse)));

	}

	@Test
	@DisplayName("カテゴリ作成API_存在チェック取得NG")
	public void createAccount_NG2() throws Exception {

		final Category category = new Category("ネックレス");

		ErrorResponse errorResponse = new ErrorResponse(
				HttpStatus.INTERNAL_SERVER_ERROR.value(),
				"カテゴリの取得に失敗しました");

		// モックの設定
		doNothing().when(categoryService).checkCategoryData(category);
		doThrow(new SQLException("カテゴリの取得に失敗しました")).when(categoryService)
				.checkExistsCategory(null, category);

		// テスト実行
		mvc.perform(MockMvcRequestBuilders.post("/api/category")
				.content(asJsonString(category))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError())
				.andExpect(content().json(asJsonString(errorResponse)));

	}

	@Test
	@DisplayName("カテゴリ作成API_存在チェックNG")
	public void createAccount_NG3() throws Exception {

		final Category category = new Category("ネックレス");

		ErrorResponse errorResponse = new ErrorResponse(
				HttpStatus.INTERNAL_SERVER_ERROR.value(),
				"このカテゴリ名は既に存在しています");

		// モックの設定
		doNothing().when(categoryService).checkCategoryData(category);
		doThrow(new SQLException("このカテゴリ名は既に存在しています")).when(categoryService)
				.checkExistsCategory(null, category);

		// テスト実行
		mvc.perform(MockMvcRequestBuilders.post("/api/category")
				.content(asJsonString(category))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError())
				.andExpect(content().json(asJsonString(errorResponse)));

	}

	@Test
	@DisplayName("カテゴリ作成API_作成NG")
	public void createAccount_NG4() throws Exception {

		final Category category = new Category("ネックレス");

		ErrorResponse errorResponse = new ErrorResponse(
				HttpStatus.INTERNAL_SERVER_ERROR.value(),
				"カテゴリの作成に失敗しました");

		// モックの設定
		doNothing().when(categoryService).checkCategoryData(category);
		doNothing().when(categoryService).checkExistsCategory(null, category);
		doThrow(new SQLException("カテゴリの作成に失敗しました")).when(categoryService)
				.createCategory(category);

		// テスト実行
		mvc.perform(MockMvcRequestBuilders.post("/api/category")
				.content(asJsonString(category))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError())
				.andExpect(content().json(asJsonString(errorResponse)));

	}

	@Test
	@DisplayName("カテゴリリスト取得API_0件")
	public void getCategoryList_OK1() throws Exception {

		List<Category> categoryList = new ArrayList<Category>();

		// モックの設定
		doReturn(categoryList).when(categoryService).getCategoryList();

		// テスト実行
		mvc.perform(MockMvcRequestBuilders.get("/api/category/list")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().json(asJsonString(categoryList)));
	}

	@Test
	@DisplayName("カテゴリリスト取得API_2件")
	public void getCategoryList_OK2() throws Exception {

		List<Category> categoryList = new ArrayList<Category>();
		categoryList.add(new Category("ネックレス"));
		categoryList.add(new Category("ピアス"));

		// モックの設定
		doReturn(categoryList).when(categoryService).getCategoryList();

		// テスト実行
		mvc.perform(MockMvcRequestBuilders.get("/api/category/list")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().json(asJsonString(categoryList)));
	}

	@Test
	@DisplayName("カテゴリリスト取得API_取得失敗NG")
	public void getCategoryList_NG() throws Exception {

		ErrorResponse errorResponse = new ErrorResponse(
				HttpStatus.INTERNAL_SERVER_ERROR.value(),
				"カテゴリの取得に失敗しました");

		// モックの設定
		doThrow(new SQLException("カテゴリの取得に失敗しました")).when(categoryService).getCategoryList();

		// テスト実行
		mvc.perform(MockMvcRequestBuilders.get("/api/category/list")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError())
				.andExpect(content().json(asJsonString(errorResponse)));
	}

	@Test
	@DisplayName("カテゴリリスト更新API_削除フラグなし_OK")
	public void updataCategory_OK1() throws Exception {

		final Long categoryId = (long) 1;

		final Category category = new Category("ネックレス");

		ErrorResponse errorResponse = new ErrorResponse(
				HttpStatus.OK.value(),
				null);

		// モックの設定
		doNothing().when(categoryService).checkCategoryData(category);
		doNothing().when(categoryService).checkExistsCategory(categoryId, category);
		doNothing().when(categoryService).updateCategory(categoryId, category);

		// テスト実行
		mvc.perform(MockMvcRequestBuilders.put("/api/category/" + categoryId)
				.content(asJsonString(category))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().json(asJsonString(errorResponse)));
	}

	@Test
	@DisplayName("カテゴリリスト更新API_削除フラグあり_OK")
	public void updataCategory_OK2() throws Exception {

		final Long categoryId = (long) 1;

		final Category category = new Category("ネックレス", true);

		ErrorResponse errorResponse = new ErrorResponse(
				HttpStatus.OK.value(),
				null);

		// モックの設定
		doNothing().when(categoryService).checkCategoryData(category);
		doNothing().when(categoryService).updateCategory(categoryId, category);

		// テスト実行
		mvc.perform(MockMvcRequestBuilders.put("/api/category/" + categoryId)
				.content(asJsonString(category))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().json(asJsonString(errorResponse)));
	}

	@Test
	@DisplayName("カテゴリリスト更新API_入力情報NG")
	public void updataCategory_NG1() throws Exception {

		final Long categoryId = (long) 1;

		final Category category = new Category();

		ErrorResponse errorResponse = new ErrorResponse(
				HttpStatus.BAD_REQUEST.value(),
				"カテゴリ名が未入力です");

		// モックの設定
		doThrow(new BadRequestException("カテゴリ名が未入力です")).when(categoryService).checkCategoryData(category);

		// テスト実行
		mvc.perform(MockMvcRequestBuilders.put("/api/category/" + categoryId)
				.content(asJsonString(category))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(content().json(asJsonString(errorResponse)));

	}

	@Test
	@DisplayName("カテゴリリスト更新API_存在チェック取得NG")
	public void updataCategory_NG2() throws Exception {

		final Long categoryId = (long) 1;

		final Category category = new Category("ネックレス");

		ErrorResponse errorResponse = new ErrorResponse(
				HttpStatus.INTERNAL_SERVER_ERROR.value(),
				"カテゴリの取得に失敗しました");

		// モックの設定
		doNothing().when(categoryService).checkCategoryData(category);
		doThrow(new SQLException("カテゴリの取得に失敗しました")).when(categoryService)
				.checkExistsCategory(categoryId, category);

		// テスト実行
		mvc.perform(MockMvcRequestBuilders.put("/api/category/" + categoryId)
				.content(asJsonString(category))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError())
				.andExpect(content().json(asJsonString(errorResponse)));

	}

	@Test
	@DisplayName("カテゴリリスト更新API_存在チェックNG")
	public void updataCategory_NG3() throws Exception {

		final Long categoryId = (long) 1;

		final Category category = new Category("ネックレス");

		ErrorResponse errorResponse = new ErrorResponse(
				HttpStatus.INTERNAL_SERVER_ERROR.value(),
				"このカテゴリ名は既に存在しています");

		// モックの設定
		doNothing().when(categoryService).checkCategoryData(category);
		doThrow(new SQLException("このカテゴリ名は既に存在しています")).when(categoryService)
				.checkExistsCategory(categoryId, category);

		// テスト実行
		mvc.perform(MockMvcRequestBuilders.put("/api/category/" + categoryId)
				.content(asJsonString(category))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError())
				.andExpect(content().json(asJsonString(errorResponse)));

	}

	@Test
	@DisplayName("カテゴリ停止API_OK")
	public void deleteCategory_OK() throws Exception {

		final Long categoryId = (long) 1;

		ErrorResponse errorResponse = new ErrorResponse(
				HttpStatus.OK.value(),
				null);

		// モックの設定
		doNothing().when(categoryService).deleteCategory(categoryId);

		// テスト実行
		mvc.perform(MockMvcRequestBuilders.delete("/api/category/" + categoryId)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().json(asJsonString(errorResponse)));

	}

	@Test
	@DisplayName("カテゴリ停止API_停止失敗")
	public void deleteCategory_NG() throws Exception {

		final Long categoryId = (long) 1;

		ErrorResponse errorResponse = new ErrorResponse(
				HttpStatus.INTERNAL_SERVER_ERROR.value(),
				"カテゴリの停止に失敗しました");

		// モックの設定
		doThrow(new SQLException("カテゴリの停止に失敗しました")).when(categoryService).deleteCategory(categoryId);

		// テスト実行
		mvc.perform(MockMvcRequestBuilders.delete("/api/category/" + categoryId)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError())
				.andExpect(content().json(asJsonString(errorResponse)));

	}
}
