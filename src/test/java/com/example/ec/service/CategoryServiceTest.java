package com.example.ec.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.ec.ServiceImpl.CategoryServiceImpl;
import com.example.ec.entity.Category;
import com.example.ec.repository.CategoryRepository;

/**
 * Category APIのサービステスト
 */
@SpringBootTest
public class CategoryServiceTest {

	@Autowired
	CategoryServiceImpl categoryService;

	CategoryRepository categoryRepository;

	@Autowired
	private DataSource dataSource;
	private IDatabaseConnection dbUnitConnection;

	/**
	 * テスト前に毎回実施
	 * @throws Exception
	 */
	@BeforeEach
	void beforeEach() throws Exception {
		// 元のリポジトリを保存
		categoryRepository = (CategoryRepository) ReflectionTestUtils.getField(categoryService, "categoryRepository");

		// DBUnit データ抽入
		Connection connection = dataSource.getConnection();
		dbUnitConnection = new DatabaseConnection(connection);
		try (InputStream is = getClass().getClassLoader().getResourceAsStream("Category.xml")) {
			IDataSet originalDataSet = new FlatXmlDataSetBuilder().build(is);
			ReplacementDataSet dataSet = new ReplacementDataSet(originalDataSet);
			dataSet.addReplacementObject("", null);
			DatabaseOperation.CLEAN_INSERT.execute(dbUnitConnection, dataSet);
		}
	}

	/**
	 * テスト後に毎回実施
	 * @throws SQLException 
	 * @throws DataSetException 
	 * @throws Exception
	 */
	@AfterEach
	public void afterEach() throws Exception {
		// 元のリポジトリを戻す
		ReflectionTestUtils.setField(categoryService, "categoryRepository", categoryRepository);

		// DBUnit データ削除
		DatabaseOperation.DELETE_ALL.execute(dbUnitConnection, dbUnitConnection.createDataSet());
		dbUnitConnection.close();
	}

	@Test
	@DisplayName("カテゴリ作成_正常終了")
	public void createCategory_OK() throws Exception {
		Category category = new Category("テストピアス");

		// テスト実行
		categoryService.createCategory(category);

		// DB Assert
		ITable actualTable = dbUnitConnection.createDataSet().getTable("category");
		assertThat(actualTable.getRowCount()).isEqualTo(4);
		assertThat(actualTable.getValue(3, "category_id")).isNotNull();
		assertThat(actualTable.getValue(3, "category_name")).isEqualTo("テストピアス");
		assertThat(actualTable.getValue(3, "update_data")).isNotNull();
		assertThat(actualTable.getValue(3, "delete_data")).isNull();
		assertThat(actualTable.getValue(3, "delete_flag")).isEqualTo(false);
	}

	@Test
	@DisplayName("カテゴリ作成_異常終了")
	public void createCategory_NG1() throws Exception {
		Category category = new Category("テストピアス");

		// リポジトリをnullに設定
		ReflectionTestUtils.setField(categoryService, "categoryRepository", null);

		// テスト実行
		Exception exception = assertThrows(Exception.class, () -> {
			categoryService.createCategory(category);
		});

		assertThat(exception.getMessage()).contains("カテゴリの作成に失敗しました");
	}

	@Test
	@DisplayName("カテゴリリスト取得_正常終了")
	public void getCategoryList_OK() throws Exception {

		// テスト実行
		List<Category> categoryList = categoryService.getCategoryList();

		// Assert
		assertThat(categoryList.get(0).getCategoryId()).isEqualTo(1);
		assertThat(categoryList.get(0).getCategoryName()).isEqualTo("ネックレス");
		assertThat(categoryList.get(0).getUpdateData()).isNotNull();
		assertThat(categoryList.get(0).getDeleteData()).isNull();
		assertThat(categoryList.get(0).isDeleteFlag()).isEqualTo(false);

		// Assert
		assertThat(categoryList.get(1).getCategoryId()).isEqualTo(2);
		assertThat(categoryList.get(1).getCategoryName()).isEqualTo("ピアス");
		assertThat(categoryList.get(1).getUpdateData()).isNotNull();
		assertThat(categoryList.get(1).getDeleteData()).isNull();
		assertThat(categoryList.get(1).isDeleteFlag()).isEqualTo(false);
	}

