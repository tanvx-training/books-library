# Frontend Integration Guide

## Overview

This guide explains how to integrate frontend applications with the Library Management System's authentication and authorization system using Keycloak and the API Gateway.

## Authentication Flow Options

### 1. Authorization Code Flow (Recommended for Web Apps)

This is the most secure flow for web applications.

#### Setup

```javascript
// keycloak-config.js
export const keycloakConfig = {
  url: 'http://localhost:8080',
  realm: 'library-realm',
  clientId: 'frontend-app'
};

// Initialize Keycloak
import Keycloak from 'keycloak-js';

const keycloak = new Keycloak(keycloakConfig);

export default keycloak;
```

#### React Integration

```jsx
// App.js
import React, { useEffect, useState } from 'react';
import keycloak from './keycloak-config';
import { AuthProvider } from './contexts/AuthContext';

function App() {
  const [keycloakInitialized, setKeycloakInitialized] = useState(false);
  const [authenticated, setAuthenticated] = useState(false);

  useEffect(() => {
    keycloak.init({
      onLoad: 'login-required',
      checkLoginIframe: false,
      pkceMethod: 'S256'
    }).then((authenticated) => {
      setAuthenticated(authenticated);
      setKeycloakInitialized(true);
      
      if (authenticated) {
        // Set up token refresh
        setInterval(() => {
          keycloak.updateToken(70).then((refreshed) => {
            if (refreshed) {
              console.log('Token refreshed');
            }
          }).catch(() => {
            console.log('Failed to refresh token');
            keycloak.login();
          });
        }, 60000);
      }
    }).catch((error) => {
      console.error('Keycloak initialization failed', error);
    });
  }, []);

  if (!keycloakInitialized) {
    return <div>Loading...</div>;
  }

  return (
    <AuthProvider keycloak={keycloak} authenticated={authenticated}>
      <div className="App">
        {authenticated ? <MainApp /> : <LoginPage />}
      </div>
    </AuthProvider>
  );
}

export default App;
```

#### Auth Context

```jsx
// contexts/AuthContext.js
import React, { createContext, useContext, useEffect, useState } from 'react';

const AuthContext = createContext();

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

export const AuthProvider = ({ children, keycloak, authenticated }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (authenticated && keycloak.token) {
      fetchUserInfo();
    } else {
      setLoading(false);
    }
  }, [authenticated, keycloak.token]);

  const fetchUserInfo = async () => {
    try {
      const response = await fetch('http://localhost:8888/api/auth/me', {
        headers: {
          'Authorization': `Bearer ${keycloak.token}`
        }
      });
      
      if (response.ok) {
        const userData = await response.json();
        setUser(userData);
      }
    } catch (error) {
      console.error('Failed to fetch user info:', error);
    } finally {
      setLoading(false);
    }
  };

  const login = () => {
    keycloak.login();
  };

  const logout = () => {
    keycloak.logout();
  };

  const hasRole = (role) => {
    return user?.roles?.includes(role) || false;
  };

  const hasPermission = (permission) => {
    return user?.permissions?.includes(permission) || false;
  };

  const hasAnyRole = (...roles) => {
    return roles.some(role => hasRole(role));
  };

  const value = {
    user,
    authenticated,
    loading,
    token: keycloak.token,
    login,
    logout,
    hasRole,
    hasPermission,
    hasAnyRole,
    keycloak
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
};
```

### 2. Direct Access Grants Flow (For Mobile/SPA)

For single-page applications or mobile apps where you handle login forms directly.

