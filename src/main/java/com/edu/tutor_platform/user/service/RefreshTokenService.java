package com.edu.tutor_platform.user.service;

import com.edu.tutor_platform.user.entity.RefreshToken;
import com.edu.tutor_platform.user.entity.User;
import com.edu.tutor_platform.user.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {
    
    @Value("${jwt.refresh-token-expiration}")
    private Long refreshTokenDurationMs;
    
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }
    
    public Optional<RefreshToken> findByUserId(Long userId) {
        return refreshTokenRepository.findByUserId(userId);
    }
    
    public RefreshToken createRefreshToken(User user) {
        // Delete existing refresh token for the user
        refreshTokenRepository.findByUser(user).ifPresent(refreshTokenRepository::delete);
        
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(LocalDateTime.now().plusSeconds(refreshTokenDurationMs / 1000));
        refreshToken.setToken(UUID.randomUUID().toString());
        
        refreshToken = refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }
    
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.isExpired()) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token was expired. Please make a new signin request");
        }
        return token;
    }
    
    @Transactional
    public void deleteByUser(User user) {
        refreshTokenRepository.deleteByUser(user);
    }
    
    @Transactional
    public void deleteExpiredTokens() {
        refreshTokenRepository.deleteExpiredTokens();
    }
}

