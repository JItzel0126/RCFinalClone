package com.RCclone.example.reci.feed.recipeTag.repository;

import com.RCclone.example.reci.feed.recipeTag.entity.RecipeTag;
import com.RCclone.example.reci.feed.recipes.entity.Recipes;
import com.RCclone.example.reci.tag.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeTagRepository extends JpaRepository<RecipeTag, Long> {

//    uuid로 찾기
    List<RecipeTag> findByRecipesUuid(String recipesUuid);

//    존재하는 태그 확인
    boolean existsByRecipesAndTag(Recipes recipes, Tag tag);
}
