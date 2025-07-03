# Frontend Integration Guide for OAuth2/OIDC with Keycloak

This guide provides instructions for integrating a frontend application with the OAuth2/OIDC setup using Keycloak.

## 1. Prerequisites

- A running Keycloak instance (see KEYCLOAK_SETUP.md)
- A configured client in Keycloak for your frontend application
- A modern frontend framework (React, Angular, Vue.js, etc.)

## 2. Integration Options

There are several libraries available for integrating with Keycloak:

1. **keycloak-js**: Official Keycloak JavaScript adapter
2. **oidc-client-js**: Generic OIDC client library
3. Framework-specific libraries:
   - React: `react-keycloak`, `@react-keycloak/web`
   - Angular: `keycloak-angular`
   - Vue.js: `vue-keycloak`

This guide will focus on using `keycloak-js` as it's the official library and works with any framework.

## 3. Installation

### 3.1. Using npm/yarn

```bash
# npm
npm install keycloak-js

# yarn
yarn add keycloak-js
```

## 4. Basic Integration (React Example)

### 4.1. Create a Keycloak Service

Create a file `keycloak.js`:

```javascript
import Keycloak from 'keycloak-js';

// Keycloak initialization options
const keycloakConfig = {
  url: 'http://localhost:8090',
  realm: 'library-realm',
  clientId: 'library-frontend'
};

// Initialize Keycloak instance
const keycloak = new Keycloak(keycloakConfig);

// Setup token refresh
const initKeycloak = (onAuthenticatedCallback) => {
  keycloak.init({
    onLoad: 'check-sso',
    silentCheckSsoRedirectUri: window.location.origin + '/silent-check-sso.html',
    pkceMethod: 'S256', // PKCE is recommended for public clients
    checkLoginIframe: false // Disable iframe checking for better performance
  })
    .then((authenticated) => {
      if (authenticated) {
        // Setup token refresh
        setInterval(() => {
          keycloak.updateToken(70)
            .catch(() => {
              console.log('Failed to refresh token, logging out...');
              keycloak.logout();
            });
        }, 60000); // Refresh token every minute
      }
      onAuthenticatedCallback();
    })
    .catch(error => {
      console.error('Keycloak initialization error:', error);
    });
};

// Get the access token
const getToken = () => keycloak.token;

// Check if user is authenticated
const isAuthenticated = () => !!keycloak.token;

// Get user info
const getUserInfo = () => keycloak.tokenParsed;

// Login function
const login = keycloak.login;

// Logout function
const logout = keycloak.logout;

// Check if user has a specific role
const hasRole = (role) => {
  return keycloak.hasRealmRole(role);
};

// Export the Keycloak service
const KeycloakService = {
  initKeycloak,
  getToken,
  isAuthenticated,
  getUserInfo,
  login,
  logout,
  hasRole
};

export default KeycloakService;
```

### 4.2. Create Silent SSO Check Page

Create a file `public/silent-check-sso.html`:

```html
<!DOCTYPE html>
<html>
<head>
    <title>Silent SSO Check</title>
    <script src="http://localhost:8090/js/keycloak.js"></script>
    <script>
        window.onload = function() {
            parent.postMessage(location.href, location.origin);
        }
    </script>
</head>
<body>
    <!-- This page is used for silent SSO check -->
</body>
</html>
```

### 4.3. Create Auth Provider Component (React Context)

Create a file `AuthProvider.js`:

```jsx
import React, { createContext, useState, useEffect } from 'react';
import KeycloakService from './keycloak';

// Create a context
export const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [userInfo, setUserInfo] = useState(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    KeycloakService.initKeycloak(() => {
      setIsAuthenticated(KeycloakService.isAuthenticated());
      if (KeycloakService.isAuthenticated()) {
        setUserInfo(KeycloakService.getUserInfo());
      }
      setIsLoading(false);
    });
  }, []);

  const login = () => KeycloakService.login();
  const logout = () => KeycloakService.logout();
  const hasRole = (role) => KeycloakService.hasRole(role);

  // Create an auth object with authentication state and methods
  const auth = {
    isAuthenticated,
    userInfo,
    login,
    logout,
    hasRole,
    getToken: KeycloakService.getToken
  };

  // Show loading state while Keycloak is being initialized
  if (isLoading) {
    return <div>Loading authentication...</div>;
  }

  // Provide auth object to children components
  return (
    <AuthContext.Provider value={auth}>
      {children}
    </AuthContext.Provider>
  );
};

// Custom hook to use the auth context
export const useAuth = () => {
  const context = React.useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
```

### 4.4. Wrap Your App with AuthProvider

In your `App.js` or main component:

```jsx
import React from 'react';
import { AuthProvider } from './AuthProvider';
import MainContent from './MainContent';

function App() {
  return (
    <AuthProvider>
      <MainContent />
    </AuthProvider>
  );
}

export default App;
```

### 4.5. Create Protected Routes

Create a `PrivateRoute` component:

