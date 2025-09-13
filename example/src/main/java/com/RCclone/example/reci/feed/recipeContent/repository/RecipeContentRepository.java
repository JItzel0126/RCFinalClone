package com.RCclone.example.reci.feed.recipeContent.repository;

import com.RCclone.example.reci.feed.recipeContent.entity.RecipeContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeContentRepository extends JpaRepository<RecipeContent, Long> {

//    레시피 uuid로 조리 단계 리스트 조회
    List<RecipeContent> findByRecipesUuidOrderByStepOrderAsc(String uuid);
}
