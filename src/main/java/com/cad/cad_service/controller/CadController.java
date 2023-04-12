package com.cad.cad_service.controller;

import com.cad.cad_service.entity.Cad;
import com.cad.cad_service.dto.CadSaveRequest;
import com.cad.cad_service.service.CadService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cad")
public class CadController {
    private final CadService cadService;
    private final Logger log = LoggerFactory.getLogger(CadController.class);

    @GetMapping("/data")
    public ResponseEntity<List<Cad>> searchCad(@RequestParam String searchTerm) {
        try {
            if (searchTerm.isEmpty())
                throw new IllegalArgumentException("Parameter is not entered.");
            List<Cad> result = cadService.searchCadFile(searchTerm);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            log.error("Invalid input parameter: " + searchTerm, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            log.error("Internal server error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/data")
    public ResponseEntity<Void> saveCad(@RequestBody CadSaveRequest request) {

        try {
            if (request == null)
                throw new IllegalArgumentException("RequestBody is not entered");
            cadService.saveCadFile(request);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            log.error("Invalid input parameter: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            log.error("Internal server error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}