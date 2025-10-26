package com.edu.tutor_platform.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.edu.tutor_platform.payment.entity.Payment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import java.util.Map;

public interface PaymentRepository extends JpaRepository<Payment, String> {

    // Find payment by order ID
    Optional<Payment> findByOrderId(String orderId);

    // Lookup by external UUID (primary key)
    Optional<Payment> findByPaymentId(String paymentId);

    // Lookup by PayHere payment id
    Optional<Payment> findByPayherePaymentId(String payherePaymentId);

    // Find payments by student ID
    List<Payment> findByStudentId(Long studentId);

    // Find payments by status
    List<Payment> findByStatus(String status);

    // Find expired pending payments for cleanup
    @Query("SELECT p FROM Payment p WHERE p.status = 'PENDING' AND p.expiresAt < :now")
    List<Payment> findExpiredPendingPayments(@Param("now") LocalDateTime now);

    // Find payments by slot ID
    List<Payment> findBySlotId(Long slotId);

    // Find payments by tutor ID
    List<Payment> findByTutorId(Long tutorId);

    // Aggregate monthly earnings for a tutor for payments marked SUCCESS (or
    // completed)
    @Query(value = "SELECT EXTRACT(MONTH FROM p.completed_at) as month, SUM(p.amount) as total " +
            "FROM public.payment p " +
            "WHERE p.tutor_id = :tutorId AND p.status = 'SUCCESS' " +
            "GROUP BY EXTRACT(MONTH FROM p.completed_at)", nativeQuery = true)
    List<Object[]> sumAmountGroupedByMonth(@Param("tutorId") Long tutorId);

    // Check if payment exists for slot and student
    @Query("SELECT COUNT(p) > 0 FROM Payment p WHERE p.slotId = :slotId AND p.studentId = :studentId AND p.status = 'PENDING'")
    Boolean hasPendingPaymentForSlot(@Param("slotId") Long slotId, @Param("studentId") Long studentId);

    // Call Supabase function repay_and_reassign_class using named parameters
    @Query(value = """
            SELECT repay_and_reassign_class(
                :paymentId,
                :classId,
                :studentId,
                :paymentTime,
                :amount,
                :month,
                :year,
                CAST(:slotsJson AS jsonb),
                CAST(:nextMonthSlotsArray AS bigint[])
            )
            """, nativeQuery = true)
    String callRepayAndReassignClass(
            @Param("paymentId") String paymentId,
            @Param("classId") Long classId,
            @Param("studentId") Long studentId,
            @Param("paymentTime") LocalDateTime paymentTime,
            @Param("amount") BigDecimal amount,
            @Param("month") Short month,
            @Param("year") Short year,
            @Param("slotsJson") String slotsJson,
            @Param("nextMonthSlotsArray") String nextMonthSlotsArray);
}