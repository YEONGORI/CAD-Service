package com.cad.searh_service.service;

import com.cad.searh_service.entity.Cad;
import com.cad.searh_service.dto.cadDto.CadSaveRequest;
import com.cad.searh_service.repository.CadRepository;
import com.cad.searh_service.util.AsposeUtil;
import com.cad.searh_service.util.S3Util;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CadService {
    private final CadRepository cadRepository;
    private final S3Util s3Util;
    private final AsposeUtil asposeUtil;

    public void saveCadFile(CadSaveRequest request) {
        String folder = request.getS3Url();
        String author = request.getAuthor();
        LocalDateTime dateTime = LocalDateTime.now();

        s3Util.downloadFolder(folder);
        Map<String, String[]> cadInfo = asposeUtil.getCadInfo(folder);

        cadInfo.forEach((key, value) -> {
            String imgUrl = s3Util.encryptImgUrl(value[2]);
            cadRepository.save(
                Cad.builder()
                        .author(author)
                        .mainCategory(folder)
                        .subCategory(value[0])
                        .title(value[1])
                        .index(key)
                        .imgUrl(imgUrl)
                        .createdAt(dateTime)
                        .updatedAt(dateTime)
                        .build()
                );
        });
    }

    public List<Cad> searchCadFile(String searchTerm) {
        return cadRepository.findByMainCategoryContainingOrSubCategoryContainingOrTitleContainingOrIndexContaining(searchTerm, searchTerm, searchTerm, searchTerm);
    }
}
