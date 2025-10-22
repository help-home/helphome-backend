package com.example.helphomebackend.service;

import com.example.helphomebackend.entity.Language;
import com.example.helphomebackend.enums.LanguageCategory;
import com.example.helphomebackend.repository.LanguageRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class LanguageService {

    private final LanguageRepository languageRepository;

    public LanguageService(LanguageRepository languageRepository) {
        this.languageRepository = languageRepository;
    }

    public Language saveLanguage(Language language) {
        // 비즈니스 규칙 검증(카테고리 등)
        validateBusinessRules(language);

        // 동일한 카테고리 내에서 한국어 이름 중복검사
        checkDuplicate(language);

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
            throw new IllegalArgumentException(
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
            throw new IllegalArgumentException("유효하지 않은 카테고리입니다: " + language.getCategory());
        }

        // 언어명 형식 검사 (예: 특수문자 제한)
        if (!language.getKoName().matches("^[가-힣a-zA-Z0-9\\s\\-\\+#\\.]*$")) {
            throw new IllegalArgumentException("언어 이름에 허용되지 않은 문자가 포함되어 있습니다.");
        }
    }
}
