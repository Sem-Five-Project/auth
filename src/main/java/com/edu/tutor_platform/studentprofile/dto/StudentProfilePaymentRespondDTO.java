package com.edu.tutor_platform.studentprofile.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentProfilePaymentRespondDTO {
    private Double amount;
    private Timestamp paymentTime;
    private String status;
    private String tutorName;
    private String subjectName;
}