```javascript
// auth-service.js
class AuthService {
  constructor() {
    this.baseUrl = 'http://localhost:8080/realms/library-realm/protocol/openid-connect';
    this.apiBaseUrl = 'http://localhost:8888';
    this.clientId = 'frontend-app';
  }

  async login(username, password) {
    try {
      const response = await fetch(`${this.baseUrl}/token`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: new URLSearchParams({
          grant_type: 'password',
          client_id: this.clientId,
          username,
          password
        })
      });

      if (!response.ok) {
        throw new Error('Login failed');
      }

      const tokens = await response.json();
      
      // Store tokens securely
      localStorage.setItem('access_token', tokens.access_token);
      localStorage.setItem('refresh_token', tokens.refresh_token);
      localStorage.setItem('token_expires_at', Date.now() + (tokens.expires_in * 1000));

      return tokens;
    } catch (error) {
      console.error('Login error:', error);
      throw error;
    }
  }

  async refreshToken() {
    const refreshToken = localStorage.getItem('refresh_token');
    if (!refreshToken) {
      throw new Error('No refresh token available');
    }

    try {
      const response = await fetch(`${this.baseUrl}/token`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: new URLSearchParams({
          grant_type: 'refresh_token',
          client_id: this.clientId,
          refresh_token: refreshToken
        })
      });

      if (!response.ok) {
        throw new Error('Token refresh failed');
      }

      const tokens = await response.json();
      
      localStorage.setItem('access_token', tokens.access_token);
      localStorage.setItem('refresh_token', tokens.refresh_token);
      localStorage.setItem('token_expires_at', Date.now() + (tokens.expires_in * 1000));

      return tokens;
    } catch (error) {
      console.error('Token refresh error:', error);
      this.logout();
      throw error;
    }
  }

  async logout() {
    const refreshToken = localStorage.getItem('refresh_token');
    
    if (refreshToken) {
      try {
        await fetch(`${this.baseUrl}/logout`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
          },
          body: new URLSearchParams({
            client_id: this.clientId,
            refresh_token: refreshToken
          })
        });
      } catch (error) {
        console.error('Logout error:', error);
      }
    }

    // Clear stored tokens
    localStorage.removeItem('access_token');
    localStorage.removeItem('refresh_token');
    localStorage.removeItem('token_expires_at');
  }

  getAccessToken() {
    return localStorage.getItem('access_token');
  }

  isTokenExpired() {
    const expiresAt = localStorage.getItem('token_expires_at');
    if (!expiresAt) return true;
    
    return Date.now() >= parseInt(expiresAt) - 60000; // Refresh 1 minute before expiry
  }

  async ensureValidToken() {
    if (this.isTokenExpired()) {
      await this.refreshToken();
    }
    return this.getAccessToken();
  }
}

export default new AuthService();
```

## API Client Setup

### HTTP Client with Automatic Token Handling

```javascript
// api-client.js
import authService from './auth-service';

class ApiClient {
  constructor() {
    this.baseUrl = 'http://localhost:8888';
  }

  async request(endpoint, options = {}) {
    try {
      // Ensure we have a valid token
      const token = await authService.ensureValidToken();
      
      const url = `${this.baseUrl}${endpoint}`;
      const config = {
        ...options,
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`,
          ...options.headers
        }
      };

      const response = await fetch(url, config);

      if (response.status === 401) {
        // Token might be invalid, try to refresh
        try {
          await authService.refreshToken();
          const newToken = authService.getAccessToken();
          config.headers.Authorization = `Bearer ${newToken}`;
          
          // Retry the request
          const retryResponse = await fetch(url, config);
          if (!retryResponse.ok) {
            throw new Error(`HTTP ${retryResponse.status}: ${retryResponse.statusText}`);
          }
          return retryResponse.json();
        } catch (refreshError) {
          // Refresh failed, redirect to login
          authService.logout();
          window.location.href = '/login';
          throw new Error('Authentication failed');
        }
      }

      if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        throw new Error(errorData.message || `HTTP ${response.status}: ${response.statusText}`);
      }

      return response.json();
    } catch (error) {
      console.error('API request failed:', error);
      throw error;
    }
  }

  // Convenience methods
  get(endpoint, options = {}) {
    return this.request(endpoint, { ...options, method: 'GET' });
  }

  post(endpoint, data, options = {}) {
    return this.request(endpoint, {
      ...options,
      method: 'POST',
      body: JSON.stringify(data)
    });
  }

  put(endpoint, data, options = {}) {
    return this.request(endpoint, {
      ...options,
      method: 'PUT',
      body: JSON.stringify(data)
    });
  }

  delete(endpoint, options = {}) {
    return this.request(endpoint, { ...options, method: 'DELETE' });
  }
}

export default new ApiClient();
```

### API Service Layer

```javascript
// services/user-service.js
import apiClient from '../api-client';

export const userService = {
  async getCurrentUser() {
    return apiClient.get('/api/auth/me');
  },

  async updateProfile(userData) {
    return apiClient.put('/api/users/me', userData);
  },

  async getAllUsers() {
    return apiClient.get('/api/users');
  },

  async createUser(userData) {
    return apiClient.post('/api/users', userData);
  },

  async syncUser(keycloakId) {
    return apiClient.post(`/api/auth/sync/${keycloakId}`);
  },

  async syncAllUsers() {
    return apiClient.post('/api/auth/sync/all');
  },

  async checkPermissions(role, permission) {
    const params = new URLSearchParams();
    if (role) params.append('role', role);
    if (permission) params.append('permission', permission);
    
    return apiClient.get(`/api/auth/permissions?${params.toString()}`);
  }
};

