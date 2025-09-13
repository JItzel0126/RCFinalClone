package com.RCclone.example.reci.feed.recipes.service;

import com.RCclone.example.reci.auth.entity.Member;
import com.RCclone.example.reci.auth.repository.MemberRepository;
import com.RCclone.example.reci.feed.ingredient.dto.IngredientDto;
import com.RCclone.example.reci.feed.ingredient.entity.Ingredient;
import com.RCclone.example.reci.feed.ingredient.repository.IngredientRepository;
import com.RCclone.example.reci.feed.ingredient.service.IngredientService;
import com.RCclone.example.reci.feed.recipeContent.dto.RecipeContentDto;
import com.RCclone.example.reci.feed.recipeContent.entity.RecipeContent;
import com.RCclone.example.reci.feed.recipeContent.repository.RecipeContentRepository;
import com.RCclone.example.reci.feed.recipeContent.service.RecipeContentService;
import com.RCclone.example.reci.feed.recipeTag.entity.RecipeTag;
import com.RCclone.example.reci.feed.recipeTag.repository.RecipeTagRepository;
import com.RCclone.example.reci.feed.recipeTag.service.RecipeTagService;
import com.RCclone.example.reci.feed.recipes.dto.RecipesDto;
import com.RCclone.example.reci.feed.recipes.entity.Recipes;
import com.RCclone.example.reci.feed.recipes.repository.RecipesRepository;
import com.RCclone.example.reci.tag.dto.TagDto;
import com.RCclone.example.reci.tag.service.TagService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Log4j2
class RecipesServiceTest {

    @PersistenceContext
    private EntityManager em; // 👉 JPA 영속성 컨텍스트 제어용

    @Autowired
    RecipesService recipeService;

    @Autowired
    RecipesRepository recipeRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    RecipeTagService recipeTagService;
    @Autowired
    RecipeTagRepository recipeTagRepository;


    @Autowired
    RecipeContentService recipeContentService;
    @Autowired
    RecipeContentRepository recipeContentRepository;

    @Autowired
    IngredientService ingredientService;
    @Autowired
    IngredientRepository ingredientRepository;

    @Autowired
    TagService tagService;

    private RecipesDto sampleDto;

    private MockMultipartFile thumbnail;

    private Recipes testRecipe;



    @Test
    void 레시피_DTO_통합저장_검증() throws Exception {
        Member member = new Member();
        member.setUserEmail("test@example.com");
        member.setUserId("테스터");
        memberRepository.save(member);


            // given
            RecipesDto recipeDto = new RecipesDto(
                    "김치찌개", "얼큰한 김치찌개", "한식",
                    "PUBLIC", "쉬움", 30L,
                    null, null, null
            );

        List<IngredientDto> ingredientDtos = List.of(
                new IngredientDto(null, "김치", "200g", null),
                new IngredientDto(null, "돼지고기", "150g", null)
        );


            List<RecipeContentDto> contentDtos = List.of(
                    new RecipeContentDto("김치를 볶는다", 10L, null),
                    new RecipeContentDto("물을 붓는다", 20L, null)
            );

            List<byte[]> images = List.of(
                    "img-step1".getBytes(StandardCharsets.UTF_8),
                    "img-step2".getBytes(StandardCharsets.UTF_8)
            );

            List<TagDto> tagDtos = List.of(
                    new TagDto(null, "찌개"),
                    new TagDto(null, "매운맛")
            );


            // when
            String recipeId = recipeService.createRecipe(
                    recipeDto, ingredientDtos, contentDtos, images, tagDtos, member
            );

        // === flush & clear ===
        em.flush();
        em.clear();


        // then
            Recipes saved = recipeRepository.findByIdWithTags(recipeId).orElseThrow();
            log.info("저장된 레시피: {}", saved);
            log.info("입력 태그 DTO: {}", tagDtos);
            List<RecipeTag> savedTags = recipeTagRepository.findAll();
            log.info("저장된 RecipeTag 엔티티: {}", savedTags);

        // 단방향이라서 직접 Repository로 조회해야 함
        List<Ingredient> savedIngredients = ingredientRepository.findByRecipesUuidAndDeletedFalseOrderBySortOrderAsc(recipeId);
        List<RecipeContent> savedContents = recipeContentRepository.findByRecipesUuidOrderByStepOrderAsc(recipeId);

        log.info("연결된 재료: {}", savedIngredients);
        log.info("연결된 조리단계: {}", savedContents);
        log.info("연결된 태그: {}", saved.getRecipeTag());

        assertThat(savedIngredients).hasSize(2);
        assertThat(savedContents).hasSize(2);
        // DB를 기준으로 검증
        assertThat(savedTags).hasSize(2);
        }

    @Test
    void 레시피_태그포함_조회() {
        // given (레시피 + 태그 저장)

        Member member = new Member();
        member.setUserEmail("test@example.com");
        member.setUserId("테스터");
        memberRepository.save(member);


        // given
        RecipesDto recipeDto = new RecipesDto(
                "김치찌개", "얼큰한 김치찌개", "한식",
                "PUBLIC", "쉬움", 30L,
                null, null, null
        );

        List<IngredientDto> ingredientDtos = List.of(
                new IngredientDto(null, "김치", "200g", null),
                new IngredientDto(null, "돼지고기", "150g", null)
        );


        List<RecipeContentDto> contentDtos = List.of(
                new RecipeContentDto("김치를 볶는다", 10L, null),
                new RecipeContentDto("물을 붓는다", 20L, null)
        );

        List<byte[]> images = List.of(
                "img-step1".getBytes(StandardCharsets.UTF_8),
                "img-step2".getBytes(StandardCharsets.UTF_8)
        );

        List<TagDto> tagDtos = List.of(
                new TagDto(null, "찌개"),
                new TagDto(null, "매운맛")
        );


        String recipeId = recipeService.createRecipe(
                recipeDto, ingredientDtos, contentDtos, images, tagDtos, member
        );

        // === flush & clear ===
        em.flush();
        em.clear();

        // when
        Recipes found = recipeRepository.findByIdWithTags(recipeId).orElseThrow();

        // then
        log.info("조회된 레시피 태그: {}", found.getRecipeTag());
        assertThat(found.getRecipeTag()).hasSize(2);
    }
    }


