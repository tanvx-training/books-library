-- Create databases if they don't exist
CREATE DATABASE keycloak;
CREATE DATABASE library_management;

-- Grant privileges to the postgres user
GRANT ALL PRIVILEGES ON DATABASE keycloak TO postgres;
GRANT ALL PRIVILEGES ON DATABASE library_management TO postgres;