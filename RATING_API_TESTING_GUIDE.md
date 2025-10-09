# Rating API Testing Guide

This guide demonstrates how to test the rating endpoints in the tutor platform application.

## Prerequisites

1. Ensure the application is running
2. You need valid authentication tokens for students
3. Have valid session IDs, tutor IDs, and student profiles in your database

## Base URL
```
http://localhost:8080/api/ratings
```

## Endpoints

### 1. Add Rating

**Endpoint:** `POST /api/ratings`

**Description:** Allows a student to add a rating for a tutor after a session.

**Headers:**
```
Content-Type: application/json
Authorization: Bearer <student_jwt_token>
```

**Request Body:**
```json
{
    "sessionId": 1,
    "tutorId": 1,
    "ratingValue": 4.5,
    "reviewText": "Great tutor! Very knowledgeable and patient."
}
```

**Response (201 Created):**
```json
{
    "ratingId": 1,
    "studentId": 1,
    "studentName": "John Doe",
    "tutorId": 1,
    "tutorName": "Jane Smith",
    "sessionId": 1,
    "sessionName": "Math Session 1",
    "ratingValue": 4.5,
    "reviewText": "Great tutor! Very knowledgeable and patient.",
    "createdAt": "2024-01-15T10:30:00",
    "updatedAt": "2024-01-15T10:30:00"
}
```

**Error Responses:**
- `400 Bad Request`: Invalid rating value (must be between 1.0 and 5.0)
- `403 Forbidden`: Unauthorized to rate this session
- `404 Not Found`: Session or tutor not found
- `409 Conflict`: Rating already exists for this session

### 2. Get Ratings by Tutor

**Endpoint:** `GET /api/ratings/tutor/{tutorId}`

**Description:** Get all ratings for a specific tutor with pagination.

**Parameters:**
- `tutorId` (path): The ID of the tutor
- `page` (query, optional): Page number (default: 0)
- `size` (query, optional): Page size (default: 10)

**Example:**
```
GET /api/ratings/tutor/1?page=0&size=5
```

**Response (200 OK):**
```json
[
    {
        "ratingId": 1,
        "studentId": 1,
        "studentName": "John Doe",
        "tutorId": 1,
        "tutorName": "Jane Smith",
        "sessionId": 1,
        "sessionName": "Math Session 1",
        "ratingValue": 4.5,
        "reviewText": "Great tutor! Very knowledgeable and patient.",
        "createdAt": "2024-01-15T10:30:00",
        "updatedAt": "2024-01-15T10:30:00"
    }
]
```

### 3. Get Tutor Ratings Summary

**Endpoint:** `GET /api/ratings/tutor/{tutorId}/summary`

**Description:** Get a comprehensive ratings summary for a tutor including average rating and distribution.

**Example:**
```
GET /api/ratings/tutor/1/summary
```

**Response (200 OK):**
```json
{
    "tutorId": 1,
    "tutorName": "Jane Smith",
    "averageRating": 4.2,
    "totalRatings": 25,
    "fiveStarRatings": 8,
    "fourStarRatings": 12,
    "threeStarRatings": 3,
    "twoStarRatings": 2,
    "oneStarRatings": 0
}
```

### 4. Get My Ratings

**Endpoint:** `GET /api/ratings/my-ratings`

**Description:** Get all ratings created by the authenticated student.

**Headers:**
```
Authorization: Bearer <student_jwt_token>
```

**Parameters:**
- `page` (query, optional): Page number (default: 0)
- `size` (query, optional): Page size (default: 10)

**Example:**
```
GET /api/ratings/my-ratings?page=0&size=10
```

**Response (200 OK):**
```json
[
    {
        "ratingId": 1,
        "studentId": 1,
        "studentName": "John Doe",
        "tutorId": 1,
        "tutorName": "Jane Smith",
        "sessionId": 1,
        "sessionName": "Math Session 1",
        "ratingValue": 4.5,
        "reviewText": "Great tutor! Very knowledgeable and patient.",
        "createdAt": "2024-01-15T10:30:00",
        "updatedAt": "2024-01-15T10:30:00"
    }
]
```

### 5. Get Rating by ID

**Endpoint:** `GET /api/ratings/{ratingId}`

**Description:** Get a specific rating by its ID.

**Example:**
```
GET /api/ratings/1
```

**Response (200 OK):**
```json
{
    "ratingId": 1,
    "studentId": 1,
    "studentName": "John Doe",
    "tutorId": 1,
    "tutorName": "Jane Smith",
    "sessionId": 1,
    "sessionName": "Math Session 1",
    "ratingValue": 4.5,
    "reviewText": "Great tutor! Very knowledgeable and patient.",
    "createdAt": "2024-01-15T10:30:00",
    "updatedAt": "2024-01-15T10:30:00"
}
```

**Error Response:**
- `404 Not Found`: Rating not found

### 6. Delete Rating

**Endpoint:** `DELETE /api/ratings/{ratingId}`

**Description:** Delete a rating (only by the student who created it).

**Headers:**
```
Authorization: Bearer <student_jwt_token>
```

**Example:**
```
DELETE /api/ratings/1
```

**Response (204 No Content):** Empty response body

**Error Responses:**
- `403 Forbidden`: Not authorized to delete this rating
- `404 Not Found`: Rating not found

## Testing with cURL

### Add a Rating
```bash
curl -X POST "http://localhost:8080/api/ratings" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "sessionId": 1,
    "tutorId": 1,
    "ratingValue": 4.5,
    "reviewText": "Great tutor!"
  }'
```

### Get Tutor Ratings
```bash
curl -X GET "http://localhost:8080/api/ratings/tutor/1?page=0&size=5"
```

### Get Tutor Summary
```bash
curl -X GET "http://localhost:8080/api/ratings/tutor/1/summary"
```

### Get My Ratings
```bash
curl -X GET "http://localhost:8080/api/ratings/my-ratings" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Delete Rating
```bash
curl -X DELETE "http://localhost:8080/api/ratings/1" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## Error Codes

- `200 OK`: Request successful
- `201 Created`: Rating created successfully
- `204 No Content`: Rating deleted successfully
- `400 Bad Request`: Invalid request data
- `401 Unauthorized`: Authentication required
- `403 Forbidden`: Access denied
- `404 Not Found`: Resource not found
- `409 Conflict`: Resource already exists
- `500 Internal Server Error`: Server error

## Notes

1. All rating values must be between 1.0 and 5.0
2. Students can only rate tutors from sessions they attended
3. Students can only rate a session once
4. Students can only delete their own ratings
5. The tutor's average rating is automatically updated when ratings are added or deleted