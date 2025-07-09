# Keycloak Setup Guide for Library Microservices

This guide provides step-by-step instructions for setting up Keycloak as an Identity Provider for the Library Microservices application.

## 1. Start Keycloak with Docker

First, ensure Docker is installed on your system. Then, start Keycloak using the provided Docker Compose file:

```bash
docker-compose up -d keycloak
```

This will start Keycloak on port 8090 with the following default credentials:
- Username: `admin`
- Password: `admin`

Access the Keycloak Admin Console at: http://localhost:8090/admin/

## 2. Create a New Realm

1. Log in to the Keycloak Admin Console
2. Hover over the realm dropdown in the top-left corner (default is "master")
3. Click "Create Realm"
4. Enter "library-realm" as the Realm name
5. Click "Create"

## 3. Create Clients

### 3.1. API Gateway Client

1. In the left sidebar, click on "Clients"
2. Click "Create client"
3. Fill in the following details:
   - Client ID: `api-gateway`
   - Name: `API Gateway`
   - Client Authentication: ON (confidential access type)
   - Authentication flow: check "Standard flow" and "Service accounts roles"
4. Click "Next"
5. In the Capability config screen:
   - Set Root URL: `http://localhost:8080`
   - Set Valid redirect URIs: `http://localhost:8080/*`
   - Set Web Origins: `+` (or specify `http://localhost:8080`)
6. Click "Next" and then "Save"

After creating the client, go to the "Credentials" tab and note the client secret. You'll need this for your application configuration.

### 3.2. Frontend Client (for SPA applications)

1. Click "Create client"
2. Fill in the following details:
   - Client ID: `library-frontend`
   - Name: `Library Frontend`
   - Client Authentication: OFF (public access type)
3. Click "Next"
4. In the Capability config screen:
   - Set Root URL: `http://localhost:3000` (adjust if your frontend runs on a different port)
   - Set Valid redirect URIs: `http://localhost:3000/*`
   - Set Web Origins: `http://localhost:3000` (or use `+` for wildcard)
5. Click "Next" and then "Save"

## 4. Create Roles

1. In the left sidebar, click on "Realm roles"
2. Click "Create role"
3. Create the following roles one by one:
   - `USER` - Regular library user
   - `LIBRARIAN` - Staff with book management capabilities
   - `ADMIN` - Administrator with full access

## 5. Create Users

### 5.1. Create Admin User

1. In the left sidebar, click on "Users"
2. Click "Add user"
3. Fill in the following details:
   - Username: `admin1`
   - Email: `admin@library.com`
   - First name: `Admin`
   - Last name: `User`
   - Email verified: ON
4. Click "Create"
5. Go to the "Credentials" tab
6. Set a password and disable "Temporary" if you don't want the user to reset password on first login
7. Click "Set Password"
8. Go to the "Role mappings" tab
9. Under "Realm roles", select "ADMIN" and click "Add selected"

### 5.2. Create Librarian User

1. Click "Add user"
2. Fill in the following details:
   - Username: `librarian1`
   - Email: `librarian@library.com`
   - First name: `Librarian`
   - Last name: `User`
   - Email verified: ON
3. Follow steps 5-8 from above, but assign the "LIBRARIAN" role

### 5.3. Create Regular User

1. Click "Add user"
2. Fill in the following details:
   - Username: `user1`
   - Email: `user@library.com`
   - First name: `Regular`
   - Last name: `User`
   - Email verified: ON
3. Follow steps 5-8 from above, but assign the "USER" role

## 6. Configure Token Settings

1. In the left sidebar, click on "Realm settings"
2. Go to the "Tokens" tab
3. Configure the following settings:
   - Access Token Lifespan: 15 minutes (900 seconds)
   - Client login timeout: 30 minutes (1800 seconds)
   - Offline Session Idle: 30 days
   - Access Token Lifespan For Implicit Flow: 15 minutes
   - Client Session Idle: 30 minutes
   - Client Session Max: 10 hours

## 7. Testing the Setup

### 7.1. Get an Access Token (using curl)

```bash
curl -X POST \
  http://localhost:8090/realms/library-realm/protocol/openid-connect/token \
  -H 'Content-Type: application/x-www-form-urlencoded' \
  -d 'grant_type=password&client_id=api-gateway&client_secret=YOUR_CLIENT_SECRET&username=user1&password=YOUR_PASSWORD'
```

Replace `YOUR_CLIENT_SECRET` with the client secret from step 3.1 and `YOUR_PASSWORD` with the password you set for the user.

### 7.2. Access a Protected Resource

```bash
curl -X GET \
  http://localhost:8080/api/books \
  -H 'Authorization: Bearer YOUR_ACCESS_TOKEN'
```

Replace `YOUR_ACCESS_TOKEN` with the access token received from the previous request.

## 8. Troubleshooting

### 8.1. CORS Issues

If you encounter CORS issues, ensure that:
1. Web Origins are properly configured in the client settings
2. Your Spring Security configuration has CORS enabled
3. Your Gateway has proper CORS configuration

### 8.2. Token Validation Failures

If token validation fails, check:
1. The issuer URI in your application.yaml matches the Keycloak realm
2. The JWK Set URI is correctly pointing to your Keycloak instance
3. The client secret is correct
4. The token is not expired

### 8.3. Role Mapping Issues

If role-based access control is not working:
1. Verify that roles are correctly defined in Keycloak
2. Check that your JwtAuthenticationConverter is correctly extracting roles
3. Ensure roles are properly prefixed with "ROLE_" in Spring Security

## 9. Next Steps

- Configure SSL for production environments
- Set up user registration and email verification
- Implement refresh token rotation
- Configure user attribute mapping for additional user information
- Set up groups for role management 