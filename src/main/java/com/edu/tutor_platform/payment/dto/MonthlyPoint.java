package com.edu.tutor_platform.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyPoint {
    private String x; // month short name
    private Double y; // amount
}
