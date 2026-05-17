# MyPlant Backend API Documentation

## Base URL
```
http://localhost:8080
```

## Authentication

Most endpoints require JWT token in the Authorization header:
```
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNjk4...
```

## HTTP Status Codes
- `200 OK` - Request successful
- `201 Created` - Resource created
- `400 Bad Request` - Invalid request data
- `401 Unauthorized` - Missing or invalid token
- `403 Forbidden` - User doesn't own the resource
- `404 Not Found` - Resource doesn't exist
- `409 Conflict` - Resource already exists (e.g., email)
- `500 Internal Server Error` - Server error

## Error Response Format
```json
{
  "status": 400,
  "message": "Error description",
  "path": "/api/endpoint",
  "timestamp": "2024-05-11T10:30:00"
}
```

---

## Authentication Endpoints

### 1. Register User
**POST** `/api/auth/register`

Creates a new user account.

**Request Body:**
```json
{
  "email": "user@example.com",
  "password": "SecurePassword123!",
  "firstName": "John",
  "lastName": "Doe",
  "city": "New York",
  "phoneNumber": "+1234567890"
}
```

**Response:** `201 Created`
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "type": "Bearer",
  "id": 1,
  "email": "user@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "message": "User registered successfully!"
}
```

**Errors:**
- `409 Conflict` - Email already exists

---

### 2. Login User
**POST** `/api/auth/login`

Authenticates user and returns JWT token.

**Request Body:**
```json
{
  "email": "user@example.com",
  "password": "SecurePassword123!"
}
```

**Response:** `200 OK`
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "type": "Bearer",
  "id": 1,
  "email": "user@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "message": "Login successful!"
}
```

**Errors:**
- `401 Unauthorized` - Invalid email or password

---

## User Endpoints

### 3. Get Current User Profile
**GET** `/api/users/profile`

Gets the authenticated user's profile information.

**Headers:**
```
Authorization: Bearer {token}
```

**Response:** `200 OK`
```json
{
  "id": 1,
  "email": "user@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "city": "New York",
  "phoneNumber": "+1234567890",
  "emailNotifications": true,
  "whatsappNotifications": false,
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T10:30:00"
}
```

---

### 4. Get User by ID
**GET** `/api/users/{id}`

Gets a specific user's profile.

**Path Parameters:**
- `id` (Long) - User ID

**Response:** `200 OK`
(Same format as above)

---

### 5. Update Notification Preferences
**PUT** `/api/users/{id}/preferences`

Updates user's notification settings.

**Path Parameters:**
- `id` (Long) - User ID

**Request Body:**
```json
{
  "emailNotifications": true,
  "whatsappNotifications": true
}
```

**Response:** `200 OK`
(Updated user profile)

---

### 6. Update User's City
**PUT** `/api/users/{id}/city`

Updates user's city (used for weather integration).

**Request Body:**
```json
{
  "city": "London"
}
```

**Response:** `200 OK`
(Updated user profile)

---

## Plant Endpoints

### 7. Get All User's Plants
**GET** `/api/plants`

Gets all plants owned by the authenticated user.

