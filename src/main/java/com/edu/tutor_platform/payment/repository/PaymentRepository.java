package com.edu.tutor_platform.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.edu.tutor_platform.payment.entity.Payment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, String> {
    
    // Find payment by order ID
    Optional<Payment> findByOrderId(String orderId);
    // Lookup by external UUID (primary key)
    Optional<Payment> findByPaymentId(String paymentId);
    
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
}