// services/library-service.js
export const libraryService = {
  async getMyLibraryCards() {
    return apiClient.get('/api/library-cards/my-cards');
  },

  async getAllLibraryCards() {
    return apiClient.get('/api/library-cards');
  },

  async createLibraryCard(cardData) {
    return apiClient.post('/api/library-cards', cardData);
  }
};
```

## Component Examples

### Protected Route Component

```jsx
// components/ProtectedRoute.js
import React from 'react';
import { useAuth } from '../contexts/AuthContext';

const ProtectedRoute = ({ 
  children, 
  requiredRoles = [], 
  requiredPermissions = [],
  fallback = <div>Access Denied</div>
}) => {
  const { authenticated, user, loading, hasAnyRole, hasPermission } = useAuth();

  if (loading) {
    return <div>Loading...</div>;
  }

  if (!authenticated) {
    return <div>Please log in to access this page.</div>;
  }

  // Check roles
  if (requiredRoles.length > 0 && !hasAnyRole(...requiredRoles)) {
    return fallback;
  }

  // Check permissions
  if (requiredPermissions.length > 0) {
    const hasRequiredPermission = requiredPermissions.some(permission => 
      hasPermission(permission)
    );
    if (!hasRequiredPermission) {
      return fallback;
    }
  }

  return children;
};

export default ProtectedRoute;
```

### User Profile Component

```jsx
// components/UserProfile.js
import React, { useState, useEffect } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { userService } from '../services/user-service';

const UserProfile = () => {
  const { user, hasRole } = useAuth();
  const [profile, setProfile] = useState(null);
  const [editing, setEditing] = useState(false);
  const [formData, setFormData] = useState({});

  useEffect(() => {
    if (user) {
      setProfile(user);
      setFormData({
        firstName: user.firstName || '',
        lastName: user.lastName || '',
        phone: user.phone || ''
      });
    }
  }, [user]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const updatedUser = await userService.updateProfile(formData);
      setProfile(updatedUser);
      setEditing(false);
    } catch (error) {
      console.error('Failed to update profile:', error);
      alert('Failed to update profile');
    }
  };

  if (!profile) {
    return <div>Loading...</div>;
  }

  return (
    <div className="user-profile">
      <h2>User Profile</h2>
      
      {!editing ? (
        <div className="profile-view">
          <p><strong>Username:</strong> {profile.username}</p>
          <p><strong>Email:</strong> {profile.email}</p>
          <p><strong>Name:</strong> {profile.firstName} {profile.lastName}</p>
          <p><strong>Phone:</strong> {profile.phone || 'Not provided'}</p>
          <p><strong>Roles:</strong> {profile.roles?.join(', ')}</p>
          
          <button onClick={() => setEditing(true)}>
            Edit Profile
          </button>
        </div>
      ) : (
        <form onSubmit={handleSubmit} className="profile-edit">
          <div>
            <label>First Name:</label>
            <input
              type="text"
              value={formData.firstName}
              onChange={(e) => setFormData({...formData, firstName: e.target.value})}
            />
          </div>
          
          <div>
            <label>Last Name:</label>
            <input
              type="text"
              value={formData.lastName}
              onChange={(e) => setFormData({...formData, lastName: e.target.value})}
            />
          </div>
          
          <div>
            <label>Phone:</label>
            <input
              type="tel"
              value={formData.phone}
              onChange={(e) => setFormData({...formData, phone: e.target.value})}
            />
          </div>
          
          <div>
            <button type="submit">Save</button>
            <button type="button" onClick={() => setEditing(false)}>
              Cancel
            </button>
          </div>
        </form>
      )}
      
      {hasRole('ADMIN') && (
        <div className="admin-actions">
          <h3>Admin Actions</h3>
          <button onClick={() => userService.syncAllUsers()}>
            Sync All Users
          </button>
        </div>
      )}
    </div>
  );
};

export default UserProfile;
```

### Login Component (for Direct Access Grants)

```jsx
// components/Login.js
import React, { useState } from 'react';
import authService from '../auth-service';

