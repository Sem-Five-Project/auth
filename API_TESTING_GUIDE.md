# API Testing Guide - Tutor Platform

## Overview

This guide provides comprehensive testing instructions for all implemented features including authentication, unified search, atomic payment-booking system, and CI/CD pipeline.

## Authentication Endpoints

### 1. User Registration
```http
POST /api/auth/register
Content-Type: application/json

{
    "username": "student123",
    "email": "student@example.com",
    "password": "SecurePass123!",
    "role": "STUDENT"
}
```

### 2. User Login
```http
POST /api/auth/login
Content-Type: application/json

{
    "usernameOrEmail": "student123",
    "password": "SecurePass123!"
}
```

### 3. Token Refresh
```http
POST /api/auth/refresh
```
Note: Requires valid refresh token in HttpOnly cookie

### 4. Logout
```http
POST /api/auth/logout
Authorization: Bearer <access_token>
```

## Search Endpoints

### 1. Unified Search
```http
POST /api/search/unified
Content-Type: application/json
Authorization: Bearer <access_token>

{
    "query": "math",
    "filters": {
        "subjectIds": [1, 2],
        "priceRange": {
            "min": 1000,
            "max": 5000
        },
        "rating": 4.0,
        "availability": "AVAILABLE"
    },
    "sortBy": "rating",
    "sortDirection": "DESC",
    "page": 0,
    "size": 10
}
```

### 2. Search Suggestions (Autocomplete)
```http
GET /api/search/suggestions?query=mat&limit=5
Authorization: Bearer <access_token>
```

## Atomic Payment-Booking System

### 1. Get Available Slots
```http
POST /api/booking/search
Content-Type: application/json
Authorization: Bearer <access_token>

{
    "tutorId": 1,
    "subjectId": 2,
    "date": "2024-01-15",
    "startTime": "09:00",
    "endTime": "17:00"
}
```

### 2. Initiate Payment with Slot Booking
```http
POST /api/payments/atomic/initiate
Content-Type: application/json
Authorization: Bearer <access_token>

{
    "amount": 2500.00,
    "currency": "LKR",
    "classId": 1,
    "studentId": 1,
    "tutorId": 2,
    "availabilityId": 5,
    "slotId": 15,
    "sessionNotes": "Mathematics algebra session",
    "bookingType": "REGULAR"
}
```

**Expected Response:**
```json
{
    "merchantId": "your_merchant_id",
    "hash": "generated_md5_hash"
}
```

### 3. Check Transaction Status
```http
GET /api/payments/atomic/status/{orderId}
Authorization: Bearer <access_token>
```

**Expected Response:**
```json
{
    "paymentStatus": "PENDING",
    "bookingId": 123,
    "isConfirmed": false,
    "slotId": 15,
    "expiresAt": "2024-01-15T10:15:00",
    "isExpired": false,
    "lockedUntil": "2024-01-15T10:15:00"
}
```

### 4. Payment Success Webhook (PayHere)
```http
POST /api/payments/webhook/success
Content-Type: application/x-www-form-urlencoded

merchant_id=your_merchant_id&order_id=ORDER_123456&payment_id=PH_123&amount=2500.00&currency=LKR&status_code=2&md5sig=calculated_hash
```

### 5. Payment Cancel/Failure
```http
POST /api/payments/atomic/cancel/{orderId}
Authorization: Bearer <access_token>

{
    "reason": "Payment timeout"
}
```

## Testing Scenarios

### Scenario 1: Successful Payment-Booking Flow
1. **Login** as student
2. **Search** for available slots
3. **Initiate** atomic payment-booking
4. **Verify** slot is LOCKED and booking is pending
5. **Complete** payment via webhook
6. **Verify** slot is BOOKED and booking is confirmed

### Scenario 2: Payment Timeout Flow
1. **Login** as student
2. **Initiate** atomic payment-booking
3. **Wait** more than 15 minutes
4. **Verify** slot is automatically released
5. **Verify** payment is marked as EXPIRED

### Scenario 3: Double-Booking Prevention
1. **Student A** initiates payment for slot X
2. **Student B** tries to book same slot X
3. **Verify** Student B gets "Slot not available" error
4. **Complete** or cancel Student A's payment
5. **Verify** slot becomes available again

### Scenario 4: Search Functionality
1. **Search** for "mathematics" tutors
2. **Filter** by price range and rating
3. **Sort** by different criteria
4. **Test** autocomplete suggestions

## Environment Variables for Testing

### Local Development
```properties
# Database
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/tutor_platform_test
SPRING_DATASOURCE_USERNAME=test_user
SPRING_DATASOURCE_PASSWORD=test_password

# JWT
JWT_SECRET=your-256-bit-secret-key-for-testing
JWT_ACCESS_EXPIRATION=3600000
JWT_REFRESH_EXPIRATION=604800000

# PayHere
PAYHERE_MERCHANT_ID=your_test_merchant_id
PAYHERE_MERCHANT_SECRET=your_test_merchant_secret

# Admin
ADMIN_USERNAME=admin
ADMIN_PASSWORD=admin123
ADMIN_EMAIL=admin@tutor-platform.com
```

### Test Environment (CI/CD)
```properties
SPRING_PROFILES_ACTIVE=test
SPRING_DATASOURCE_URL=jdbc:h2:mem:testdb
SPRING_DATASOURCE_USERNAME=sa
SPRING_DATASOURCE_PASSWORD=
SPRING_JPA_HIBERNATE_DDL_AUTO=create-drop
```

## Postman Collection

### Authentication
1. **Register Student**: POST `/api/auth/register`
2. **Login Student**: POST `/api/auth/login`
3. **Refresh Token**: POST `/api/auth/refresh`

