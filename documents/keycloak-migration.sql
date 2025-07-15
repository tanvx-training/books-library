-- Migration script to update existing users table for Keycloak integration
-- Run this script if you have existing data that needs to be migrated

-- Step 1: Add keycloak_id column if it doesn't exist
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_name = 'users' AND column_name = 'keycloak_id') THEN
        ALTER TABLE users ADD COLUMN keycloak_id VARCHAR(36) UNIQUE;
    END IF;
END $$;

-- Step 2: Update created_by and updated_by columns to support keycloak_id
DO $$
BEGIN
    -- Check if created_by is not already VARCHAR(36)
    IF EXISTS (SELECT 1 FROM information_schema.columns 
               WHERE table_name = 'users' AND column_name = 'created_by' 
               AND data_type = 'character varying' AND character_maximum_length != 36) THEN
        ALTER TABLE users ALTER COLUMN created_by TYPE VARCHAR(36);
    END IF;
    
    -- Check if updated_by is not already VARCHAR(36)
    IF EXISTS (SELECT 1 FROM information_schema.columns 
               WHERE table_name = 'users' AND column_name = 'updated_by' 
               AND data_type = 'character varying' AND character_maximum_length != 36) THEN
        ALTER TABLE users ALTER COLUMN updated_by TYPE VARCHAR(36);
    END IF;
END $$;

-- Step 3: Update library_cards table for keycloak_id support
DO $$
BEGIN
    -- Update created_by column in library_cards
    IF EXISTS (SELECT 1 FROM information_schema.columns 
               WHERE table_name = 'library_cards' AND column_name = 'created_by' 
               AND data_type = 'character varying' AND character_maximum_length != 36) THEN
        ALTER TABLE library_cards ALTER COLUMN created_by TYPE VARCHAR(36);
    END IF;
    
    -- Update updated_by column in library_cards
    IF EXISTS (SELECT 1 FROM information_schema.columns 
               WHERE table_name = 'library_cards' AND column_name = 'updated_by' 
               AND data_type = 'character varying' AND character_maximum_length != 36) THEN
        ALTER TABLE library_cards ALTER COLUMN updated_by TYPE VARCHAR(36);
    END IF;
END $$;

-- Step 4: Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_users_keycloak_id ON users(keycloak_id);
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_library_cards_user_id ON library_cards(user_id);

-- Step 5: Insert default roles if they don't exist
INSERT INTO roles (name, created_by) 
SELECT 'USER', 'SYSTEM'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'USER');

INSERT INTO roles (name, created_by) 
SELECT 'LIBRARIAN', 'SYSTEM'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'LIBRARIAN');

INSERT INTO roles (name, created_by) 
SELECT 'ADMIN', 'SYSTEM'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ADMIN');

-- Step 6: Create sequence for users table if using BIGINT
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.sequences WHERE sequence_name = 'users_id_seq') THEN
        CREATE SEQUENCE users_id_seq START WITH 1;
        ALTER TABLE users ALTER COLUMN id SET DEFAULT nextval('users_id_seq');
        ALTER SEQUENCE users_id_seq OWNED BY users.id;
    END IF;
END $$;

-- Step 7: Create sequence for library_cards table if using BIGINT
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.sequences WHERE sequence_name = 'library_cards_id_seq') THEN
        CREATE SEQUENCE library_cards_id_seq START WITH 1;
        ALTER TABLE library_cards ALTER COLUMN id SET DEFAULT nextval('library_cards_id_seq');
        ALTER SEQUENCE library_cards_id_seq OWNED BY library_cards.id;
    END IF;
END $$;

-- Step 8: Add constraints
ALTER TABLE users ADD CONSTRAINT chk_users_keycloak_or_password 
CHECK (keycloak_id IS NOT NULL OR password IS NOT NULL);

-- Step 9: Update existing data (if any) - this is optional and depends on your data
-- You might want to run this manually after setting up Keycloak
-- UPDATE users SET keycloak_id = 'some-keycloak-id' WHERE username = 'existing-user';

COMMIT;