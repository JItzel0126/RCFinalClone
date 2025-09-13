package com.RCclone.example.reci.tag.service;

import com.RCclone.example.reci.auth.entity.Member;
import com.RCclone.example.reci.auth.repository.MemberRepository;
import com.RCclone.example.reci.feed.recipes.entity.Recipes;
import com.RCclone.example.reci.feed.recipes.repository.RecipesRepository;
import com.RCclone.example.reci.tag.entity.Tag;
import com.RCclone.example.reci.tag.repository.TagRepository;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


@Transactional
@SpringBootTest
@Log4j2
class TagServiceTest {

    @Autowired
    TagService tagService;

    @Autowired
    TagRepository tagRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    RecipesRepository recipesRepository;

    private Recipes testRecipe;

    @BeforeEach
    void setUp() {
        tagRepository.save(new Tag(null, "김치찌개", false));
        tagRepository.save(new Tag(null, "된장찌개", false));

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
    void 태그_신규저장() {
        // given
        String tagName = "비빔밥";

        // when
        Tag tag = tagService.saveOrGetTag(tagName);

        // then
        assertThat(tag.getTag()).isEqualTo(tagName);
        assertThat(tag.getTagId()).isNotNull(); // DB에 저장됨
        log.info("저장 된 태그 확인: {}" , tagRepository.findAll());
    }

    @Test
    void 태그_이미존재하면_재사용() {
        // given
        String tagName = "된장찌개";
        Tag saved = tagService.saveOrGetTag(tagName);

        // when
        Tag again = tagService.saveOrGetTag(tagName);

        String taggName = "강된장";
        Tag savedAgain = tagService.saveOrGetTag(taggName);

        // then
        assertThat(again.getTagId()).isEqualTo(saved.getTagId());
        log.info(tagRepository.findAll().toString());
    }

    @Test
    void 태그_대소문자_구분없이_동일처리() {
        // given
        Tag lower = tagService.saveOrGetTag("kimchi");
        Tag upper = tagService.saveOrGetTag("KIMCHI");
        Tag lunch = tagService.saveOrGetTag("KimChi");

        // then
        assertThat(lower.getTagId()).isEqualTo(upper.getTagId());
        log.info(tagRepository.findAll().toString());
    }

    @Test
    void 삭제된태그는_재사용되지않음() {
        // given
        Tag tag = tagService.saveOrGetTag("삼겹살");
        tag.setDeleted(true);
        tagRepository.save(tag);

        log.info("tags : {} ",tagRepository.findAll());

        // when
        Tag again = tagService.saveOrGetTag("삼겹살");

        // then
        assertThat(again.getTagId()).isEqualTo(tag.getTagId()); // ✅ 같은 ID
        assertThat(again.isDeleted()).isFalse();                // ✅ 재활성화됨
        log.info("tags : {} ",tagRepository.findAll());
    }
}