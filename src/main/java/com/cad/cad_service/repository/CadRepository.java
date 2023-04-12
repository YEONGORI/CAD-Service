package com.cad.cad_service.repository;

import com.cad.cad_service.entity.Cad;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CadRepository extends JpaRepository<Cad, Long> {
    List<Cad> findByMainCategoryContainingOrSubCategoryContainingOrTitleContainingOrIndexContaining(String mainCategory, String subCategory, String title, String index);
}
