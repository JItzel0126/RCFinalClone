package com.RCclone.example.reci.feed.recipeContent.entity;

import com.RCclone.example.common.BaseTimeEntity;
import com.RCclone.example.reci.feed.recipes.entity.Recipes;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "stepId", callSuper = false)
@Entity
@Table(name = "recipe_content")
public class RecipeContent extends BaseTimeEntity {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long stepId;                        // 기본키

    @Lob
    private byte[] recipeImage;
    private String recipeImageUrl;
    private String stepExplain;
    //    순서 변경용
    private Long stepOrder;

    //  레시피 참조키
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uuid", nullable = false)
    private Recipes recipes;
}
