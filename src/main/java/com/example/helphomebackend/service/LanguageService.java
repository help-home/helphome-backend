package com.example.helphomebackend.service;

import com.example.helphomebackend.entity.Language;
import com.example.helphomebackend.repository.LaguageRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LanguageService {

    private final LaguageRepository laguageRepository;

    public LanguageService(LaguageRepository laguageRepository) {
        this.laguageRepository = laguageRepository;
    }

    public Language saveLanguage(Language language) {
        // 중복 체크 (한국어 기준)
        if (laguageRepository.findByKoName(language.getKoName()).isPresent()) {
            throw new RuntimeException("이미 존재하는 언어입니다: " + language.getKoName());
        }
        return laguageRepository.save(language);
    }

    public List<Language> getAllLanguages() {
        return laguageRepository.findAll();
    }
}