**Headers:**
```
Authorization: Bearer {token}
```

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "name": "My Snake Plant",
    "plantType": "Snake Plant",
    "potSize": "Medium (6 inches)",
    "isIndoor": true,
    "location": "Living Room",
    "lastWateredDate": "2024-05-10",
    "wateringStreak": 5,
    "health": "Healthy",
    "notes": "Placed near south window",
    "customWateringInterval": null,
    "plantCareRuleId": 1,
    "careRuleDescription": "Hardy succulent...",
    "baseWateringDays": 10,
    "wateringFrequency": "Every 10 days",
    "sunlightNeeds": "Low to Medium",
    "createdAt": "2024-01-15T10:30:00",
    "updatedAt": "2024-05-10T14:20:00"
  }
]
```

---

### 8. Get Specific Plant
**GET** `/api/plants/{id}`

Gets details of a specific plant.

**Path Parameters:**
- `id` (Long) - Plant ID

**Response:** `200 OK`
(Single plant object, see above)

**Errors:**
- `403 Forbidden` - Plant doesn't belong to user
- `404 Not Found` - Plant doesn't exist

---

### 9. Create New Plant
**POST** `/api/plants`

Adds a new plant to the user's collection.

**Request Body:**
```json
{
  "name": "My Pothos",
  "plantType": "Pothos",
  "potSize": "Small (4 inches)",
  "isIndoor": true,
  "location": "Bedroom",
  "notes": "Hanging in window",
  "customWateringInterval": null
}
```

**Response:** `201 Created`
(Created plant object with ID)

**Errors:**
- `400 Bad Request` - Invalid plant type
- `404 Not Found` - Plant care rule not found

---

### 10. Update Plant
**PUT** `/api/plants/{id}`

Updates existing plant information.

**Path Parameters:**
- `id` (Long) - Plant ID

**Request Body:**
```json
{
  "name": "My Updated Pothos",
  "plantType": "Pothos",
  "potSize": "Medium (6 inches)",
  "isIndoor": true,
  "location": "Living Room",
  "notes": "Moved from bedroom",
  "customWateringInterval": 5
}
```

**Response:** `200 OK`
(Updated plant object)

---

### 11. Delete Plant
**DELETE** `/api/plants/{id}`

Removes a plant from the user's collection.

**Path Parameters:**
- `id` (Long) - Plant ID

**Response:** `200 OK`
```json
{
  "message": "Plant deleted successfully"
}
```

---

### 12. Mark Plant as Watered
**POST** `/api/plants/{id}/water`

Records that the plant was watered today.

**Path Parameters:**
- `id` (Long) - Plant ID

**Response:** `200 OK`
(Updated plant object with lastWateredDate = today)

---

### 13. Update Plant Health
**PUT** `/api/plants/{id}/health`

Updates plant's health status.

**Request Body:**
```json
{
  "health": "Healthy"
}
```

Valid values: `"Healthy"`, `"Fair"`, `"Poor"`

**Response:** `200 OK`
(Updated plant object)

---

## Plant Care Rules Endpoints

### 14. Get All Plant Types
**GET** `/api/plant-care-rules`

Gets all available plant types with care information.

**NOTE:** This endpoint is PUBLIC (no authentication needed)

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "plantName": "Snake Plant",
    "baseWateringDays": 10,
    "wateringFrequency": "Every 10 days",
    "sunlightNeeds": "Low to Medium",
    "humidityPreference": "Low",
    "temperatureRange": "16-24°C",
    "difficultyLevel": "Beginner",
    "description": "Hardy succulent with dark green leaves...",
    "commonIssues": "Root rot, Brown tips, Pests",
    "careTips": "Let soil dry between waterings...",
    "rainSensitive": true,
    "temperatureSensitive": true,
    "recommendedPotSize": "Medium (6 inches)",
    "growthRate": "Slow",
    "maxSize": "12-15 inches"
  }
]
```

---

### 15. Get Plant Care Rules by Type
**GET** `/api/plant-care-rules/{plantName}`

Gets care information for a specific plant type.

**Path Parameters:**
- `plantName` (String) - Plant type name (URL encoded, e.g., "Snake%20Plant")

**Response:** `200 OK`
(Single plant care rule object)

**Errors:**
- `404 Not Found` - Plant type not found

---

## Notification Endpoints

### 16. Get All Notifications
**GET** `/api/notifications`

