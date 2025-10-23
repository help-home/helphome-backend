package com.example.helphomebackend.repository;

import com.example.helphomebackend.entity.Language;
import com.example.helphomebackend.entity.QLanguage;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class LanguageQueryRepository {

    private final JPAQueryFactory queryFactory;
    private final QLanguage language = QLanguage.language;

    // 동적 검색 쿼리
    public List<Language> searchLanguages(String category, String keyword) {
        BooleanBuilder builder = new BooleanBuilder();

        // 삭제되지 않은 데이터만
        builder.and(language.deletedYn.eq(false));

        // 카테고리 필터
        if (category != null && !category.trim().isEmpty()) {
            builder.and(language.category.eq(category));
        }

        // 키워드 검색 (한국어, 영어, 중국어 이름에서 검색)
        if (keyword != null && !keyword.trim().isEmpty()) {
            BooleanBuilder keywordBuilder = new BooleanBuilder();
            keywordBuilder.or(language.koName.containsIgnoreCase(keyword))
                    .or(language.enName.containsIgnoreCase(keyword))
                    .or(language.chName.containsIgnoreCase(keyword));
            builder.and(keywordBuilder);
        }

        return queryFactory
                .selectFrom(language)
                .where(builder)
                .orderBy(language.category.asc(), language.koName.asc())
                .fetch();
    }

    // 중복검사
    public boolean existsLanguage(String category, String koName, Long excludeId) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(language.category.eq(category))
                .and(language.koName.eq(koName))
                .and(language.deletedYn.eq(false));

        // 수정 시 자기자신 제외
        if (excludeId != null) {
            builder.and(language.id.ne(excludeId));
        }

        return queryFactory
                .selectOne()
                .from(language)
                .where(builder)
                .fetchFirst() != null;
    }
}
