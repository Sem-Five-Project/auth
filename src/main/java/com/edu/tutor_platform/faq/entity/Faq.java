package com.edu.tutor_platform.faq.entity;

import com.edu.tutor_platform.faq.enums.FaqCategory;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "faq")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Faq {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "faq_id")
    private Long faqId;

    @Column(nullable = false)
    private String question;

    @Column(nullable = false)
    private String answer;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", columnDefinition = "faq_category")
    private FaqCategory category;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
