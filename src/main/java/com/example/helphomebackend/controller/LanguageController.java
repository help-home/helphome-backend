package com.example.helphomebackend.controller;

import com.example.helphomebackend.entity.Language;
import com.example.helphomebackend.service.LanguageService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/languages")
public class LanguageController {

    private final LanguageService languageService;

    public LanguageController(LanguageService languageService) {
        this.languageService = languageService;
    }

    // 언어 저장
    @PostMapping
    public ResponseEntity<?> createLanguage(@Valid @RequestBody Language language) {
        try {
            Language savedLanguage = languageService.saveLanguage(language);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedLanguage);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }

    }

    // 언어 목록 조회
    @GetMapping
    public ResponseEntity<List<Language>> getLanguages() {
        List<Language> languages = languageService.getAllLanguages();
        return ResponseEntity.ok(languages);
    }
}
