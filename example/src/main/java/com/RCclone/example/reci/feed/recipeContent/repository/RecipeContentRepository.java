package com.RCclone.example.reci.feed.recipeContent.repository;

import com.RCclone.example.reci.feed.recipeContent.entity.RecipeContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface RecipeContentRepository extends JpaRepository<RecipeContent, Long> {

//    레시피 uuid로 조리 단계 리스트 조회
    List<RecipeContent> findByRecipesUuidOrderByStepOrderAsc(String uuid);

    @Modifying
    @Transactional
    @Query("DELETE FROM RecipeContent rc WHERE rc.recipes.uuid = :uuid")
    void deleteByRecipesUuid(@Param("uuid") String uuid);
}
