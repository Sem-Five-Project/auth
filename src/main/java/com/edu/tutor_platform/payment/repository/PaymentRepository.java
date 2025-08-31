package com.edu.tutor_platform.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.edu.tutor_platform.payment.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    // Optional: findByOrderId(String orderId);
}