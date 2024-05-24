package com.example.ec.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.ec.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

	@Query(value = "select * from category ORDER BY category_id", nativeQuery = true) // SQL
	List<Category> findByCategoryList();

	@Query(value = "select count(*) from category where category_name = :category_name", nativeQuery = true) // SQL
	int findByCategoryName(@Param("category_name") String categoryName);

	@Query(value = "select count(*) from category where category_id NOT IN(select category_id from category where category_id = :category_id) AND category_name = :category_name", nativeQuery = true) // SQL
	int findByCategoryIdAndCategoryName(@Param("category_id") Long categoryId,
			@Param("category_name") String categoryName);
}
