package com.edu.tutor_platform.clazz.dto;

import com.edu.tutor_platform.clazz.enums.VerificationEnum;
import java.math.BigDecimal;

public class TutorSubjectRequest {
    private Long tutorId;
    private Long subjectId;
    private VerificationEnum verification;
    private String verificationDocs;
    private BigDecimal hourlyRate;

    // Getters and setters
    public Long getTutorId() { return tutorId; }
    public void setTutorId(Long tutorId) { this.tutorId = tutorId; }
    public Long getSubjectId() { return subjectId; }
    public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }
    public VerificationEnum getVerification() { return verification; }
    public void setVerification(VerificationEnum verification) { this.verification = verification; }
    public String getVerificationDocs() { return verificationDocs; }
    public void setVerificationDocs(String verificationDocs) { this.verificationDocs = verificationDocs; }
    public BigDecimal getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(BigDecimal hourlyRate) { this.hourlyRate = hourlyRate; }
}
