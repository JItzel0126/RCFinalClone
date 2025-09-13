package com.RCclone.example.reci.feed.recipes.dto;

import com.RCclone.example.reci.feed.ingredient.dto.IngredientDto;
import com.RCclone.example.reci.feed.recipeContent.dto.RecipeContentDto;
import com.RCclone.example.reci.tag.dto.TagDto;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RecipesDto {

    private String uuid;            // PK
    private String userEmail;       // member FK
    private String recipeTitle;
    private String recipeIntro;
    private String recipeCategory;
    private String postStatus;
    private String difficulty;
    private Long cookingTime;
    private String thumbnailUrl;

//    배열 조회
    private List<RecipeContentDto> contents;
    private List<IngredientDto> ingredients;
    private List<TagDto> tags;                  // 태그 배열

    private Long viewCount;
    private Long likeCount;
    private Long reportCount;
    private Long commentCount;

    private LocalDateTime insertTime;
    private LocalDateTime updateTime;

    /* ==========================
    ⚡ 피드 조회용 생성자
    ========================== */
    public RecipesDto(String uuid,
                      String recipeTitle,
                      String userEmail,
                      String recipeIntro,
                      Long likeCount,
                      Long commentCount,
                      String postStatus,
                      List<TagDto> tags,
                      LocalDateTime insertTime) {
        this.uuid = uuid;
        this.recipeTitle = recipeTitle;
        this.userEmail = userEmail;
        this.recipeIntro = recipeIntro;
        this.insertTime = insertTime;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.postStatus = postStatus;
        this.tags = tags;
    }

    /* ==========================
     ⚡ 상세페이지 조회용 생성자
     (필드 대부분 다 채움)
     ========================== */
    public RecipesDto(String uuid,
                      String userEmail,
                      String recipeTitle,
                      String recipeIntro,
                      String recipeCategory,
                      String postStatus,
                      String difficulty,
                      Long cookingTime,
                      List<RecipeContentDto> contents,
                      List<IngredientDto> ingredients,
                      List<TagDto> tags,
                      Long viewCount,
                      Long likeCount,
                      Long commentCount,
                      LocalDateTime insertTime,
                      LocalDateTime updateTime) {
        this.uuid = uuid;
        this.userEmail = userEmail;
        this.recipeTitle = recipeTitle;
        this.recipeIntro = recipeIntro;
        this.recipeCategory = recipeCategory;
        this.postStatus = postStatus;
        this.difficulty = difficulty;
        this.cookingTime = cookingTime;
        this.contents = contents;
        this.ingredients = ingredients;
        this.viewCount = viewCount;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.insertTime = insertTime;
        this.updateTime = updateTime;
        this.tags = tags;
    }

    /* ==========================
   ⚡ 등록/수정용 생성자
   (자동생성 제외, 작성자가 넣는 값만)
   ========================== */
    public RecipesDto(String recipeTitle,
                      String recipeIntro,
                      String recipeCategory,
                      String postStatus,
                      String difficulty,
                      Long cookingTime,
                      List<RecipeContentDto> contents,
                      List<IngredientDto> ingredients,
                      List<TagDto> tags) {

        this.recipeTitle = recipeTitle;
        this.recipeIntro = recipeIntro;
        this.recipeCategory = recipeCategory;
        this.postStatus = postStatus;
        this.difficulty = difficulty;
        this.cookingTime = cookingTime;
        this.contents = contents;
        this.ingredients = ingredients;
        this.tags = tags;
    }
}
