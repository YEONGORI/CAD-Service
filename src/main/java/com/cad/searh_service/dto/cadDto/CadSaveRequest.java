package com.cad.searh_service.dto.cadDto;

import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public class CadSaveRequest {
    private String s3Url;
    private String author;
}
