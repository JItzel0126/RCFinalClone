package com.RCclone.example.reci.feed.recipeContent.service;

import com.RCclone.example.common.ErrorMsg;
import com.RCclone.example.common.RecipeMapStruct;
import com.RCclone.example.reci.feed.recipeContent.dto.RecipeContentDto;
import com.RCclone.example.reci.feed.recipeContent.entity.RecipeContent;
import com.RCclone.example.reci.feed.recipeContent.repository.RecipeContentRepository;
import com.RCclone.example.reci.feed.recipes.entity.Recipes;
import lombok.RequiredArgsConstructor;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Service;

import java.util.List;

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
        등록 (레시피와 함께 저장)
    ========================== */
    public void saveRecipeContent(List<RecipeContentDto> dto,
                                  Recipes recipes) {
        for (int i = 0; i<dto.size(); i++) {
            RecipeContent content = recipeMapStruct.toRecipeContentEntity(dto.get(i));

            content.setStepOrder((i+1L)*10);
            content.setRecipes(recipes);

            recipeContentRepository.save(content);
        }
    }

    /* ==========================
         수정 (Dirty Checking)
       - 설명 or 이미지 교체 가능
    ========================== */
    
}
