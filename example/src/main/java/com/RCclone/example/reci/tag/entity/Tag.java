package com.RCclone.example.reci.tag.entity;

import com.RCclone.example.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "tag")
@EqualsAndHashCode(of = "tagId", callSuper = false)
public class Tag extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tagId;             // PK
    private String tag;
    boolean deleted=false;
}
