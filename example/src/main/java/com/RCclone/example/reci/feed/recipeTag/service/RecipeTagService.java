package com.RCclone.example.reci.feed.recipeTag.service;

import com.RCclone.example.common.ErrorMsg;
import com.RCclone.example.common.RecipeMapStruct;
import com.RCclone.example.reci.feed.recipeTag.dto.RecipeTagDto;
import com.RCclone.example.reci.feed.recipeTag.entity.RecipeTag;
import com.RCclone.example.reci.feed.recipeTag.repository.RecipeTagRepository;
import com.RCclone.example.reci.feed.recipes.entity.Recipes;
import com.RCclone.example.reci.tag.dto.TagDto;
import com.RCclone.example.reci.tag.entity.Tag;
import com.RCclone.example.reci.tag.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecipeTagService {
    private final RecipeTagRepository recipeTagRepository;
    private final RecipeMapStruct recipeMapStruct;
    private final TagService tagService;
    private final ErrorMsg errorMsg;

//    레시피에 태그 저장
    public void saveTagsForRecipe(List<TagDto> tagDtos,
                                  Recipes recipe) {
        for (TagDto dto : tagDtos) {
//            1.태그가 이미 DB에 있는지 확인(없으면 생성)
            Tag tag = tagService.saveOrGetTag(dto.getTag()); // 태그 존재 체크 -> 없으면 생성

//            2. 이미 연결된 태그인지 체크
            boolean exists = recipeTagRepository.existsByRecipesAndTag(recipe,tag);
            if (exists) continue; // 중복 방지

//            3. 새로 연결
            RecipeTag recipeTag = new RecipeTag();
            recipeTag.setRecipes(recipe);
            recipeTag.setTag(tag);
            recipeTagRepository.save(recipeTag);
        }
    }

//    레시피별 태그 조회
    public List<RecipeTagDto> getTagByRecipeUuid(String recipeUuid) {
        List<RecipeTag> recipeTags = recipeTagRepository.findByRecipesUuid(recipeUuid);
        return recipeMapStruct.toRecipeTagDtoList(recipeTags);
    }
}
