package com.RCclone.example.reci.feed.recipes.service;

import com.RCclone.example.common.ErrorMsg;
import com.RCclone.example.common.RecipeMapStruct;
import com.RCclone.example.reci.auth.entity.Member;
import com.RCclone.example.reci.feed.ingredient.dto.IngredientDto;
import com.RCclone.example.reci.feed.ingredient.service.IngredientService;
import com.RCclone.example.reci.feed.recipeContent.dto.RecipeContentDto;
import com.RCclone.example.reci.feed.recipeContent.service.RecipeContentService;
import com.RCclone.example.reci.feed.recipeTag.service.RecipeTagService;
import com.RCclone.example.reci.feed.recipes.dto.RecipesDto;
import com.RCclone.example.reci.feed.recipes.entity.Recipes;
import com.RCclone.example.reci.feed.recipes.repository.RecipesRepository;
import com.RCclone.example.reci.tag.dto.TagDto;
import com.RCclone.example.reci.tag.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RecipesService {
    private final RecipesRepository recipesRepository;
    private final IngredientService ingredientService;
    private final RecipeContentService recipeContentService;
    private final RecipeTagService recipeTagService;
    private final TagService tagService;
    private final RecipeMapStruct recipeMapStruct;
    private final ErrorMsg errorMsg;

    /* 저장 */
    @Transactional
    public String createRecipe(RecipesDto recipesDto,
                               List<IngredientDto> ingredientDtos,
                               List<RecipeContentDto> contentDtos,
                               List<byte[]> images,
                               List<TagDto> tagDtos,
                               Member member) {
        // 1) 레시피 엔티티 변환 및 기본값 설정
        Recipes recipe = recipeMapStruct.toRecipeEntity(recipesDto);
        recipe.setUuid(UUID.randomUUID().toString());
        recipe.setMember(member);

        // 2) 레시피 저장
        recipesRepository.save(recipe);

        // 3) 연관 엔티티 저장
        ingredientService.saveAll(ingredientDtos, recipe);
        recipeContentService.saveRecipeContent(contentDtos, images, recipe);
        recipeTagService.saveTagsForRecipe(tagDtos, recipe);

        return recipe.getUuid();
    }

    /* 수정 */
    @Transactional
    public void updateRecipe(String uuid,
                             RecipesDto recipesDto,
                             List<IngredientDto> ingredientDtos,
                             List<RecipeContentDto> contentDtos,
                             List<byte[]> images,
                             List<TagDto> tagDtos){
        Recipes recipe = recipesRepository.findById(uuid)
                .orElseThrow(()-> new RuntimeException(errorMsg.getMessage("errors.not.found")));

        // 1) 레시피 기본 정보 업데이트
        recipeMapStruct.updateRecipe(recipesDto, recipe);

        // 2) 하위 엔티티 전체 교체
        ingredientService.saveAll(ingredientDtos, recipe);
        recipeContentService.saveRecipeContent(contentDtos, images, recipe);
        recipeTagService.saveTagsForRecipe(tagDtos, recipe);
    }

    /* 상세 조회*/
    @Transactional(readOnly = true)
    public RecipesDto getRecipeDetails(String uuid) {
        Recipes recipe = recipesRepository.findById(uuid)
                .orElseThrow(()-> new RuntimeException(errorMsg.getMessage("errors.not.found")));

        return recipeMapStruct.toRecipeDto(recipe);
    }

    /* 삭제 */
    @Transactional
    public void deleteRecipe(String uuid) {
        recipesRepository.deleteById(uuid);
    }

}
