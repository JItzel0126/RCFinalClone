package com.RCclone.example.reci.tag.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TagDto {
    private Long tagId;
    private String tag;

//    등록용(id없이 tag 이름만)
    public TagDto(String tag) {
        this.tag = tag;
    }

}
