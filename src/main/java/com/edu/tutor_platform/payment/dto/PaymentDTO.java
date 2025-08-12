// package com.edu.tutor_platform.payment.dto;

// import jakarta.validation.constraints.DecimalMin;
// import jakarta.validation.constraints.NotBlank;
// import jakarta.validation.constraints.NotNull;
// import lombok.Data;

// import java.math.BigDecimal;

// @Data
// public class PaymentDTO {
//     @NotNull(message = "Student ID is required")
//     private Long studentId;

//     @NotNull(message = "Teacher ID is required")
//     private Long teacherId;

//     @NotNull(message = "Amount is required")
//     @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
//     private BigDecimal amount;

//     @NotBlank(message = "Currency is required")
//     private String currency;
// }

package com.edu.tutor_platform.payment.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentDTO {
    @NotNull(message = "Student ID is required")
    private Long studentId;

    @NotNull(message = "Session ID is required")
    private Long sessionId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    @NotBlank(message = "Currency is required")
    private String currency;

    @NotBlank(message = "Payment method is required")
    private String paymentMethod;

    @NotBlank(message = "Payment type is required")
    private String paymentType;

    // Customer information (required by PayHere)
    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Phone is required")
    private String phone;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "Country is required")
    private String country;
}