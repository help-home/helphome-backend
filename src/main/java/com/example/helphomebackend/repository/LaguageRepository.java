package com.example.helphomebackend.repository;

import com.example.helphomebackend.entity.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LaguageRepository extends JpaRepository<Language, Long> {
    Optional<Language> findByKoName(String koName);
}
