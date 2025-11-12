package com.example.helphomebackend.service;

import com.example.helphomebackend.entity.Language;
import com.example.helphomebackend.enums.LanguageCategory;
import com.example.helphomebackend.exception.DuplicateResourceException;
import com.example.helphomebackend.exception.InvalidCategoryException;
import com.example.helphomebackend.repository.LanguageQueryRepository;
import com.example.helphomebackend.repository.LanguageRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LanguageService {

    private final LanguageRepository languageRepository;
    private final LanguageQueryRepository languageQueryRepository;

    public LanguageService(LanguageRepository languageRepository, LanguageQueryRepository languageQueryRepository) {
        this.languageRepository = languageRepository;
        this.languageQueryRepository = languageQueryRepository;
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
        return languageRepository.findByDeletedYnFalse();
    }

    // 동적 검색 기능
    public List<Language> searchLanguages(String category, String keyword) {
        return languageQueryRepository.searchLanguages(category, keyword);
    }

    // 수정
    public Language updateLanguage(Long id, Language updateLanguage) {
        // 기존 언어 조회
        Language existingLanguage = languageRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("해당 ID의 언어를 찾을 수 없습니다. ID: " + id));

        // 수정이 불가능한 필드 보존
        updateLanguage.setId(existingLanguage.getId());
        updateLanguage.setCreatedAt(existingLanguage.getCreatedAt());
        updateLanguage.setDeletedYn(existingLanguage.isDeletedYn());

        // 전체 업데이트 시 모든 필드 검증
        validateBusinessRules(updateLanguage);

        // 선택적 필드 업데이트 로직
        if (updateLanguage.getCategory() != null) {
            existingLanguage.setCategory(updateLanguage.getCategory().toLowerCase().trim());
        }

        // 한국어 이름 업데이트
        Optional.ofNullable(updateLanguage.getKoName())
                .ifPresent(name -> {
                    validateNameFormat(name, "한국어");
                    existingLanguage.setKoName(name.trim());
                });

        // 영어 이름 업데이트
        Optional.ofNullable(updateLanguage.getEnName())
                .ifPresent(name -> {
                    validateNameFormat(name, "영어");
                    existingLanguage.setEnName(name.trim());
                });

        // 중국어 이름 업데이트
        Optional.ofNullable(updateLanguage.getChName())
                .ifPresent(name -> {
                    validateNameFormat(name, "중국어");
                    existingLanguage.setChName(name.trim());
                });

        // 중복검사
        checkDuplicate(existingLanguage);

        return languageRepository.save(existingLanguage);
    }

    // TODO: 삭제로직 추가
    // 논리삭제
    public void deleteLanguage(Long id) {
        Language language = languageRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("삭제하시려는 언어를 찾을 수 없습니다." + id));

        // 이미 삭제 된 언어일 경우
        if (language.isDeletedYn()) {
            throw new IllegalStateException("이미 삭제된 언어 입니다. ID:" + id);
        }

        // 논리삭제 메서드 호출
        language.delete();

        // 저장
        languageRepository.save(language);
    }

    // 동일한 카테고리 내에서 한국어 이름 중복 검사
    private void checkDuplicate(Language language) {
        boolean exists = languageQueryRepository.existsLanguage(
                language.getCategory(),
                language.getKoName(),
                language.getId()
        );

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

        // 특수문자나 숫자 제한
        validateNameFormat(language.getKoName(), "한국어");
        validateNameFormat(language.getEnName(), "영어");
        validateNameFormat(language.getChName(), "중국어");
    }

    // 포멧검사
    private void validateNameFormat(String name, String language) {
        // 이름이 공백인 경우 검사 제외
        if (name == null || name.trim().isEmpty()) {
            return;
        }

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
