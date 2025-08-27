// package com.edu.tutor_platform.user.dto;

// public class AuthResponse {
//     private String token;

//     public AuthResponse(String token) {
//         this.token = token;
//     }

//     public String getToken() {
//         return token;
//     }
// }
package com.authsystem.dto;

public class AuthResponse {
    
    private String accessToken;
    private String tokenType = "Bearer";
    private UserInfo user;
    
    public AuthResponse() {}
    
    public AuthResponse(String accessToken, UserInfo user) {
        this.accessToken = accessToken;
        this.user = user;
    }
    
    public String getAccessToken() {
        return accessToken;
    }
    
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    
    public String getTokenType() {
        return tokenType;
    }
    
    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
    
    public UserInfo getUser() {
        return user;
    }
    
    public void setUser(UserInfo user) {
        this.user = user;
    }
    
    public static class UserInfo {
        private Long id;
        private String username;
        private String firstName;
        private String lastName;
        private String email;
        private String role;
        
        public UserInfo() {}
        
        public UserInfo(Long id, String username, String firstName, String lastName, String email, String role) {
            this.id = id;
            this.username = username;
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.role = role;
            System.out.println("UserInfo constructor called with role: " + role);
        }
        
        public Long getId() {
            return id;
        }
        
        public void setId(Long id) {
            this.id = id;
        }
        
        public String getUsername() {
            return username;
        }
        
        public void setUsername(String username) {
            this.username = username;
        }
        
        public String getFirstName() {
            return firstName;
        }
        
        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }
        
        public String getLastName() {
            return lastName;
        }
        
        public void setLastName(String lastName) {
            this.lastName = lastName;
        }
        
        public String getEmail() {
            return email;
        }
        
        public void setEmail(String email) {
            this.email = email;
        }
        
        public String getRole() {
            return role;
        }
        
        public void setRole(String role) {
            this.role = role;
        }
    }
}


