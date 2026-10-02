package com.streetconnect.controller;

import com.streetconnect.dto.ApiResponse;
import com.streetconnect.dto.StreetDto;
import com.streetconnect.service.StreetService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/streets")
public class StreetController {

    private final StreetService streetService;

    @Autowired
    public StreetController(StreetService streetService) {
        this.streetService = streetService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<StreetDto>>> getAllStreets() {
        List<StreetDto> streets = streetService.getAllStreets();
        return ResponseEntity.ok(ApiResponse.success("Streets retrieved successfully", streets));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StreetDto>> getStreetById(@PathVariable Long id) {
        StreetDto street = streetService.getStreetById(id);
        return ResponseEntity.ok(ApiResponse.success("Street retrieved successfully", street));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<StreetDto>> createStreet(@Valid @RequestBody StreetDto streetDto) {
        StreetDto created = streetService.createStreet(streetDto);
        return ResponseEntity.ok(ApiResponse.success("Street created successfully", created));
    }
}
