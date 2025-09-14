package com.RCclone.example.reci.feed.recipes.controller;

import com.RCclone.example.reci.auth.entity.Member;
import com.RCclone.example.reci.feed.ingredient.dto.IngredientDto;
import com.RCclone.example.reci.feed.recipeContent.dto.RecipeContentDto;
import com.RCclone.example.reci.feed.recipeContent.entity.RecipeContent;
import com.RCclone.example.reci.feed.recipeContent.service.RecipeContentService;
import com.RCclone.example.reci.feed.recipes.dto.RecipesDto;
import com.RCclone.example.reci.feed.recipes.entity.Recipes;
import com.RCclone.example.reci.feed.recipes.service.RecipesService;
import com.RCclone.example.reci.tag.dto.TagDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2
public class RecipesController {
    private final RecipesService recipesService;
    private final RecipeContentService recipeContentService;

    /** 레시피 등록 폼 이동 */
    @GetMapping("/recipes/add")
    public String createForm() {
        return "feed/recipe_add"; // JSP or Thymeleaf 템플릿
    }

    @PostMapping(path = "/recipes", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String createRecipe(
            @ModelAttribute RecipesDto recipesDto,                        // ingredients[i].*, contents[i].*, tags[i]
            @RequestParam(value="thumbnail", required=false) MultipartFile thumbnail,
            @RequestParam(value="stepImages", required=false) List<MultipartFile> stepImages,
            Principal principal
    ) throws Exception {

        Member member = new Member();
        member.setUserEmail(principal != null ? principal.getName() : "anonymous@local");

        byte[] thumbnailBytes = (thumbnail != null && !thumbnail.isEmpty()) ? thumbnail.getBytes() : null;

        List<byte[]> images = new ArrayList<>();
        if (stepImages != null) for (MultipartFile f : stepImages)
            if (f != null && !f.isEmpty()) images.add(f.getBytes());

        // tags(List<String>) → TagDto
        List<TagDto> tagDtos = (recipesDto.getTags() != null) ? recipesDto.getTags() : new ArrayList<>();

        // sortOrder/stepOrder 보정
        List<IngredientDto> ingredientDtos =
                recipesDto.getIngredients() != null ? recipesDto.getIngredients() : new ArrayList<>();
        for (int i = 0; i < ingredientDtos.size(); i++)
            if (ingredientDtos.get(i).getSortOrder() == null) ingredientDtos.get(i).setSortOrder((long)(i+1));

        List<RecipeContentDto> contentDtos =
                recipesDto.getContents() != null ? recipesDto.getContents() : new ArrayList<>();
        for (int i = 0; i < contentDtos.size(); i++)
            if (contentDtos.get(i).getStepOrder() == null) contentDtos.get(i).setStepOrder((long)(i+1)); // 프로젝트 필드명에 맞게

        String uuid = recipesService.createRecipe(
                recipesDto, ingredientDtos, contentDtos, images, tagDtos, thumbnailBytes,
                recipesDto.getThumbnailUrl(), // 없으면 null
                member
        );
        return "redirect:/recipes/" + uuid;
    }

    @GetMapping("recipes/download")
    @ResponseBody
    public ResponseEntity<byte[]> downloadThumbnail(@RequestParam("uuid") String uuid) {
    Recipes recipes = recipesService.findById(uuid);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentDispositionFormData("attachment",recipes.getUuid());
    headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
    return new ResponseEntity<byte[]>(recipes.getThumbnail(),
                                      headers, HttpStatus.OK);
    }

    // 단계 이미지
    @GetMapping(value = "/recipes/content/download")
    public ResponseEntity<byte[]> downloadStep(@RequestParam Long stepId) {
        RecipeContent recipeContent = recipeContentService.findById(stepId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment", recipeContent.getStepId().toString());
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        return new ResponseEntity<byte[]>(recipeContent.getRecipeImage(),
                                       headers, HttpStatus.OK);
    }

    /** ✅ 상세 페이지 (리다이렉트 목적지) */
    @GetMapping("/recipes/{uuid}")
    public String detail(@PathVariable String uuid, Model model) {
        // 선택: 조회수 +1
        // recipesService.increaseViewCount(uuid);

        // 서비스에서 DTO로 가져와 뷰에 그대로 바인딩
        RecipesDto dto = recipesService.getRecipeDetails(uuid); // 서비스에 맞게 메서드명 조정

        String insertTime = "";
        if (dto.getInsertTime() != null) {
            insertTime = dto.getInsertTime()
                    .format(java.time.format.DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"));
        }

        model.addAttribute("recipe", dto);
        model.addAttribute("insertTime", insertTime);
        return "feed/recipe_details"; // JSP 경로
    }

}