	@Test
	@DisplayName("カテゴリリスト取得_異常終了")
	public void getCategoryList_NG1() throws Exception {
		Category category = new Category("テストピアス");

		// リポジトリをnullに設定
		ReflectionTestUtils.setField(categoryService, "categoryRepository", null);

		// テスト実行
		Exception exception = assertThrows(Exception.class, () -> {
			categoryService.getCategoryList();
		});

		assertThat(exception.getMessage()).contains("カテゴリの取得に失敗しました");
	}

	@Test
	@DisplayName("カテゴリ更新_正常終了_カテゴリ更新")
	public void updateCategory_OK1() throws Exception {
		final Long categoryId = (long) 1;
		Category category = new Category("テストピアス", false);

		// テスト実行
		categoryService.updateCategory(categoryId, category);

		// DB Assert
		ITable actualTable = dbUnitConnection.createDataSet().getTable("category");
		assertThat(actualTable.getRowCount()).isEqualTo(3);
		assertThat(actualTable.getValue(0, "category_id")).isNotNull();
		assertThat(actualTable.getValue(0, "category_name")).isEqualTo("テストピアス");
		assertThat(actualTable.getValue(0, "update_data")).isNotNull();
		assertThat(actualTable.getValue(0, "delete_data")).isNull();
		assertThat(actualTable.getValue(0, "delete_flag")).isEqualTo(false);
	}

	@Test
	@DisplayName("カテゴリ更新_正常終了_カテゴリ更新_削除のまま")
	public void updateCategory_OK2() throws Exception {
		final Long categoryId = (long) 3;
		Category category = new Category("テストピアス", true);

		// テスト実行
		categoryService.updateCategory(categoryId, category);

		// DB Assert
		ITable actualTable = dbUnitConnection.createDataSet().getTable("category");
		assertThat(actualTable.getRowCount()).isEqualTo(3);
		assertThat(actualTable.getValue(2, "category_id")).isNotNull();
		assertThat(actualTable.getValue(2, "category_name")).isEqualTo("テストピアス");
		assertThat(actualTable.getValue(2, "update_data")).isNotNull();
		assertThat(actualTable.getValue(2, "delete_data")).isNotNull();
		assertThat(actualTable.getValue(2, "delete_flag")).isEqualTo(true);
	}

	@Test
	@DisplayName("カテゴリ更新_正常終了_カテゴリ更新_停止解除")
	public void updateCategory_OK3() throws Exception {
		final Long categoryId = (long) 3;
		Category category = new Category("2連ピアス", false);

		// テスト実行
		categoryService.updateCategory(categoryId, category);

		// DB Assert
		ITable actualTable = dbUnitConnection.createDataSet().getTable("category");
		assertThat(actualTable.getRowCount()).isEqualTo(3);
		assertThat(actualTable.getValue(2, "category_id")).isNotNull();
		assertThat(actualTable.getValue(2, "category_name")).isEqualTo("2連ピアス");
		assertThat(actualTable.getValue(2, "update_data")).isNotNull();
		assertThat(actualTable.getValue(2, "delete_data")).isNull();
		assertThat(actualTable.getValue(2, "delete_flag")).isEqualTo(false);
	}

	@Test
	@DisplayName("カテゴリ更新_異常終了")
	public void updateCategory_NG() throws Exception {
		final Long categoryId = (long) 1;
		Category category = new Category("テストピアス");

		// リポジトリをnullに設定
		ReflectionTestUtils.setField(categoryService, "categoryRepository", null);

		// テスト実行
		Exception exception = assertThrows(Exception.class, () -> {
			categoryService.updateCategory(categoryId, category);
		});

		assertThat(exception.getMessage()).contains("カテゴリの更新に失敗しました");
	}

