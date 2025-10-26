package com.edu.tutor_platform.payment.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.boot.test.mock.mockito.SpyBean;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.edu.tutor_platform.payment.entity.Payment;
import com.edu.tutor_platform.payment.repository.PaymentRepository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;

@SpringBootTest
@ActiveProfiles("test")
class PaymentServiceTest {

    @SuppressWarnings("removal")
    @SpyBean
    private PaymentService paymentService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${payhere.merchant-secret}")
    private String merchantSecret;

    @Test
    @Transactional
    void handleNotify_shouldPersistPayherePaymentIdOnSuccess() throws JsonProcessingException {
        // Arrange
        String orderId = "ORDER-123";

        Payment payment = Payment.builder()
                .orderId(orderId)
                .studentId(1L)
                .amount(1500.00)
                .paymentMethod("PAYHERE")
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .build();
        paymentRepository.saveAndFlush(payment);

        Map<String, String> payload = buildSuccessfulPayload(orderId, "1500.00", "LKR", "2", "PH-987654321");
        payload.put("custom_1", buildConfirmPayloadJson(payment));

        doReturn("BOOKED").when(paymentService).completePayment(
                anyString(),
                anyString(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any());

        // Act
        paymentService.handleNotify(payload);

        // Assert
        Payment updated = paymentRepository.findByOrderId(orderId).orElseThrow();
        assertThat(updated.getPayherePaymentId()).isEqualTo("PH-987654321");
        assertThat(updated.getStatus()).isEqualTo("SUCCESS");
        assertThat(updated.getPaymentMethod()).isEqualTo("VISA");
        assertThat(updated.getCardHolderName()).isEqualTo("Test User");
    }

    @Test
    @Transactional
    void getTutorEarningsAnalytics_shouldAggregatePerMonthAndTotal() {
        // Arrange - create multiple payments for tutor 10 across different months
        Long tutorId = 10L;

        // May: 16800
        Payment p1 = Payment.builder()
                .orderId("ORD-MAY-1")
                .studentId(2L)
                .amount(16800.0)
                .paymentMethod("PAYHERE")
                .status("SUCCESS")
                .completedAt(LocalDateTime.of(2025, 5, 15, 10, 0))
                .tutorId(tutorId)
                .build();

        // Jun: 19400
        Payment p2 = Payment.builder()
                .orderId("ORD-JUN-1")
                .studentId(3L)
                .amount(19400.0)
                .paymentMethod("PAYHERE")
                .status("SUCCESS")
                .completedAt(LocalDateTime.of(2025, 6, 8, 12, 0))
                .tutorId(tutorId)
                .build();

        // Jul: 21000
        Payment p3 = Payment.builder()
                .orderId("ORD-JUL-1")
                .studentId(4L)
                .amount(21000.0)
                .paymentMethod("PAYHERE")
                .status("SUCCESS")
                .completedAt(LocalDateTime.of(2025, 7, 2, 9, 0))
                .tutorId(tutorId)
                .build();

        paymentRepository.saveAndFlush(p1);
        paymentRepository.saveAndFlush(p2);
        paymentRepository.saveAndFlush(p3);

        // Act
        com.edu.tutor_platform.payment.dto.TutorEarningsAnalyticsDTO res = paymentService
                .getTutorEarningsAnalytics(tutorId);

        // Assert
        assertThat(res).isNotNull();
        assertThat(res.getMonthly()).hasSize(12);
        assertThat(res.getMonthly().get(4).getY()).isEqualTo(16800.0); // May (index 4)
        assertThat(res.getMonthly().get(5).getY()).isEqualTo(19400.0); // Jun
        assertThat(res.getMonthly().get(6).getY()).isEqualTo(21000.0); // Jul
        assertThat(res.getTotal()).isEqualTo(16800.0 + 19400.0 + 21000.0);
    }

    private Map<String, String> buildSuccessfulPayload(String orderId, String amount, String currency,
            String statusCode, String paymentId) {
        Map<String, String> payload = new HashMap<>();
        payload.put("merchant_id", "TEST_ID");
        payload.put("order_id", orderId);
        payload.put("payhere_amount", amount);
        payload.put("payhere_currency", currency);
        payload.put("status_code", statusCode);
        payload.put("payment_id", paymentId);
        payload.put("payment_method", "VISA");
        payload.put("card_holder_name", "Test User");

        String md5Secret = DigestUtils.md5Hex(merchantSecret).toUpperCase();
        String hashInput = payload.get("merchant_id") + orderId + amount + currency + statusCode + md5Secret;
        String signature = DigestUtils.md5Hex(hashInput).toUpperCase();
        payload.put("md5sig", signature);

        return payload;
    }

    private String buildConfirmPayloadJson(Payment payment) throws JsonProcessingException {
        Map<String, Object> confirm = new HashMap<>();
        confirm.put("paymentId", payment.getPaymentId());
        confirm.put("slots", Map.of("1001", List.of(2001L)));
        confirm.put("tutorId", 10L);
        confirm.put("subjectId", 11L);
        confirm.put("languageId", 12L);
        confirm.put("classTypeId", 13L);
        confirm.put("studentId", payment.getStudentId());
        confirm.put("paymentTime", LocalDateTime.now());
        confirm.put("amount", 1500.00);
        confirm.put("month", 10);
        confirm.put("year", 2025);
        confirm.put("nextMonthSlots", List.of(423L, 424L, 425L));
        return objectMapper.writeValueAsString(confirm);
    }
}