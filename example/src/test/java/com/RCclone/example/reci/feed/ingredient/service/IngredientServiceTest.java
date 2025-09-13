package com.RCclone.example.reci.feed.ingredient.service;

import com.RCclone.example.reci.auth.entity.Member;
import com.RCclone.example.reci.auth.repository.MemberRepository;
import com.RCclone.example.reci.feed.ingredient.dto.IngredientDto;
import com.RCclone.example.reci.feed.ingredient.entity.Ingredient;
import com.RCclone.example.reci.feed.ingredient.repository.IngredientRepository;
import com.RCclone.example.reci.feed.recipes.entity.Recipes;
import com.RCclone.example.reci.feed.recipes.repository.RecipesRepository;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Log4j2
class IngredientServiceTest {

    @Autowired
    IngredientService ingredientService;
    @Autowired
    IngredientRepository ingredientRepository;
    @Autowired
    RecipesRepository recipesRepository;
    @Autowired
    MemberRepository memberRepository;

    private Recipes testRecipe;

    @BeforeEach
    void setUp() {
        // 1. 테스트용 사용자 생성
        Member testMember = new Member();
        testMember.setUserEmail("test@example.com");  // PK 필수
        testMember.setUserId("tester");              // optional 하지만 있으면 보기 좋음
        testMember.setNickname("테스터");             // optional
        testMember.setPassword("1234");              // optional (실제로는 암호화 해야 하지만 테스트니까 OK)

        memberRepository.save(testMember);

        // 2. 레시피 생성 + 작성자 연결
        testRecipe = new Recipes();
        testRecipe.setUuid(UUID.randomUUID().toString());
        testRecipe.setRecipeTitle("테스트 레시피");
        testRecipe.setMember(testMember);   // ⭐ 중요: null 방지

        recipesRepository.save(testRecipe);
    }

    @Test
    void saveAll() {
        // 준비
        List<IngredientDto> ingredients = List.of(
                new IngredientDto("양파", "100"),
                new IngredientDto("소금","10")
        );

        // when
        ingredientService.saveAll(ingredients, testRecipe);

        // then
        List<Ingredient> saved = ingredientRepository
                .findByRecipesUuidAndDeletedFalseOrderBySortOrderAsc(testRecipe.getUuid());
        log.info("saved:{}",saved);
        assertThat(saved).hasSize(2);
        assertThat(saved.get(0).getIngredientName()).isEqualTo("양파");
        assertThat(saved.get(1).getSortOrder()).isEqualTo(20);
    }

    @Test
    void testUpdateIngredient() {
        Ingredient ing = new Ingredient();
        ing.setIngredientName("고추");
        ing.setIngredientAmount("2개");
        ing.setSortOrder(10L);
        ing.setRecipes(testRecipe);
        ingredientRepository.save(ing);

        IngredientDto dto = new IngredientDto(ing.getId(), "청양고추", "3개");

        // when
        ingredientService.updateIngredient(dto);

        // then
        Ingredient updated = ingredientRepository.findById(ing.getId()).orElseThrow();
        log.info("updated:{}",updated);
    }

    @Test
    void testIngredientLogicalDelete() {
        Ingredient ing = new Ingredient();
        ing.setIngredientName("후추");
        ing.setIngredientAmount("조금");
        ing.setSortOrder(10L);
        ing.setRecipes(testRecipe);
        ingredientRepository.save(ing);

        // when
        ingredientService.ingredientLogicalDelete(ing.getId());

        // then
        Ingredient deleted = ingredientRepository.findById(ing.getId()).orElseThrow();
        log.info("deleted:{}", deleted);
    }

    @Test
    void testGetIngredients() {
        Ingredient ing1 = new Ingredient();
        ing1.setIngredientName("간장");
        ing1.setIngredientAmount("1큰술");
        ing1.setSortOrder(10L);
        ing1.setRecipes(testRecipe);

        Ingredient ing2 = new Ingredient();
        ing2.setIngredientName("설탕");
        ing2.setIngredientAmount("1큰술");
        ing2.setSortOrder(20L);
        ing2.setRecipes(testRecipe);

        ingredientRepository.saveAll(List.of(ing1, ing2));

        // when
        List<IngredientDto> list = ingredientService.getIngredients(testRecipe.getUuid());

        log.info("list:{}",list);
    }

    @Test
    void replaceAll() {

        Ingredient ing = new Ingredient();
        ing.setIngredientName("고추");
        ing.setIngredientAmount("2개");
        ing.setSortOrder(10L);
        ing.setRecipes(testRecipe);
        ingredientRepository.save(ing);

        log.info("updated:{}", ingredientRepository.findById(ing.getId()).orElseThrow());

        IngredientDto dto = new IngredientDto(ing.getId(), "청양고추", "3개");

        // when
        ingredientService.updateIngredient(dto);

        // then
        Ingredient updated = ingredientRepository.findById(ing.getId()).orElseThrow();
        log.info("updated:{}", updated);
    }
}