	@Test
	@DisplayName("カテゴリ停止_正常終了")
	public void deleteCategory_OK() throws Exception {
		final Long categoryId = (long) 1;

		// テスト実行
		categoryService.deleteCategory(categoryId);

		// DB Assert
		ITable actualTable = dbUnitConnection.createDataSet().getTable("category");
		assertThat(actualTable.getRowCount()).isEqualTo(3);
		assertThat(actualTable.getValue(0, "category_id")).isNotNull();
		assertThat(actualTable.getValue(0, "category_name")).isEqualTo("ネックレス");
		assertThat(actualTable.getValue(0, "update_data")).isNotNull();
		assertThat(actualTable.getValue(0, "delete_data")).isNotNull();
		assertThat(actualTable.getValue(0, "delete_flag")).isEqualTo(true);
	}

	@Test
	@DisplayName("カテゴリ停止_異常終了")
	public void deleteCategory_NG() throws Exception {
		final Long categoryId = (long) 1;

		// リポジトリをnullに設定
		ReflectionTestUtils.setField(categoryService, "categoryRepository", null);

		// テスト実行
		Exception exception = assertThrows(Exception.class, () -> {
			categoryService.deleteCategory(categoryId);
		});

		assertThat(exception.getMessage()).contains("カテゴリの停止に失敗しました");
	}

	@Test
	@DisplayName("カテゴリ情報チェック処理_チェック結果が問題なし")
	public void checkCategoryData_OK() throws Exception {
		Category category = new Category("カフー");

		// テスト実行
		categoryService.checkCategoryData(category);
	}

	@Test
	@DisplayName("カテゴリ情報チェック処理_カテゴリ名が未存在")
	public void checkCategoryData_NG() throws Exception {
		Category category = new Category();

		// テスト実行	
		Exception exception = assertThrows(Exception.class, () -> {
			categoryService.checkCategoryData(category);
		});

		// Assert
		assertThat(exception.getMessage()).contains("カテゴリ名が未入力です");
	}

	@Test
	@DisplayName("カテゴリ存在チェック処理_新規作成_チェック結果が問題なし")
	public void checkExistsCategory_OK1() throws Exception {
		Category category = new Category("カフー");

		// テスト実行
		categoryService.checkExistsCategory(null, category);
	}

	@Test
	@DisplayName("カテゴリ存在チェック処理_更新_同ID同カテゴリ名_チェック結果が問題なし")
	public void checkExistsCategory_OK2() throws Exception {
		final Long categoryId = (long) 1;
		Category category = new Category("ネックレス");

		// テスト実行
		categoryService.checkExistsCategory(categoryId, category);
	}

	@Test
	@DisplayName("カテゴリ存在チェック処理_更新_同ID別カテゴリ名_チェック結果が問題なし")
	public void checkExistsCategory_OK3() throws Exception {
		final Long categoryId = (long) 1;
		Category category = new Category("カフー");

		// テスト実行
		categoryService.checkExistsCategory(categoryId, category);
	}

	@Test
	@DisplayName("カテゴリ存在チェック処理_カテゴリ名取得失敗")
	public void checkExistsCategory_NG1() throws Exception {
		final Long categoryId = (long) 1;
		Category category = new Category("テストピアス");

		// リポジトリをnullに設定
		ReflectionTestUtils.setField(categoryService, "categoryRepository", null);

		// テスト実行
		Exception exception = assertThrows(Exception.class, () -> {
			categoryService.checkExistsCategory(categoryId, category);
		});

		assertThat(exception.getMessage()).contains("カテゴリの取得に失敗しました");
	}

	@Test
	@DisplayName("カテゴリ存在チェック処理_新規作成_カテゴリ名が存在")
	public void checkExistsCategory_NG2() throws Exception {
		Category category = new Category("ネックレス");

		// テスト実行
		Exception exception = assertThrows(Exception.class, () -> {
			categoryService.checkExistsCategory(null, category);
		});

		assertThat(exception.getMessage()).contains("このカテゴリ名は既に存在しています");
	}

	@Test
	@DisplayName("カテゴリ存在チェック処理_更新_同ID別カテゴリ名_カテゴリ名が存在")
	public void checkExistsCategory_NG3() throws Exception {
		final Long categoryId = (long) 1;
		Category category = new Category("ピアス");

		// テスト実行
		Exception exception = assertThrows(Exception.class, () -> {
			categoryService.checkExistsCategory(categoryId, category);
		});

		assertThat(exception.getMessage()).contains("このカテゴリ名は既に存在しています");
	}
}
