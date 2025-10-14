package com.edu.tutor_platform.payment.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Payload model for PayHere custom_2 when type = "repay" (next-month payment).
 */
public class PaymentRepayDTO {
    private String type; // expected "repay"
    private Long classId;
    private Long studentId;
    private String paymentTime; // ISO string (may include Z)
    private BigDecimal amount;
    private Integer month;
    private Integer year;
    private String paymentId;
    private Map<String, List<Long>> slots; // { availabilityId: [slotIds...] }
    private List<Long> nextMonthSlots;     // optional

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Long getClassId() { return classId; }
    public void setClassId(Long classId) { this.classId = classId; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public String getPaymentTime() { return paymentTime; }
    public void setPaymentTime(String paymentTime) { this.paymentTime = paymentTime; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public Integer getMonth() { return month; }
    public void setMonth(Integer month) { this.month = month; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }

    public Map<String, List<Long>> getSlots() { return slots; }
    public void setSlots(Map<String, List<Long>> slots) { this.slots = slots; }

    public List<Long> getNextMonthSlots() { return nextMonthSlots; }
    public void setNextMonthSlots(List<Long> nextMonthSlots) { this.nextMonthSlots = nextMonthSlots; }
}
