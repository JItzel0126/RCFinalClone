package com.RCclone.example.reci.feed.recipes.repository;

import com.RCclone.example.reci.feed.recipes.entity.Recipes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecipesRepository extends JpaRepository<Recipes, String> {
    @Query(value = "select DISTINCT r from Recipes r\n" +
                   "left join fetch r.recipeTag rt\n" +
                   "left join fetch rt.tag\n" +
                   "where r.uuid = :uuid")
    Optional<Recipes> findByIdWithTags(@Param("uuid") String uuid);
}
