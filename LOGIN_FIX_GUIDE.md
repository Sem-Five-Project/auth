# Login Issue Fix Guide

## Problem Identified
The login endpoint `https://auth-production-b40f.up.railway.app/api/auth/login` wasn't working because of a **path mismatch** in the Spring Security configuration.

## Root Cause
- **Security Config** was allowing: `/auth/login` 
- **Controller** was using: `/api/auth/login`
- Spring Security was **blocking** all `/api/auth/login` requests as unauthorized

## Fix Applied
Updated [`SecurityConfig.java`](src/main/java/com/edu/tutor_platform/config/SecurityConfig.java) to allow all `/api/auth/**` endpoints:

```java
.requestMatchers("/api/auth/**", "/api/payment/notify", "/api/actuator/health").permitAll()
```

## Testing the Fix

### 1. Test Registration (Should still work)
```bash
curl -X POST https://auth-production-b40f.up.railway.app/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser123",
    "firstName": "Test",
    "lastName": "User",
    "email": "test@example.com",
    "password": "SecurePass123!",
    "confirmPassword": "SecurePass123!",
    "role": "STUDENT"
  }'
```

### 2. Test Login (Should now work)
```bash
curl -X POST https://auth-production-b40f.up.railway.app/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "testuser123",
    "password": "SecurePass123!"
  }'
```

### 3. Expected Login Response
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "user": {
    "id": 1,
    "username": "testuser123",
    "firstName": "Test",
    "lastName": "User",
    "email": "test@example.com",
    "role": "STUDENT"
  }
}
```

## Additional Endpoints That Should Work
- ✅ `POST /api/auth/register`
- ✅ `POST /api/auth/login` (Fixed)
- ✅ `POST /api/auth/refresh`
- ✅ `POST /api/auth/logout`
- ✅ `GET /api/auth/check-username`
- ✅ `GET /api/auth/me`

## Deployment Steps
1. **Push to GitHub**: Commit the SecurityConfig fix
2. **Automatic Deployment**: GitHub Actions will deploy to Railway
3. **Verify**: Test the login endpoint once deployment completes

## Error Handling
If you still get errors, check these common issues:

### Invalid Credentials
- **Error**: `"message": "Bad credentials"`
- **Solution**: Verify username/email and password are correct

### Rate Limiting
- **Error**: `"message": "Too many failed login attempts"`
- **Solution**: Wait 15 minutes for IP block to clear

### Database Connection
- **Error**: Connection timeout or database errors
- **Solution**: Check Railway database service status

## User Entity Integration
The system uses [`UserDetailsServiceImpl`](src/main/java/com/edu/tutor_platform/user/service/UserDetailsServiceImpl.java) which properly handles both username and email login:

```java
public UserDetails loadUserByUsername(String usernameOrEmail) {
    User user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    return user;
}
```

## Security Features Working
- ✅ **JWT Authentication**: Access tokens with proper expiration
- ✅ **Refresh Tokens**: HttpOnly cookies with rotation
- ✅ **Rate Limiting**: 8 attempts, 15-minute block
- ✅ **CORS**: Configured for frontend origins
- ✅ **CSRF Protection**: Disabled for API endpoints

## Production Environment Variables
Ensure these are set in Railway:

```properties
SPRING_PROFILES_ACTIVE=production
SPRING_DATASOURCE_URL=postgresql://...
JWT_SECRET=your-256-bit-secret
PAYHERE_MERCHANT_ID=your-merchant-id
```

The fix should resolve the login authentication issue immediately after deployment.