### Search
1. **Unified Search**: POST `/api/search/unified`
2. **Auto-complete**: GET `/api/search/suggestions`

### Atomic Payment-Booking
1. **Search Slots**: POST `/api/booking/search`
2. **Initiate Payment**: POST `/api/payments/atomic/initiate`
3. **Check Status**: GET `/api/payments/atomic/status/{orderId}`
4. **Cancel Payment**: POST `/api/payments/atomic/cancel/{orderId}`

### Webhooks
1. **Payment Success**: POST `/api/payments/webhook/success`
2. **Payment Failure**: POST `/api/payments/webhook/failure`

## Error Handling Test Cases

### 1. Slot Already Booked
- Try to book a slot that's already BOOKED
- Expected: `400 Bad Request` with "Slot not available"

### 2. Payment Timeout
- Initiate payment but don't complete within 15 minutes
- Expected: Automatic slot release and payment marked as EXPIRED

### 3. Invalid Payment Data
- Send payment request without required fields
- Expected: `400 Bad Request` with validation errors

### 4. Duplicate Booking Attempt
- Try to initiate payment while having another pending payment
- Expected: `400 Bad Request` with "Already have pending payment"

## Performance Testing

### 1. Concurrent Booking Attempts
Use tools like JMeter or Artillery to simulate:
- 10 students trying to book the same slot simultaneously
- Only 1 should succeed, others should get "not available" error

### 2. Search Performance
- Test unified search with large datasets
- Measure response times with different filter combinations

### 3. Database Cleanup
- Verify scheduled cleanup removes expired bookings every 5 minutes
- Check that database doesn't accumulate stale data

## Security Testing

### 1. Authentication
- Try accessing protected endpoints without token
- Test with expired tokens
- Verify refresh token rotation works

### 2. Payment Security
- Verify MD5 hash validation for PayHere webhooks
- Test with tampered payment data
- Ensure unauthorized users can't initiate payments for others

### 3. SQL Injection
- Test search endpoints with malicious SQL
- Verify JPA repositories prevent injection attacks

## Monitoring and Health Checks

### 1. Application Health
```http
GET /api/actuator/health
```

### 2. Metrics
```http
GET /api/actuator/metrics
```

### 3. Database Health
```http
GET /api/actuator/health/db
```

## CI/CD Pipeline Testing

### 1. Test Pipeline Trigger
1. Push code to `host_dev` branch
2. Verify pipeline runs automatically
3. Check all jobs (test, security-scan, build, deploy) complete

### 2. Security Scanning
- Verify OWASP dependency check runs
- Check for vulnerabilities in reports
- Ensure high-severity vulnerabilities fail the build

### 3. Deployment Verification
- Verify Railway deployment succeeds
- Check health endpoints are accessible
- Verify environment variables are loaded correctly

## Load Testing Scripts

### Artillery.js Configuration
```javascript
config:
  target: 'http://localhost:8080'
  phases:
    - duration: 60
      arrivalRate: 5
scenarios:
  - name: "Concurrent booking test"
    flow:
      - post:
          url: "/api/auth/login"
          json:
            usernameOrEmail: "student{{ $randomInt(1, 100) }}"
            password: "password"
      - post:
          url: "/api/payments/atomic/initiate"
          headers:
            Authorization: "Bearer {{ token }}"
          json:
            amount: 2500
            currency: "LKR"
            slotId: 1
```

## Database Verification Queries

### Check Slot Status
```sql
SELECT slot_id, status, tutor_availability_id 
FROM slot_instance 
WHERE status = 'LOCKED' OR status = 'BOOKED';
```

### Check Pending Bookings
```sql
SELECT b.booking_id, b.created_at, b.locked_until, b.is_confirmed, s.slot_id
FROM booking b
JOIN slot_instance s ON b.slot_instance_slot_id = s.slot_id
WHERE b.is_confirmed = false;
```

### Check Payment Status
```sql
SELECT order_id, status, expires_at, slot_id, student_id
FROM payment
WHERE status = 'PENDING'
ORDER BY created_at DESC;
```

## Integration Test Examples

### 1. Complete Booking Flow Test
```java
@Test
public void testCompleteBookingFlow() {
    // 1. Login
    String token = loginAsStudent();
    
    // 2. Search for slots
    List<SlotInstanceDTO> slots = searchAvailableSlots();
    
    // 3. Initiate payment
    String orderId = initiateAtomicPayment(slots.get(0));
    
    // 4. Verify slot is locked
    assertSlotStatus(slots.get(0).getSlotId(), SlotStatus.LOCKED);
    
    // 5. Complete payment
    completePaymentViaWebhook(orderId);
    
    // 6. Verify booking is confirmed
    assertBookingConfirmed(orderId);
}
```

## Troubleshooting Common Issues

### 1. Compilation Errors
- Check import statements
- Verify entity relationships
- Ensure proper annotations

### 2. Database Connection Issues
- Verify PostgreSQL is running
- Check connection string format
- Validate credentials

### 3. Payment Integration Issues
- Verify PayHere merchant credentials
- Check hash generation algorithm
- Validate webhook URL configuration

### 4. Scheduling Issues
- Verify @EnableScheduling annotation
- Check scheduled method syntax
- Ensure proper transaction handling

## Performance Benchmarks

### Expected Response Times
- Authentication: < 500ms
- Search queries: < 1000ms
- Payment initiation: < 800ms
- Slot booking: < 600ms

### Database Query Optimization
- Index on `slot_instance.status`
- Index on `booking.locked_until`
- Index on `payment.order_id`
- Composite index on `booking(student_id, is_confirmed)`

This testing guide ensures comprehensive validation of all implemented features with specific focus on the atomic payment-booking system's critical 15-minute timeout and slot blocking functionality.