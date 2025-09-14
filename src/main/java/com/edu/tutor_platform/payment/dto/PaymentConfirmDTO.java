package com.edu.tutor_platform.payment.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
public class PaymentConfirmDTO {

    
    @NotNull
    private String paymentId; // Internal payment ID (UUID)

    @NotNull
    private Long tutorId;

    @NotNull
    private Long slotId;

    @NotNull
    private Long subjectId;

    @NotNull
    private Long languageId;

    @NotNull
    private Long classTypeId;
}