const Login = ({ onLogin }) => {
  const [credentials, setCredentials] = useState({
    username: '',
    password: ''
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      await authService.login(credentials.username, credentials.password);
      onLogin();
    } catch (error) {
      setError('Login failed. Please check your credentials.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-form">
      <h2>Library Management System</h2>
      
      <form onSubmit={handleSubmit}>
        <div>
          <label>Username:</label>
          <input
            type="text"
            value={credentials.username}
            onChange={(e) => setCredentials({
              ...credentials,
              username: e.target.value
            })}
            required
          />
        </div>
        
        <div>
          <label>Password:</label>
          <input
            type="password"
            value={credentials.password}
            onChange={(e) => setCredentials({
              ...credentials,
              password: e.target.value
            })}
            required
          />
        </div>
        
        {error && <div className="error">{error}</div>}
        
        <button type="submit" disabled={loading}>
          {loading ? 'Logging in...' : 'Login'}
        </button>
      </form>
    </div>
  );
};

export default Login;
```

## Routing Setup

### React Router with Authentication

```jsx
// App.js with routing
import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { useAuth } from './contexts/AuthContext';
import ProtectedRoute from './components/ProtectedRoute';
import Login from './components/Login';
import UserProfile from './components/UserProfile';
import UserManagement from './components/UserManagement';
import LibraryCards from './components/LibraryCards';

const AppRoutes = () => {
  const { authenticated, loading } = useAuth();

  if (loading) {
    return <div>Loading...</div>;
  }

  return (
    <Router>
      <Routes>
        <Route 
          path="/login" 
          element={authenticated ? <Navigate to="/" /> : <Login />} 
        />
        
        <Route 
          path="/" 
          element={
            <ProtectedRoute>
              <UserProfile />
            </ProtectedRoute>
          } 
        />
        
        <Route 
          path="/users" 
          element={
            <ProtectedRoute requiredRoles={['ADMIN', 'LIBRARIAN']}>
              <UserManagement />
            </ProtectedRoute>
          } 
        />
        
        <Route 
          path="/library-cards" 
          element={
            <ProtectedRoute requiredRoles={['ADMIN', 'LIBRARIAN']}>
              <LibraryCards />
            </ProtectedRoute>
          } 
        />
        
        <Route 
          path="*" 
          element={<Navigate to="/" />} 
        />
      </Routes>
    </Router>
  );
};
```

## Error Handling

### Global Error Handler

```javascript
// utils/error-handler.js
export const handleApiError = (error) => {
  if (error.message.includes('401')) {
    // Unauthorized - redirect to login
    window.location.href = '/login';
    return;
  }
  
  if (error.message.includes('403')) {
    // Forbidden - show access denied message
    alert('Access denied. You do not have permission to perform this action.');
    return;
  }
  
  if (error.message.includes('500')) {
    // Server error
    alert('Server error. Please try again later.');
    return;
  }
  
  // Generic error
  console.error('API Error:', error);
  alert('An error occurred. Please try again.');
};
```

## Security Best Practices

### 1. Token Storage

```javascript
// Secure token storage (consider using secure HTTP-only cookies in production)
class SecureStorage {
  static setToken(token) {
    // In production, use secure HTTP-only cookies
    sessionStorage.setItem('access_token', token);
  }
  
  static getToken() {
    return sessionStorage.getItem('access_token');
  }
  
  static removeToken() {
    sessionStorage.removeItem('access_token');
  }
}
```

### 2. CSRF Protection

```javascript
// Add CSRF token to requests
const getCsrfToken = () => {
  return document.querySelector('meta[name="csrf-token"]')?.getAttribute('content');
};

// Include in API requests
headers: {
  'X-CSRF-Token': getCsrfToken(),
  // ... other headers
}
```

### 3. Content Security Policy

```html
<!-- Add to your HTML head -->
<meta http-equiv="Content-Security-Policy" 
      content="default-src 'self'; 
               script-src 'self' 'unsafe-inline'; 
               style-src 'self' 'unsafe-inline'; 
               connect-src 'self' http://localhost:8080 http://localhost:8888;">
```

## Testing

### Mock Authentication for Testing

```javascript
// test-utils/auth-mock.js
export const mockAuthContext = {
  user: {
    keycloakId: 'test-id',
    username: 'testuser',
    email: 'test@example.com',
    roles: ['USER'],
    permissions: ['read:books']
  },
  authenticated: true,
  loading: false,
  hasRole: (role) => ['USER'].includes(role),
  hasPermission: (permission) => ['read:books'].includes(permission),
  hasAnyRole: (...roles) => roles.some(role => ['USER'].includes(role))
};

// In your tests
import { render } from '@testing-library/react';
import { AuthContext } from '../contexts/AuthContext';
import { mockAuthContext } from './auth-mock';

const renderWithAuth = (component, authContext = mockAuthContext) => {
  return render(
    <AuthContext.Provider value={authContext}>
      {component}
    </AuthContext.Provider>
  );
};
```

This guide provides a comprehensive foundation for integrating frontend applications with the Library Management System's authentication and authorization system. Adapt the examples based on your specific frontend framework and requirements.