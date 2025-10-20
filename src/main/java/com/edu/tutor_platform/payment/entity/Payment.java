// package com.edu.tutor_platform.payment.entity;

// import com.edu.tutor_platform.booking.entity.Booking;
// import jakarta.persistence.*;
// import lombok.AllArgsConstructor;
// import lombok.Builder;
// import lombok.Data;
// import lombok.NoArgsConstructor;

// import java.time.LocalDateTime;

// @Entity
// @Table(name = "payment")
// @Data
// @NoArgsConstructor
// @AllArgsConstructor
// @Builder
// public class Payment {
    
//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     @Column(name = "payment_id")
//     private Long paymentId;
    
//     @Column(name = "order_id", unique = true, nullable = false)
//     private String orderId;
    
//     @Column(name = "amount", nullable = false)
//     private double amount;
    
//     @Builder.Default
//     @Column(name = "currency", nullable = false)
//     private String currency = "LKR";
    
//     @Column(name = "status", nullable = false)
//     private String status;  // "PENDING", "SUCCESS", "FAILED", "EXPIRED"
    
//     @Column(name = "class_id")
//     private Long classId;
    
//     @Column(name = "student_id", nullable = false)
//     private Long studentId;
    
//     @Column(name = "tutor_id")
//     private Long tutorId;
    
//     @Column(name = "availability_id")
//     private Long availabilityId;
    
//     @Column(name = "slot_id")
//     private Long slotId;
    
//     @Builder.Default
//     @Column(name = "created_at")
//     private LocalDateTime createdAt = LocalDateTime.now();
    
//     @Column(name = "completed_at")
//     private LocalDateTime completedAt;
    
//     @Column(name = "expires_at")
//     private LocalDateTime expiresAt;
    
//     // Relationship with Booking (one payment can have one booking)
//     @OneToOne(mappedBy = "payment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//     private Booking booking;
    
//     // PayHere specific fields
//     @Column(name = "payhere_payment_id")
//     private String payherePaymentId;
    
//     @Column(name = "payment_method")
//     private String paymentMethod;
    
//     @Column(name = "card_holder_name")
//     private String cardHolderName;
// }


package com.edu.tutor_platform.payment.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payment")
@Data                
@NoArgsConstructor  
@AllArgsConstructor  
@Builder             
public class Payment {

    @Id
    @Column(name = "payment_id", nullable = false, updatable = false, unique = true)
    private String paymentId;

    @Column(nullable = false)
    private Long studentId;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private String paymentMethod;

    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    private LocalDateTime paymentTime;
    private Long availabilityId;
    private String cardHolderName;
    private Long classId;
    private LocalDateTime completedAt;
    private LocalDateTime createdAt;
    private String currency;
    private LocalDateTime expiresAt;

    @Column(nullable = false, unique = true)
    private String orderId;

    @Column(name = "payhere_payment_id")
    private String payherePaymentId;
    private Long slotId;

    @Column(name = "status")
    private String status;
    private Long tutorId;

    @PrePersist
    private void ensureId() {
        if (this.paymentId == null || this.paymentId.isBlank()) {
            this.paymentId = UUID.randomUUID().toString();
        }
    }
}
