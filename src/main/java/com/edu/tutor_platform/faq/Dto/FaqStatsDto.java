package com.edu.tutor_platform.faq.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FaqStatsDto {
    private Long totalFaqs;
    private Long activeFaqs;
    private Long categoryCount;
}
