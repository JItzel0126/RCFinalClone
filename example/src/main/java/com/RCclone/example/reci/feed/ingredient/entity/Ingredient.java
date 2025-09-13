package com.RCclone.example.reci.feed.ingredient.entity;


import com.RCclone.example.common.BaseTimeEntity;
import com.RCclone.example.reci.feed.recipes.entity.Recipes;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(of = "id", callSuper = false)
@Entity
@Table(name = "ingredient")
public class Ingredient extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String ingredientName;
    private String ingredientAmount;
    private Long sortOrder;

// Recipes FK (부모 Recipes 엔티티 참조)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uuid", nullable = false)
    private Recipes recipes;

//  db 재료명 용도
    private boolean deleted = false;

}
