package com.RCclone.example.reci.feed.recipes.entity;

import com.RCclone.example.common.BaseTimeEntity;
import com.RCclone.example.reci.auth.entity.Member;
import com.RCclone.example.reci.feed.ingredient.entity.Ingredient;
import com.RCclone.example.reci.feed.recipeContent.entity.RecipeContent;
import com.RCclone.example.reci.feed.recipeTag.entity.RecipeTag;
import com.RCclone.example.reci.tag.entity.Tag;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode (of = "uuid", callSuper=true)
@Entity
@Table(name = "recipes")
public class Recipes extends BaseTimeEntity {

    @Id
    private String uuid;                        // 기본키

    private String recipeTitle;
    private String recipeIntro;
    private String recipeCategory;
    private Long cookingTime;

    @Lob
    private byte[] thumbnail;
    private String thumbnailUrl;

    private String postStatus;                 // 공개여부
    private Long viewCount = 0L;               // 조회수
    private Long likeCount = 0L;               // 좋아요
    private Long reportCount = 0L;             // 신고수
    private Long commentCount = 0L;            // 댓글수

    private String difficulty;                  // 난이도

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userEmail", nullable = false)
    private Member member;

//    TODO: Tag 테이블 추가
     /* ==========================
       🔹 양방향 매핑 (태그만)
       ========================== */
    @OneToMany(mappedBy = "recipes", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecipeTag> recipeTag = new ArrayList<>();

     /* ==========================
       🔹 단방향 매핑
       ========================== */
    // 재료는 단방향: Ingredient → Recipes (ManyToOne)
    // 조리단계도 단방향: RecipeContent → Recipes (ManyToOne)

}
