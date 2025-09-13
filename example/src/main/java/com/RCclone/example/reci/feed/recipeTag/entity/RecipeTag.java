package com.RCclone.example.reci.feed.recipeTag.entity;

import com.RCclone.example.common.BaseTimeEntity;
import com.RCclone.example.reci.feed.recipes.entity.Recipes;
import com.RCclone.example.reci.tag.entity.Tag;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "recipe_tag")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(of = "recipeTagId", callSuper = false)
public class RecipeTag extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recipeTagId;               //pk

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uuid")
    private Recipes recipes;                // fk

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    private Tag tag;
}
