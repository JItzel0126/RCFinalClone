package com.RCclone.example.reci.feed.recipeContent.service;

import com.RCclone.example.reci.auth.entity.Member;
import com.RCclone.example.reci.auth.repository.MemberRepository;
import com.RCclone.example.reci.feed.recipeContent.dto.RecipeContentDto;
import com.RCclone.example.reci.feed.recipeContent.entity.RecipeContent;
import com.RCclone.example.reci.feed.recipeContent.repository.RecipeContentRepository;
import com.RCclone.example.reci.feed.recipeTag.repository.RecipeTagRepository;
import com.RCclone.example.reci.feed.recipeTag.service.RecipeTagService;
import com.RCclone.example.reci.feed.recipes.entity.Recipes;
import com.RCclone.example.reci.feed.recipes.repository.RecipesRepository;
import com.RCclone.example.reci.tag.entity.Tag;
import com.RCclone.example.reci.tag.repository.TagRepository;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Log4j2
class RecipeContentServiceTest {

    @Autowired
    private RecipeContentService recipeContentService;

    @Autowired
    private RecipeContentRepository recipeContentRepository;

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

    private Recipes recipe; // 테스트용 레시피 엔티티


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
        recipe.setMember(member);
        recipe.setUuid("test-uuid-123");
        recipe.setRecipeTitle("김치볶음밥");

        recipesRepository.save(recipe);

        // 3) 태그 저장 (예: "매운맛")
        Tag tag = new Tag();
        tag.setTag("매운맛");
        tagRepository.save(new Tag(null, "김치찌개", false));
        tagRepository.save(new Tag(null, "된장찌개", false));
        tagRepository.save(tag);
    }

    @Test
    void 레시피_콘텐츠_저장_이미지포함() throws Exception {
        // given
        RecipeContentDto dto1 = new RecipeContentDto(null, null, "재료 손질하기", 10L, recipe.getUuid());
        RecipeContentDto dto2 = new RecipeContentDto(null, null, "볶기 시작", 20L, recipe.getUuid());

        List<RecipeContentDto> dtos = List.of(dto1, dto2);

        MockMultipartFile image1 = new MockMultipartFile(
                "image1", "step1.png", "image/png", "dummy-image-1".getBytes()
        );
        MockMultipartFile image2 = new MockMultipartFile(
                "image2", "step2.png", "image/png", "dummy-image-2".getBytes()
        );

        List<byte[]> images = List.of(image1.getBytes(), image2.getBytes());

        // when
        recipeContentService.saveRecipeContent(dtos, images, recipe);

        // then
        List<RecipeContent> saved = recipeContentRepository.findAll();
        log.info("저장된 콘텐츠 개수: {}", saved.size());
        saved.forEach(c -> log.info("콘텐츠: stepId={}, stepExplain={}, url={}, 순서={}",
                c.getStepId(), c.getStepExplain(), c.getRecipeImageUrl(), c.getStepOrder()));

        assertThat(saved).hasSize(2);
        assertThat(saved.get(0).getStepExplain()).isEqualTo("재료 손질하기");
        assertThat(saved.get(1).getStepExplain()).isEqualTo("볶기 시작");
        assertThat(saved.get(0).getRecipeImage()).isNotNull();
        assertThat(saved.get(1).getRecipeImageUrl()).contains("content/download");
    }

    @Test
    void 조리단계_수정시_전체교체된다() {
        // given: 최초 등록
        List<RecipeContentDto> originalContents = List.of(
                new RecipeContentDto(null, "url1", "양파 볶기", 10L, recipe.getUuid()),
                new RecipeContentDto(null, "url2", "고기 굽기", 20L, recipe.getUuid())
        );

        recipeContentService.saveRecipeContent(originalContents, List.of(), recipe);
        List<RecipeContent> saved = recipeContentRepository.findByRecipesUuidOrderByStepOrderAsc(recipe.getUuid());

        log.info("최초 저장된 단계: {}", saved);
        assertThat(saved).hasSize(2);

        // when: 수정 (전체 교체)
        List<RecipeContentDto> updatedContents = List.of(
                new RecipeContentDto(null, "url3", "감자 넣기", 10L, recipe.getUuid()),
                new RecipeContentDto(null, "url4", "간 맞추기", 20L, recipe.getUuid())
        );

        recipeContentService.saveRecipeContent(updatedContents, List.of(), recipe);
        List<RecipeContent> afterUpdate = recipeContentRepository.findByRecipesUuidOrderByStepOrderAsc(recipe.getUuid());

        // then: 기존 데이터는 지워지고, 새로운 데이터만 남아야 함
        log.info("수정 후 단계: {}", afterUpdate);

        assertThat(afterUpdate).hasSize(2);
        assertThat(afterUpdate.get(0).getStepExplain()).isEqualTo("감자 넣기");
        assertThat(afterUpdate.get(1).getStepExplain()).isEqualTo("간 맞추기");
    }
}