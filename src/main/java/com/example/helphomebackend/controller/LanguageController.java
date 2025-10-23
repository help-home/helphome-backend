package com.example.helphomebackend.controller;

import com.example.helphomebackend.entity.Language;
import com.example.helphomebackend.service.LanguageService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/languages")
public class LanguageController {

    private final LanguageService languageService;

    public LanguageController(LanguageService languageService) {
        this.languageService = languageService;
    }

    // 언어 저장
    @PostMapping
    public ResponseEntity<Language> createLanguage(@Valid @RequestBody Language language) {
        Language savedLanguage = languageService.saveLanguage(language);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedLanguage);
    }

    // 언어 목록 조회 (삭제된 것 포함)
    @GetMapping
    public ResponseEntity<List<Language>> getLanguages() {
        List<Language> languages = languageService.getAllLanguages();
        return ResponseEntity.ok(languages);
    }

    // 활성 언어만 조회 (삭제된 것 제외)
    @GetMapping("/active")
    public ResponseEntity<List<Language>> getActiveLanguages() {
        List<Language> languages = languageService.getLanguages();
        return ResponseEntity.ok(languages);
    }

    // 동적 검색
    @GetMapping("/search")
    public ResponseEntity<List<Language>> searchLanguages(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword) {

        List<Language> languages = languageService.searchLanguages(category, keyword);
        return ResponseEntity.ok(languages);
    }
}
