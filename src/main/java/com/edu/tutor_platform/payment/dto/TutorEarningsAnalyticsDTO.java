package com.edu.tutor_platform.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TutorEarningsAnalyticsDTO {
    
    private List<MonthlyData> monthly;
    private Double total;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MonthlyData {
        private String x;  // Month name (Jan, Feb, etc.)
        private Double y;  // Earnings amount
    }
}
