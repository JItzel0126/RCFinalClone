package com.RCclone.example.reci.feed.recipeContent.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RecipeContentDto {
    private Long stepId;                // PK
    private String recipeImageUrl;
    private String stepExplain;
    private Long stepOrder;
    private String recipes;             // FK

    /* ==========================
        ⚡ 상세조회용 생성자
    ========================== */
    public RecipeContentDto(Long stepId,
                            String recipeImageUrl,
                            String stepExplain,
                            Long stepOrder){
        this.stepId = stepId;
        this.recipeImageUrl = recipeImageUrl;
        this.stepExplain = stepExplain;
        this.stepOrder = stepOrder;
    }

    /* ==========================
    ⚡ 등록용 생성자
    (PK, URL은 제외 — 이미지 업로드 시 서비스에서 처리)
    ========================== */
    public RecipeContentDto(String stepExplain,
                            Long stepOrder,
                            String recipes){
        this.stepExplain = stepExplain;
        this.stepOrder = stepOrder;
        this.recipes = recipes;
    }

    /* ==========================
    ⚡ 수정용 생성자
    (PK 포함, URL은 그대로 유지 or 새로 교체될 수 있음)
    ========================== */
    public RecipeContentDto(Long stepId,
                            String stepExplain,
                            Long stepOrder,
                            String recipes){
        this.stepId = stepId;
        this.stepExplain = stepExplain;
        this.stepOrder = stepOrder;
        this.recipes = recipes;
    }

}