```jsx
import React from 'react';
import { Route, Redirect } from 'react-router-dom';
import { useAuth } from './AuthProvider';

const PrivateRoute = ({ component: Component, roles, ...rest }) => {
  const auth = useAuth();
  
  return (
    <Route
      {...rest}
      render={(props) => {
        // Check if user is authenticated
        if (!auth.isAuthenticated) {
          // Redirect to login page if not authenticated
          return <Redirect to="/login" />;
        }

        // Check if route requires specific roles
        if (roles && roles.length > 0) {
          const hasRequiredRole = roles.some(role => auth.hasRole(role));
          if (!hasRequiredRole) {
            // Redirect to unauthorized page if user doesn't have required role
            return <Redirect to="/unauthorized" />;
          }
        }

        // Render the protected component
        return <Component {...props} />;
      }}
    />
  );
};

export default PrivateRoute;
```

### 4.6. Use Private Routes in Your Router

```jsx
import React from 'react';
import { BrowserRouter as Router, Route, Switch } from 'react-router-dom';
import PrivateRoute from './PrivateRoute';
import Home from './Home';
import Login from './Login';
import AdminDashboard from './AdminDashboard';
import Unauthorized from './Unauthorized';

const AppRouter = () => {
  return (
    <Router>
      <Switch>
        <Route exact path="/" component={Home} />
        <Route path="/login" component={Login} />
        <Route path="/unauthorized" component={Unauthorized} />
        <PrivateRoute path="/books" component={Books} />
        <PrivateRoute path="/admin" component={AdminDashboard} roles={['ADMIN']} />
        <PrivateRoute path="/librarian" component={LibrarianDashboard} roles={['LIBRARIAN']} />
      </Switch>
    </Router>
  );
};

export default AppRouter;
```

### 4.7. Create API Service with Token

Create an API service that includes the token in requests:

```javascript
import { useAuth } from './AuthProvider';

const useApiService = () => {
  const auth = useAuth();

  const fetchWithAuth = async (url, options = {}) => {
    // Get the current token
    const token = auth.getToken();
    
    // If no token is available, redirect to login
    if (!token) {
      auth.login();
      return null;
    }

    // Add authorization header
    const headers = {
      ...options.headers,
      'Authorization': `Bearer ${token}`
    };

    try {
      const response = await fetch(url, {
        ...options,
        headers
      });

      // Handle 401 Unauthorized (token expired or invalid)
      if (response.status === 401) {
        auth.login(); // Redirect to login
        return null;
      }

      return response;
    } catch (error) {
      console.error('API request failed:', error);
      throw error;
    }
  };

  // Example API methods
  const getBooks = () => fetchWithAuth('http://localhost:8080/api/books')
    .then(response => response.json());

  const getBookById = (id) => fetchWithAuth(`http://localhost:8080/api/books/${id}`)
    .then(response => response.json());

  const createBook = (bookData) => fetchWithAuth('http://localhost:8080/api/books', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(bookData)
  }).then(response => response.json());

  return {
    fetchWithAuth,
    getBooks,
    getBookById,
    createBook
  };
};

export default useApiService;
```

## 5. Using Authentication in Components

### 5.1. Login Component

```jsx
import React from 'react';
import { useAuth } from './AuthProvider';

const Login = () => {
  const auth = useAuth();

  const handleLogin = () => {
    auth.login();
  };

  if (auth.isAuthenticated) {
    return (
      <div>
        <h2>You are already logged in!</h2>
        <button onClick={auth.logout}>Logout</button>
      </div>
    );
  }

  return (
    <div>
      <h2>Please log in</h2>
      <button onClick={handleLogin}>Login with Keycloak</button>
    </div>
  );
};

export default Login;
```

### 5.2. User Profile Component

```jsx
import React from 'react';
import { useAuth } from './AuthProvider';

const UserProfile = () => {
  const auth = useAuth();

  if (!auth.isAuthenticated) {
    return <div>Please log in to view your profile</div>;
  }

  const { userInfo } = auth;

  return (
    <div>
      <h2>User Profile</h2>
      <p>Username: {userInfo.preferred_username}</p>
      <p>Email: {userInfo.email}</p>
      <p>Full Name: {userInfo.name}</p>
      <h3>Roles:</h3>
      <ul>
        {userInfo.realm_access.roles.map(role => (
          <li key={role}>{role}</li>
        ))}
      </ul>
      <button onClick={auth.logout}>Logout</button>
    </div>
  );
};

export default UserProfile;
```

### 5.3. Role-Based Content

```jsx
import React from 'react';
import { useAuth } from './AuthProvider';
import useApiService from './apiService';

