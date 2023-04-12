package com.cad.cad_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public class CadSaveRequest {
    private String s3Url;
    private String author;
}
