package com.example.helphomebackend.service;

import com.example.helphomebackend.entity.Language;
import com.example.helphomebackend.enums.LanguageCategory;
import com.example.helphomebackend.exception.DuplicateResourceException;
import com.example.helphomebackend.exception.InvalidCategoryException;
import com.example.helphomebackend.repository.LanguageRepository;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class LanguageService {

    private final LanguageRepository languageRepository;

    public LanguageService(LanguageRepository languageRepository) {
        this.languageRepository = languageRepository;
    }

    public Language saveLanguage(Language language) {
        // 비즈니스 규칙 검증(카테고리 유효성 검사, 언어명 유효성 검사)
        validateBusinessRules(language);

        // 동일한 카테고리 내에서 한국어 이름 중복검사
        checkDuplicate(language);

        // 데이터 정규화
        normalizeData(language);

        return languageRepository.save(language);
    }

    // 삭제 포함 전체 다국어 조회
    public List<Language> getAllLanguages() {
        return languageRepository.findAll();
    }

    // 삭제된 다국어 제외한 조회
    public List<Language> getLanguages() {
        return languageRepository.findByDeletedFalse();
    }

    // 동일한 카테고리 내에서 한국어 이름 중복 검사
    private void checkDuplicate(Language language) {
        boolean exists = languageRepository.existsByCategoryAndKoNameAndDeletedFalse(language.getCategory(), language.getKoName());

        if (exists) {
            throw new DuplicateResourceException(
                    String.format("이미 존재하는 언어입니다. (카테고리: %s, 이름: %s)\n",
                            language.getCategory(), language.getKoName()
                            )
            );
        }
    }

    // 비즈니스 규칙 검사
    private void validateBusinessRules(Language language) {
        // 카테고리 유효성 검사
        if (!LanguageCategory.isValid(language.getCategory())) {
            throw new InvalidCategoryException(language.getCategory());
        }

        // 언어명이 서로 다른지 검사
        if (language.getKoName().equalsIgnoreCase(language.getEnName()) ||
                language.getKoName().equalsIgnoreCase(language.getChName()) ||
                language.getEnName().equalsIgnoreCase(language.getChName())) {
            throw new IllegalArgumentException("각 언어별 이름은 서로 달라야 합니다.");
        }

        // 특수문자나 숫자 제한
        validateNameFormat(language.getKoName(), "한국어");
        validateNameFormat(language.getEnName(), "영어");
        validateNameFormat(language.getChName(), "중국어");
    }

    // 포멧검사
    private void validateNameFormat(String name, String language) {
        if (name.matches(".*\\\\d.*")) {
            throw new IllegalArgumentException(language + "이름에 허용되지 않는 문자가 포함되어 있습니다.");
        }
    }

    // 이름 정규화
    private void normalizeData(Language language) {
        // 카테고리 소문자 통일
        language.setCategory(language.getCategory().toLowerCase().trim());

        // 이름 앞뒤 공백 제거
        language.setKoName(language.getKoName().trim());
        language.setEnName(language.getEnName().trim());
        language.setChName(language.getChName().trim());
    }
}
