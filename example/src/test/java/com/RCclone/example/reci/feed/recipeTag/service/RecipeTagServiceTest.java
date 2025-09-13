package com.RCclone.example.reci.feed.recipeTag.service;

import com.RCclone.example.reci.auth.entity.Member;
import com.RCclone.example.reci.auth.repository.MemberRepository;
import com.RCclone.example.reci.feed.recipeTag.entity.RecipeTag;
import com.RCclone.example.reci.feed.recipeTag.repository.RecipeTagRepository;
import com.RCclone.example.reci.feed.recipes.entity.Recipes;
import com.RCclone.example.reci.feed.recipes.repository.RecipesRepository;
import com.RCclone.example.reci.tag.dto.TagDto;
import com.RCclone.example.reci.tag.entity.Tag;
import com.RCclone.example.reci.tag.repository.TagRepository;
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
class RecipeTagServiceTest {

    @Autowired
    private RecipeTagService recipeTagService;

    @Autowired
    private RecipeTagRepository recipeTagRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private RecipesRepository recipesRepository;

    @Autowired
    private MemberRepository memberRepository;

    private Recipes recipe;

    @BeforeEach
    void setUp() {
        // 1) 사용자 저장
        Member member = new Member();
        member.setUserEmail("test@test.com");
        member.setPassword("1234");
        member.setNickname("tester");
        memberRepository.save(member);

        // 2) 레시피 저장
        recipe = new Recipes();
        recipe.setUuid(UUID.randomUUID().toString());
        recipe.setRecipeTitle("김치찌개");
        recipe.setRecipeIntro("얼큰한 김치찌개");
        recipe.setRecipeCategory("한식");
        recipe.setPostStatus("PUBLIC");
        recipe.setDifficulty("EASY");
        recipe.setCookingTime(30L);
        recipe.setMember(member);

        recipesRepository.save(recipe);

        // 3) 태그 저장 (예: "매운맛")
        Tag tag = new Tag();
        tag.setTag("매운맛");
        tagRepository.save(new Tag(null, "김치찌개", false));
        tagRepository.save(new Tag(null, "된장찌개", false));
        tagRepository.save(tag);
    }

    @Test
    void saveTagsForRecipe() {
        // given
        TagDto spicy = new TagDto(null, "매운맛");
        TagDto korean = new TagDto(null, "한식");
        List<TagDto> tags = List.of(spicy, korean);

        // when
        recipeTagService.saveTagsForRecipe(tags, recipe);

        // then
        List<RecipeTag> all = recipeTagRepository.findAll();
        log.info("레시피 태그 저장 결과: {}", all);

        assertThat(all).hasSize(2);
        assertThat(all.get(0).getRecipes().getUuid()).isEqualTo(recipe.getUuid());
        assertThat(all.get(1).getTag().getTag()).isEqualTo("한식");
    }

    @Test
    void 이미존재하는태그는_재사용됨() {
        // given
        TagDto spicy = new TagDto(null, "매운맛"); // 이미 DB에 존재
        List<TagDto> tags = List.of(spicy);

        // when
        recipeTagService.saveTagsForRecipe(tags, recipe);


        // then
        List<Tag> allTags = tagRepository.findAll();
        List<RecipeTag> recipeTags = recipeTagRepository.findAll();

        log.info("전체 태그 목록: {}", allTags);
        log.info("레시피 태그 연결: {}", recipeTags);

        assertThat(allTags).hasSize(3); // 새로운 태그 안생김
        assertThat(recipeTags).hasSize(1); // 연결만 추가됨
    }

}