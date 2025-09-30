package com.edu.tutor_platform.adminprofile.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class paymentDto {
    private int monthOrYear;
    private float totalPayments;
}
