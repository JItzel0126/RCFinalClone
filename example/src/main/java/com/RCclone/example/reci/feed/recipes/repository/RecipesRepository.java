package com.RCclone.example.reci.feed.recipes.repository;

import com.RCclone.example.reci.feed.recipes.entity.Recipes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipesRepository extends JpaRepository<Recipes, String> {
}