const BookManagement = () => {
  const auth = useAuth();
  const api = useApiService();
  const [books, setBooks] = React.useState([]);

  React.useEffect(() => {
    api.getBooks().then(data => setBooks(data.data));
  }, []);

  return (
    <div>
      <h2>Book Management</h2>
      
      <div>
        {/* Show book list to all authenticated users */}
        <h3>Book List</h3>
        <ul>
          {books.map(book => (
            <li key={book.id}>{book.title} by {book.author}</li>
          ))}
        </ul>
      </div>
      
      {/* Show add/edit buttons only to librarians and admins */}
      {(auth.hasRole('LIBRARIAN') || auth.hasRole('ADMIN')) && (
        <div>
          <h3>Management Options</h3>
          <button>Add New Book</button>
          <button>Edit Selected Book</button>
        </div>
      )}
      
      {/* Show delete button only to admins */}
      {auth.hasRole('ADMIN') && (
        <div>
          <h3>Admin Options</h3>
          <button>Delete Selected Book</button>
          <button>View Audit Logs</button>
        </div>
      )}
    </div>
  );
};

export default BookManagement;
```

## 6. Advanced Topics

### 6.1. Token Refresh Strategy

The example above uses a simple interval-based token refresh strategy. For a more robust approach:

1. Calculate token expiry time from the JWT (exp claim)
2. Set a timer to refresh the token a few minutes before expiry
3. Handle refresh failures gracefully

### 6.2. Handling Session Timeouts

```javascript
// Enhanced token refresh logic
const setupTokenRefresh = () => {
  const tokenExpiryTime = keycloak.tokenParsed.exp * 1000; // Convert to milliseconds
  const currentTime = new Date().getTime();
  const timeToExpiry = tokenExpiryTime - currentTime;
  
  // Refresh 1 minute before expiry
  const refreshTime = timeToExpiry - 60000;
  
  // Set timeout to refresh token
  setTimeout(() => {
    keycloak.updateToken(70)
      .then(refreshed => {
        if (refreshed) {
          console.log('Token refreshed');
          // Setup the next refresh
          setupTokenRefresh();
        } else {
          console.log('Token not refreshed, still valid');
        }
      })
      .catch(() => {
        console.log('Failed to refresh token, logging out');
        keycloak.logout();
      });
  }, Math.max(refreshTime, 0)); // Ensure timeout is not negative
};
```

### 6.3. Handling Offline Mode

For applications that need to work offline, consider:

1. Using offline tokens (offline_access scope)
2. Implementing local storage with encryption
3. Syncing when connectivity is restored

## 7. Security Considerations

1. **Never store tokens in localStorage or sessionStorage** (vulnerable to XSS attacks)
2. Use PKCE (Proof Key for Code Exchange) for public clients
3. Implement proper CORS configuration on your backend
4. Use HTTPS for all communication
5. Validate tokens on both client and server sides
6. Implement proper error handling for authentication failures

## 8. Testing

### 8.1. Mock Authentication for Tests

Create a mock auth provider for testing:

```jsx
export const MockAuthProvider = ({ children, isAuthenticated = true, userRoles = ['USER'] }) => {
  const mockAuth = {
    isAuthenticated,
    userInfo: {
      preferred_username: 'test-user',
      email: 'test@example.com',
      name: 'Test User',
      realm_access: {
        roles: userRoles
      }
    },
    login: jest.fn(),
    logout: jest.fn(),
    hasRole: (role) => userRoles.includes(role),
    getToken: () => 'mock-token'
  };

  return (
    <AuthContext.Provider value={mockAuth}>
      {children}
    </AuthContext.Provider>
  );
};
```

### 8.2. Example Test

```jsx
import React from 'react';
import { render, screen } from '@testing-library/react';
import { MockAuthProvider } from './MockAuthProvider';
import UserProfile from './UserProfile';

test('renders user profile when authenticated', () => {
  render(
    <MockAuthProvider isAuthenticated={true} userRoles={['USER']}>
      <UserProfile />
    </MockAuthProvider>
  );
  
  expect(screen.getByText(/Username: test-user/i)).toBeInTheDocument();
  expect(screen.getByText(/Email: test@example.com/i)).toBeInTheDocument();
});

test('shows login message when not authenticated', () => {
  render(
    <MockAuthProvider isAuthenticated={false}>
      <UserProfile />
    </MockAuthProvider>
  );
  
  expect(screen.getByText(/Please log in/i)).toBeInTheDocument();
});
```

## 9. Troubleshooting

### 9.1. Common Issues

1. **CORS errors**: Ensure Keycloak and your API have proper CORS configuration
2. **Redirect URI mismatch**: Check that the redirect URI in Keycloak matches your application
3. **Token validation failures**: Verify that your backend is correctly validating tokens
4. **Silent refresh issues**: Check that silent-check-sso.html is properly configured

### 9.2. Debugging Tips

1. Use browser developer tools to inspect network requests
2. Check Keycloak server logs for authentication errors
3. Add debug logging to your authentication flow
4. Use JWT debugging tools to inspect token contents

## 10. Performance Optimization

1. Minimize token size by requesting only necessary claims
2. Use silent check-sso for session verification
3. Implement token caching strategies
4. Optimize token refresh timing

By following this guide, you should be able to successfully integrate your frontend application with Keycloak for secure authentication and authorization. 