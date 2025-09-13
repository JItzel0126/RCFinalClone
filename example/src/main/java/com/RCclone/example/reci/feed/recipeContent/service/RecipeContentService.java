package com.RCclone.example.reci.feed.recipeContent.service;

import com.RCclone.example.common.ErrorMsg;
import com.RCclone.example.common.RecipeMapStruct;
import com.RCclone.example.reci.feed.recipeContent.dto.RecipeContentDto;
import com.RCclone.example.reci.feed.recipeContent.entity.RecipeContent;
import com.RCclone.example.reci.feed.recipeContent.repository.RecipeContentRepository;
import com.RCclone.example.reci.feed.recipes.entity.Recipes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RecipeContentService {
    private final RecipeContentRepository recipeContentRepository;
    private final RecipeMapStruct recipeMapStruct;
    private final ErrorMsg errorMsg;

    /* ==========================
        상세조회 (레시피 기준)
    ========================== */
    public List<RecipeContentDto> getContents(String recipesUuid) {
        List<RecipeContent> contents = recipeContentRepository
                .findByRecipesUuidOrderByStepOrderAsc(recipesUuid);

        if (contents.isEmpty()) {
            throw new RuntimeException(errorMsg.getMessage("errors.not.found"));
        }
        return recipeMapStruct.toRecipeContentDtoList(contents);
    }

    /* ==========================
        등록 / 수정 - 전체 교체
    ========================== */
    public void saveRecipeContent(List<RecipeContentDto> contentDtos,
                                  List<byte[]> images,
                                  Recipes recipes){
        // 1) 기존 단계 삭제
        recipeContentRepository.deleteByRecipesUuid(recipes.getUuid());

        // 2) 새 단계 등록
        for (int i = 0; i < contentDtos.size(); i++) {
            RecipeContent content = recipeMapStruct.toRecipeContentEntity(contentDtos.get(i));
            content.setRecipes(recipes);
            content.setStepOrder((i+1L)*10);

            if(images != null && images.size() > i && images.get(i) != null) {
                content.setRecipeImage(images.get(i));
            }
        //  3) 먼저 저장해서 stepId 생성
            RecipeContent saved = recipeContentRepository.save(content);

        //   4) stepId 기반으로 다운로드 URL 생성
                String downloadUrl = ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .path("/content/download")
                        .queryParam("stepId", content.getStepId())
                        .toUriString();
                saved.setRecipeImageUrl(downloadUrl);

        //    5) 다시 update
            recipeContentRepository.save(content);
        }
    }

    /* 삭제(레시피 단위) */
    public void deleteByRecipe(String recipesUuid) {
        recipeContentRepository.deleteByRecipesUuid(recipesUuid);
    }


}