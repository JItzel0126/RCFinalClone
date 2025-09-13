package com.RCclone.example.reci.feed.ingredient.repository;

import com.RCclone.example.reci.feed.ingredient.entity.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {

    // 삭제 안 된 재료만 조회
    List<Ingredient> findByRecipesUuidAndDeletedFalseOrderBySortOrderAsc(String uuid);
}
