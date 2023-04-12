package com.cad.searh_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Entity
public class Cad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String author;
    @Column(name = "maincategory")
    private String mainCategory;
    @Column(name = "subcategory")
    private String subCategory;
    private String title;
    @Column(name = "cadindex")
    private String index;
    @Column(name = "imgurl")
    private String imgUrl;
    @Column(name = "createdat")
    private LocalDateTime createdAt;
    @Column(name = "updatedat")
    private LocalDateTime updatedAt;
}
