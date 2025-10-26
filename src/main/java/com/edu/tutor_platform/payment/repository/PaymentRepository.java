package com.edu.tutor_platform.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.edu.tutor_platform.payment.entity.Payment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;

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
    
    // Check if payment exists for slot and student
    @Query("SELECT COUNT(p) > 0 FROM Payment p WHERE p.slotId = :slotId AND p.studentId = :studentId AND p.status = 'PENDING'")
    Boolean hasPendingPaymentForSlot(@Param("slotId") Long slotId, @Param("studentId") Long studentId);

    // Find successful payments by tutor ID and year for analytics
    @Query("SELECT p FROM Payment p WHERE p.tutorId = :tutorId " +
           "AND p.status = 'SUCCESS' " +
           "AND YEAR(p.completedAt) = :year " +
           "ORDER BY p.completedAt")
    List<Payment> findSuccessfulPaymentsByTutorIdAndYear(
            @Param("tutorId") Long tutorId, 
            @Param("year") int year
    );

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
            @Param("nextMonthSlotsArray") String nextMonthSlotsArray
    );
}