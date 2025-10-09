package com.edu.tutor_platform.faq.Dto;

import com.edu.tutor_platform.faq.enums.FaqCategory;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class FaqDto {
    private Long faqId;
    private String question;
    private String answer;
    private FaqCategory category;
    private Boolean isActive;
    private LocalDateTime createdAt;
}
