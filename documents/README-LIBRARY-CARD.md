# Library Card Management Feature

## Overview
The Library Card Management feature allows administrators and librarians to create, view, update, and renew library cards for users. Regular users can view their own library card information.

## Features
- Create new library cards for users
- View library card details
- Update library card status (block/unblock)
- Renew library cards with new expiry dates
- Automatic detection of expired cards
- Notification system for cards that are expiring soon

## API Endpoints

### Create a Library Card
- **URL:** `/api/library-cards`
- **Method:** `POST`
- **Authorization:** Admin, Librarian
- **Request Body:**
  ```json
  {
    "user_id": 1,
    "expiry_date": "01-01-2025"
  }
  ```
- **Response:**
  ```json
  {
    "id": 1,
    "card_number": "LC-12345678",
    "user_id": 1,
    "issue_date": "01-01-2024",
    "expiry_date": "01-01-2025",
    "status": "ACTIVE"
  }
  ```

### Get a Library Card by ID
- **URL:** `/api/library-cards/{id}`
- **Method:** `GET`
- **Authorization:** Any authenticated user (users can only view their own cards)
- **Response:**
  ```json
  {
    "id": 1,
    "card_number": "LC-12345678",
    "user_id": 1,
    "issue_date": "01-01-2024",
    "expiry_date": "01-01-2025",
    "status": "ACTIVE"
  }
  ```

### Get Library Cards for a User
- **URL:** `/api/library-cards/user/{userId}`
- **Method:** `GET`
- **Authorization:** Any authenticated user (users can only view their own cards)
- **Response:**
  ```json
  [
    {
      "id": 1,
      "card_number": "LC-12345678",
      "user_id": 1,
      "issue_date": "01-01-2024",
      "expiry_date": "01-01-2025",
      "status": "ACTIVE"
    }
  ]
  ```

### Get All Library Cards
- **URL:** `/api/library-cards`
- **Method:** `GET`
- **Authorization:** Admin, Librarian
- **Query Parameters:**
  - `status` (optional): Filter by status (ACTIVE, EXPIRED, BLOCKED, LOST)
- **Response:**
  ```json
  [
    {
      "id": 1,
      "card_number": "LC-12345678",
      "user_id": 1,
      "issue_date": "01-01-2024",
      "expiry_date": "01-01-2025",
      "status": "ACTIVE"
    }
  ]
  ```

### Update Library Card Status
- **URL:** `/api/library-cards/{id}/status`
- **Method:** `PATCH`
- **Authorization:** Admin, Librarian
- **Request Body:**
  ```json
  {
    "status": "BLOCKED",
    "reason": "Violation of library rules"
  }
  ```
- **Response:**
  ```json
  {
    "id": 1,
    "card_number": "LC-12345678",
    "user_id": 1,
    "issue_date": "01-01-2024",
    "expiry_date": "01-01-2025",
    "status": "BLOCKED"
  }
  ```

### Renew Library Card
- **URL:** `/api/library-cards/{id}/renew`
- **Method:** `PATCH`
- **Authorization:** Admin, Librarian
- **Request Body:**
  ```json
  {
    "new_expiry_date": "01-01-2026"
  }
  ```
- **Response:**
  ```json
  {
    "id": 1,
    "card_number": "LC-12345678",
    "user_id": 1,
    "issue_date": "01-01-2024",
    "expiry_date": "01-01-2026",
    "status": "ACTIVE"
  }
  ```

## Scheduled Tasks
The system includes two scheduled tasks:

1. **Check Expired Cards**: Runs daily at midnight to identify and update the status of expired cards.
2. **Check Expiring Soon Cards**: Runs daily at 1 AM to identify cards that are expiring soon and send notifications.

## Event Publishing
The service publishes the following Kafka events:

- `CARD_CREATED`: When a new library card is created
- `CARD_RENEWED`: When a library card is renewed
- `CARD_EXPIRED`: When a card expires
- `CARD_EXPIRING_SOON`: When a card is about to expire

## Configuration
The following configuration properties are available:

```yaml
app:
  library-card:
    default-validity-years: 1  # Default validity period in years
    expiring-soon-days: 30     # Days before expiry to send notifications
```

## Business Rules
- Each user can have only one active library card at a time
- Card numbers are unique and automatically generated
- Expiry dates must be in the future
- Blocked or lost cards cannot be renewed
- When renewing an expired card, its status is set back to active 