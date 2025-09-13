package com.RCclone.example.reci.tag.service;

import com.RCclone.example.common.ErrorMsg;
import com.RCclone.example.common.RecipeMapStruct;
import com.RCclone.example.reci.tag.dto.TagDto;
import com.RCclone.example.reci.tag.entity.Tag;
import com.RCclone.example.reci.tag.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagService {
    private final TagRepository tagRepository;
    private final RecipeMapStruct recipeMapStruct;
    private final ErrorMsg errorMsg;

//    태그 저장(신규 + 재사용)
    public Tag saveOrGetTag(String tag){
        return tagRepository.findByTagIgnoreCaseAndDeletedFalse(tag)
                .orElseGet(()->{
                    Tag newTag = new Tag();
                    newTag.setTag(tag);
                    newTag.setDeleted(false);
                    return tagRepository.save(newTag);
                });
    }

//    태그 전체 조회
    public List<TagDto> getAllTags(){
        List<Tag> tags = tagRepository.findByDeletedFalse();
        if(tags.isEmpty()){
            throw new RuntimeException(errorMsg.getMessage("errors.not.found"));
        }
        return recipeMapStruct.toTagDtoList(tags);
    }

//    태그 논리 삭제
    public void tagLogicDelete(Long tagId){
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(()->new RuntimeException(errorMsg.getMessage("errors.not.found")));
        tag.setDeleted(true);
    }

}