Gets all notifications for the authenticated user.

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "userId": 1,
    "plantId": 5,
    "plantName": "My Snake Plant",
    "notificationType": "WATERING_REMINDER",
    "title": "Time to Water Your Snake Plant!",
    "message": "Your Snake Plant hasn't been watered in 10 days...",
    "channel": "EMAIL",
    "status": "SENT",
    "sentAt": "2024-05-11T09:00:00",
    "viewedAt": null,
    "createdAt": "2024-05-11T09:00:00"
  }
]
```

---

### 17. Get Unread Notifications
**GET** `/api/notifications/unread`

Gets only unread notifications.

**Response:** `200 OK`
(Array of notification objects with status != "VIEWED")

---

### 18. Mark Notification as Read
**PUT** `/api/notifications/{id}/read`

Marks a notification as viewed.

**Path Parameters:**
- `id` (Long) - Notification ID

**Response:** `200 OK`
(Updated notification with status="VIEWED")

---

## Watering History Endpoints

### 19. Record Watering Event
**POST** `/api/watering-history/record/{plantId}`

Records a watering event for a plant.

**Path Parameters:**
- `plantId` (Long) - Plant ID

**Request Body:**
```json
{
  "notes": "Watered generously due to hot weather"
}
```

**Response:** `201 Created`
```json
{
  "id": 42,
  "plantId": 5,
  "plantName": "My Snake Plant",
  "wateredDate": "2024-05-11",
  "notes": "Watered generously due to hot weather",
  "onTime": true,
  "waterAmount": null,
  "plantHealthAtWatering": null,
  "weatherCondition": null
}
```

---

### 20. Get Plant's Watering History
**GET** `/api/watering-history/plant/{plantId}`

Gets all watering records for a specific plant.

**Path Parameters:**
- `plantId` (Long) - Plant ID

**Response:** `200 OK`
(Array of watering history objects)

---

### 21. Get All Watering History
**GET** `/api/watering-history`

Gets all watering records for all user's plants.

**Response:** `200 OK`
(Array of watering history objects)

---

### 22. Get Recent Watering (Last 7 Days)
**GET** `/api/watering-history/recent`

Gets watering records from the last 7 days.

**Response:** `200 OK`
(Array of watering history objects)

---

## Dashboard Endpoints

### 23. Get Complete Dashboard
**GET** `/api/dashboard`

Gets aggregated dashboard data.

**Response:** `200 OK`
```json
{
  "plants": [
    { /* plant objects */ }
  ],
  "upcomingTasks": [
    {
      "plantId": 1,
      "plantName": "Snake Plant",
      "daysUntilWatering": 2,
      "priority": "high"
    }
  ],
  "recentlyWatered": [
    { /* watering history objects */ }
  ],
  "recentNotifications": [
    { /* notification objects */ }
  ],
  "stats": {
    "totalPlants": 5,
    "plantsDueForWatering": 2,
    "overduePlants": 0
  }
}
```

---

### 24. Get Upcoming Tasks
**GET** `/api/dashboard/tasks`

Gets list of upcoming watering tasks sorted by urgency.

**Response:** `200 OK`
```json
[
  {
    "plantId": 1,
    "plantName": "Snake Plant",
    "lastWateredDate": "2024-05-01",
    "daysUntil": 8,
    "status": "NOT_DUE",
    "recommendation": "Water in 2 days"
  }
]
```

---

## Scheduled Tasks (Running Automatically)

### Watering Reminder Scheduler
**Runs:** Daily at 9:00 AM
- Checks all user plants
- Calculates watering needs
- Sends email/WhatsApp notifications
- Creates notification records

### Overdue Plant Checker
**Runs:** Every 6 hours
- Checks for plants overdue by 3+ days
- Sends alert notifications
- Helps prevent plant death

---

## Rate Limiting

Currently no rate limiting. For production, consider implementing:
- 1000 requests per hour per IP
- 100 requests per hour per user
- Burst limit: 50 concurrent requests

---

## WebHooks (Future Feature)

Planned integration points:
- Plant watered webhook
- Notification sent webhook
- User registration webhook
- Custom reminder webhooks

---

## Common Response Codes & Messages

### Success
```
200 OK           - Request successful
201 Created      - Resource created successfully
```

### Client Errors
```
400 Bad Request  - "Invalid request data"
401 Unauthorized - "Invalid or expired JWT token. Please login again."
403 Forbidden    - "You are not authorized to access this resource."
404 Not Found    - "Resource not found"
409 Conflict     - "Email already registered"
```

### Server Errors
```
500 Internal Error - "An unexpected error occurred. Please try again later."
```

---

## Example Workflows

### Workflow 1: User Registration & First Plant
```bash
# 1. Register
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "pass123",
    "firstName": "John",
    "lastName": "Doe",
    "city": "London"
  }'

# Response includes JWT token
# TOKEN=eyJhbGciOiJIUzUxMiJ9...

# 2. Get available plant types
curl http://localhost:8080/api/plant-care-rules

# 3. Create plant
curl -X POST http://localhost:8080/api/plants \
  -H "Authorization: Bearer TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "My Snake Plant",
    "plantType": "Snake Plant",
    "potSize": "Medium (6 inches)",
    "isIndoor": true,
    "location": "Living Room"
  }'

# 4. View dashboard
curl -X GET http://localhost:8080/api/dashboard \
  -H "Authorization: Bearer TOKEN"
```

### Workflow 2: Record Watering
```bash
# 1. Mark plant as watered
curl -X POST http://localhost:8080/api/plants/1/water \
  -H "Authorization: Bearer TOKEN"

# 2. Record watering history
curl -X POST http://localhost:8080/api/watering-history/record/1 \
  -H "Authorization: Bearer TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"notes": "Watered thoroughly"}'

# 3. Check recent watering
curl -X GET http://localhost:8080/api/watering-history/recent \
  -H "Authorization: Bearer TOKEN"
```

---

## Testing Endpoints with Postman

Import collection: See `postman_collection.json`

Or create manually:
1. Set variable: `{{BASE_URL}}` = `http://localhost:8080`
2. Set variable: `{{TOKEN}}` = JWT token from login response
3. Use `Authorization: Bearer {{TOKEN}}` in headers

---

## SDK/Client Libraries

### JavaScript/React
```javascript
import axios from 'axios';

const API_URL = 'http://localhost:8080/api';

// Example: Get plants
const getPlants = (token) => {
  return axios.get(`${API_URL}/plants`, {
    headers: { Authorization: `Bearer ${token}` }
  });
};
```

### Python
```python
import requests

API_URL = 'http://localhost:8080/api'

def get_plants(token):
    headers = {'Authorization': f'Bearer {token}'}
    return requests.get(f'{API_URL}/plants', headers=headers)
```

---

## Need Help?

- Check logs: `tail -f logs/myplant.log`
- Enable debug mode in `application.properties`
- Review example requests above
- Check GitHub issues
