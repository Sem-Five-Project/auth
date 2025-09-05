package com.edu.tutor_platform.user.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "login_attempts")
public class LoginAttempt {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "ip_address", nullable = false)
    private String ipAddress;
    
    @Column(name = "attempts", nullable = false)
    private int attempts;
    
    @Column(name = "last_attempt")
    private LocalDateTime lastAttempt;
    
    @Column(name = "blocked_until")
    private LocalDateTime blockedUntil;
    
    public LoginAttempt() {}
    
    public LoginAttempt(String ipAddress) {
        this.ipAddress = ipAddress;
        this.attempts = 0;
        this.lastAttempt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getIpAddress() {
        return ipAddress;
    }
    
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    
    public int getAttempts() {
        return attempts;
    }
    
    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }
    
    public LocalDateTime getLastAttempt() {
        return lastAttempt;
    }
    
    public void setLastAttempt(LocalDateTime lastAttempt) {
        this.lastAttempt = lastAttempt;
    }
    
    public LocalDateTime getBlockedUntil() {
        return blockedUntil;
    }
    
    public void setBlockedUntil(LocalDateTime blockedUntil) {
        this.blockedUntil = blockedUntil;
    }
    
    public void incrementAttempts() {
        this.attempts++;
        this.lastAttempt = LocalDateTime.now();
    }
    
    public void resetAttempts() {
        this.attempts = 0;
        this.lastAttempt = LocalDateTime.now();
    }
    
    public boolean isBlocked() {
        return blockedUntil != null && blockedUntil.isAfter(LocalDateTime.now());
    }
    
    public boolean isRateLimited(int maxAttempts, long timeWindowMinutes) {
        // Reset attempts if they're older than the time window
        if (lastAttempt != null && lastAttempt.isBefore(LocalDateTime.now().minusMinutes(timeWindowMinutes))) {
            resetAttempts();
        }
        return attempts >= maxAttempts;
    }
}
