package com.example.helphomebackend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "languages")
@Getter @Setter
public class Language {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "카테고리는 필수입니다.")
    @Column(nullable = false)
    private String category;

    @NotBlank(message = "한국어 이름은 필수입니다.")
    @Size(max = 50, message = "한국어 이름은 50자를 초과할 수 없습니다.")
    @Column(nullable = false, length = 50)
    private String koName;

    @Size(max = 50, message = "영어 이름은 50자를 초과할 수 없습니다.")
    @Column(length = 50)
    private String enName;

    @Size(max = 50, message = "중국어 이름은 50자를 초과할 수 없습니다.")
    @Column(length = 50)
    private String chName;

    @Column(name = "created_At", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_At")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "deleted_yn")
    private boolean deletedYn = false;

    @PrePersist
    protected void onCreated() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // 논리 삭제
    public void delete() {
        this.deletedYn = true;
        this.deletedAt = LocalDateTime.now();
    }
}
