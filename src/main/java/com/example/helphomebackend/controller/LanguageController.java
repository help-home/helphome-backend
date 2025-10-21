package com.example.helphomebackend.controller;

import com.example.helphomebackend.entity.Language;
import com.example.helphomebackend.service.LanguageService;
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
    public ResponseEntity<Language> createLanguage(@RequestBody Language language) {
        Language savedLanguage = languageService.saveLanguage(language);
        return ResponseEntity.ok(savedLanguage);
    }

    // 언어 목록 조회
    @GetMapping
    public ResponseEntity<List<Language>> getLanguages() {
        List<Language> languages = languageService.getAllLanguages();
        return ResponseEntity.ok(languages);
    }
}
