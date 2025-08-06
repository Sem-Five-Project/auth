package com.edu.tutor_platform.user.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;

}
