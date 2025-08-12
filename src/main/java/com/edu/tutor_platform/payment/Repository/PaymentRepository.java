package com.edu.tutor_platform.payment.Repository;

import com.edu.tutor_platform.payment.Entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByOrderId(UUID orderId);
    
    @Query("SELECT CASE WHEN COUNT(sp) > 0 THEN true ELSE false END FROM StudentProfile sp WHERE sp.studentId = :studentId")
    boolean existsByStudentId(@Param("studentId") Long studentId);
    
    @Modifying
    @Transactional
    @Query("UPDATE TutorProfile tp SET tp.balance = tp.balance + :amount WHERE tp.tutorId = :tutorId")
    void updateTutorBalance(@Param("tutorId") Long tutorId, @Param("amount") BigDecimal amount);
}