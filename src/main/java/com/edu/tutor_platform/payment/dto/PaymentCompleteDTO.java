package com.edu.tutor_platform.payment.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentCompleteDTO {
    private String paymentId;                    // TEXT
    private Map<String, List<Long>> slots;      // JSONB shape: { "availability_id": [slot_ids...] }
    private Long tutorId;                       // BIGINT
    private Long subjectId;                     // BIGINT
    private Long languageId;                    // BIGINT
    private Long classTypeId;                   // BIGINT
    private Long studentId;                     // BIGINT
    private LocalDateTime paymentTime;          // TIMESTAMP
    private BigDecimal amount;                  // NUMERIC
    private Integer month;                      // SMALLINT
    private Integer year;                       // SMALLINT
}
