package com.example.helphomebackend.repository;

import com.example.helphomebackend.entity.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LanguageRepository extends JpaRepository<Language, Long> {
    // 중복 검사
    boolean existsByCategoryAndKoNameAndDeletedFalse(String category, String koName);

    // 논리 삭제된 것 제외한 조회
    List<Language> findByDeletedFalse();

    // 카테고리 별 조회
    List<Language> findByCategoryAndDeletedFalse(String category);
}
