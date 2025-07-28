// dto/RegisterRequest.java
package com.edu.tutor_platform.user.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String email;
    private String password;
}
