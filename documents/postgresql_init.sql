-- Tạo extension cho UUID nếu chưa có
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ====================================================================================================
-- member-service database schema
-- ====================================================================================================

-- Bảng users
CREATE TABLE users
(
    id            BIGSERIAL PRIMARY KEY,
    public_id     UUID NOT NULL DEFAULT uuid_generate_v4() UNIQUE,
    keycloak_id   VARCHAR(36) NOT NULL UNIQUE,
    username      VARCHAR(50) UNIQUE,
    email         VARCHAR(255) UNIQUE,
    first_name    VARCHAR(50),
    last_name     VARCHAR(50),
    phone_number  VARCHAR(20),
    address       TEXT,
    date_of_birth DATE,
    role          VARCHAR(20) NOT NULL DEFAULT 'MEMBER' CHECK (role IN ('MEMBER', 'LIBRARIAN', 'ADMIN')),
    is_active     BOOLEAN NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by    VARCHAR(36), -- keycloak_id of the user who created this record
    updated_by    VARCHAR(36), -- keycloak_id of the user who last updated this record
    deleted_at    TIMESTAMPTZ
);

-- Bảng library_cards
CREATE TABLE library_cards
(
    id            BIGSERIAL PRIMARY KEY,
    public_id     UUID NOT NULL DEFAULT uuid_generate_v4() UNIQUE,
    card_number   VARCHAR(20) NOT NULL UNIQUE,
    user_id       BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE, -- Internal FK within member-service
    issue_date    DATE NOT NULL DEFAULT CURRENT_DATE,
    expiry_date   DATE NOT NULL,
    status        VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'INACTIVE', 'EXPIRED', 'LOST', 'BLOCKED')),
    created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by    VARCHAR(36),
    updated_by    VARCHAR(36),
    deleted_at    TIMESTAMPTZ
);

-- Indexes for member-service
CREATE INDEX idx_users_keycloak_id ON users(keycloak_id);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_library_cards_user_id ON library_cards(user_id);

-- Triggers for member-service
CREATE OR REPLACE FUNCTION update_timestamp_member_service()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER set_users_timestamp_member_service
    BEFORE UPDATE ON users
    FOR EACH ROW
    EXECUTE FUNCTION update_timestamp_member_service();

CREATE TRIGGER set_library_cards_timestamp_member_service
    BEFORE UPDATE ON library_cards
    FOR EACH ROW
    EXECUTE FUNCTION update_timestamp_member_service();


-- ====================================================================================================
-- catalog-service database schema
-- ====================================================================================================

-- Bảng authors
CREATE TABLE authors
(
    id            BIGSERIAL PRIMARY KEY,
    public_id     UUID NOT NULL DEFAULT uuid_generate_v4() UNIQUE,
    name          VARCHAR(100) NOT NULL,
    biography     TEXT,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by    VARCHAR(36),
    updated_by    VARCHAR(36),
    deleted_at    TIMESTAMPTZ
);

-- Bảng categories
CREATE TABLE categories
(
    id            BIGSERIAL PRIMARY KEY,
    public_id     UUID NOT NULL DEFAULT uuid_generate_v4() UNIQUE,
    name          VARCHAR(100) NOT NULL UNIQUE,
    slug          VARCHAR(100) NOT NULL UNIQUE,
    description   TEXT,
    parent_id     BIGINT REFERENCES categories(id), -- Internal FK within catalog-service
    created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by    VARCHAR(36),
    updated_by    VARCHAR(36),
    deleted_at    TIMESTAMPTZ
);

-- Bảng publishers
CREATE TABLE publishers
(
    id            BIGSERIAL PRIMARY KEY,
    public_id     UUID NOT NULL DEFAULT uuid_generate_v4() UNIQUE,
    name          VARCHAR(100) NOT NULL,
    address       TEXT,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by    VARCHAR(36),
    updated_by    VARCHAR(36),
    deleted_at    TIMESTAMPTZ
);

-- Bảng books
CREATE TABLE books
(
    id                BIGSERIAL PRIMARY KEY,
    public_id         UUID NOT NULL DEFAULT uuid_generate_v4() UNIQUE,
    title             VARCHAR(255) NOT NULL,
    isbn              VARCHAR(20) UNIQUE,
    publisher_id      BIGINT REFERENCES publishers(id), -- Internal FK within catalog-service
    publication_year  SMALLINT,
    description       TEXT,
    language          VARCHAR(20),
    number_of_pages   INTEGER,
    cover_image_url   VARCHAR(1000),
    created_at        TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at        TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by        VARCHAR(36),
    updated_by        VARCHAR(36),
    deleted_at        TIMESTAMPTZ
);

-- Bảng book_authors (bảng trung gian)
CREATE TABLE book_authors
(
    book_id       BIGINT NOT NULL REFERENCES books(id) ON DELETE CASCADE, -- Internal FK within catalog-service
    author_id     BIGINT NOT NULL REFERENCES authors(id) ON DELETE CASCADE, -- Internal FK within catalog-service
    PRIMARY KEY (book_id, author_id)
);

-- Bảng book_categories (bảng trung gian)
CREATE TABLE book_categories
(
    book_id       BIGINT NOT NULL REFERENCES books(id) ON DELETE CASCADE, -- Internal FK within catalog-service
    category_id   BIGINT NOT NULL REFERENCES categories(id) ON DELETE CASCADE, -- Internal FK within catalog-service
    PRIMARY KEY (book_id, category_id)
);

-- Bảng book_copies
CREATE TABLE book_copies
(
    id            BIGSERIAL PRIMARY KEY,
    public_id     UUID NOT NULL DEFAULT uuid_generate_v4() UNIQUE,
    book_id       BIGINT NOT NULL REFERENCES books(id) ON DELETE CASCADE, -- Internal FK within catalog-service
    copy_number   VARCHAR(20) NOT NULL,
    status        VARCHAR(20) NOT NULL CHECK (status IN ('AVAILABLE', 'BORROWED', 'RESERVED', 'MAINTENANCE', 'LOST')),
    condition     VARCHAR(20) CHECK (condition IN ('NEW', 'GOOD', 'FAIR', 'POOR', 'DAMAGED')),
    location      VARCHAR(50),
    created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by    VARCHAR(36),
    updated_by    VARCHAR(36),
    deleted_at    TIMESTAMPTZ,
    UNIQUE (book_id, copy_number)
);

-- Indexes for catalog-service
CREATE INDEX idx_books_isbn ON books(isbn);
CREATE INDEX idx_books_publisher_id ON books(publisher_id);
CREATE INDEX idx_book_copies_book_id ON book_copies(book_id);

-- Triggers for catalog-service
CREATE OR REPLACE FUNCTION update_timestamp_catalog_service()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER set_authors_timestamp_catalog_service
    BEFORE UPDATE ON authors
    FOR EACH ROW
    EXECUTE FUNCTION update_timestamp_catalog_service();

CREATE TRIGGER set_categories_timestamp_catalog_service
    BEFORE UPDATE ON categories
    FOR EACH ROW
    EXECUTE FUNCTION update_timestamp_catalog_service();

CREATE TRIGGER set_publishers_timestamp_catalog_service
    BEFORE UPDATE ON publishers
    FOR EACH ROW
    EXECUTE FUNCTION update_timestamp_catalog_service();

CREATE TRIGGER set_books_timestamp_catalog_service
    BEFORE UPDATE ON books
    FOR EACH ROW
    EXECUTE FUNCTION update_timestamp_catalog_service();

CREATE TRIGGER set_book_copies_timestamp_catalog_service
    BEFORE UPDATE ON book_copies
    FOR EACH ROW
    EXECUTE FUNCTION update_timestamp_catalog_service();


-- ====================================================================================================
-- loan-service database schema
-- ====================================================================================================

-- Bảng borrowings
CREATE TABLE borrowings
(
    id            BIGSERIAL PRIMARY KEY,
    public_id     UUID NOT NULL DEFAULT uuid_generate_v4() UNIQUE,
    book_copy_public_id UUID NOT NULL, -- References book_copies.public_id from catalog-service
    user_public_id UUID NOT NULL,      -- References users.public_id from member-service
    borrow_date   DATE NOT NULL DEFAULT CURRENT_DATE,
    due_date      DATE NOT NULL,
    return_date   DATE,
    status        VARCHAR(20) NOT NULL CHECK (status IN ('ACTIVE', 'RETURNED', 'OVERDUE', 'LOST')),
    created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by    VARCHAR(36),
    updated_by    VARCHAR(36),
    deleted_at    TIMESTAMPTZ
);

-- Bảng fines
CREATE TABLE fines
(
    id            BIGSERIAL PRIMARY KEY,
    public_id     UUID NOT NULL DEFAULT uuid_generate_v4() UNIQUE,
    borrowing_id  BIGINT NOT NULL REFERENCES borrowings(id), -- Internal FK within loan-service
    amount        DECIMAL(10, 2) NOT NULL,
    reason        TEXT,
    status        VARCHAR(20) NOT NULL CHECK (status IN ('PENDING', 'PAID', 'WAIVED')),
    paid_at       TIMESTAMPTZ,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by    VARCHAR(36),
    updated_by    VARCHAR(36),
    deleted_at    TIMESTAMPTZ
);

-- Bảng reservations
CREATE TABLE reservations
(
    id                BIGSERIAL PRIMARY KEY,
    public_id         UUID NOT NULL DEFAULT uuid_generate_v4() UNIQUE,
    book_public_id    UUID NOT NULL, -- References books.public_id from catalog-service
    user_public_id    UUID NOT NULL, -- References users.public_id from member-service
    reservation_date  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    expiry_date       TIMESTAMPTZ NOT NULL,
    status            VARCHAR(20) NOT NULL CHECK (status IN ('PENDING', 'FULFILLED', 'CANCELLED', 'EXPIRED')),
    created_at        TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at        TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by        VARCHAR(36),
    updated_by        VARCHAR(36),
    deleted_at        TIMESTAMPTZ
);

-- Bảng book_bags
CREATE TABLE book_bags
(
    id            BIGSERIAL PRIMARY KEY,
    user_public_id UUID NOT NULL UNIQUE, -- References users.public_id from member-service
    created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Bảng book_bag_items
CREATE TABLE book_bag_items
(
    id            BIGSERIAL PRIMARY KEY,
    book_bag_id   BIGINT NOT NULL REFERENCES book_bags(id) ON DELETE CASCADE, -- Internal FK within loan-service
    book_public_id UUID NOT NULL, -- References books.public_id from catalog-service
    quantity      SMALLINT NOT NULL DEFAULT 1,
    added_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (book_bag_id, book_public_id)
);

-- Bảng library_policies
CREATE TABLE library_policies
(
    id            SERIAL PRIMARY KEY,
    policy_name   VARCHAR(100) NOT NULL UNIQUE,
    policy_value  VARCHAR(255) NOT NULL,
    description   TEXT,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by    VARCHAR(36),
    updated_by    VARCHAR(36)
);

-- Indexes for loan-service
CREATE INDEX idx_borrowings_book_copy_public_id ON borrowings(book_copy_public_id);
CREATE INDEX idx_borrowings_user_public_id ON borrowings(user_public_id);
CREATE INDEX idx_reservations_book_public_id ON reservations(book_public_id);
CREATE INDEX idx_reservations_user_public_id ON reservations(user_public_id);
CREATE INDEX idx_fines_borrowing_id ON fines(borrowing_id);
CREATE INDEX idx_book_bags_user_public_id ON book_bags(user_public_id);
CREATE INDEX idx_book_bag_items_book_bag_id ON book_bag_items(book_bag_id);
CREATE INDEX idx_book_bag_items_book_public_id ON book_bag_items(book_public_id);

-- Triggers for loan-service
CREATE OR REPLACE FUNCTION update_timestamp_loan_service()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER set_borrowings_timestamp_loan_service
    BEFORE UPDATE ON borrowings
    FOR EACH ROW
    EXECUTE FUNCTION update_timestamp_loan_service();

CREATE TRIGGER set_fines_timestamp_loan_service
    BEFORE UPDATE ON fines
    FOR EACH ROW
    EXECUTE FUNCTION update_timestamp_loan_service();

CREATE TRIGGER set_reservations_timestamp_loan_service
    BEFORE UPDATE ON reservations
    FOR EACH ROW
    EXECUTE FUNCTION update_timestamp_loan_service();

CREATE TRIGGER set_book_bags_timestamp_loan_service
    BEFORE UPDATE ON book_bags
    FOR EACH ROW
    EXECUTE FUNCTION update_timestamp_loan_service();

CREATE TRIGGER set_library_policies_timestamp_loan_service
    BEFORE UPDATE ON library_policies
    FOR EACH ROW
    EXECUTE FUNCTION update_timestamp_loan_service();


-- ====================================================================================================
-- notification-service database schema
-- ====================================================================================================

-- Bảng notifications
CREATE TABLE notifications
(
    id            BIGSERIAL PRIMARY KEY,
    public_id     UUID NOT NULL DEFAULT uuid_generate_v4() UNIQUE,
    user_public_id UUID NOT NULL, -- References users.public_id from member-service
    title         VARCHAR(255) NOT NULL,
    content       TEXT NOT NULL,
    type          VARCHAR(20) NOT NULL CHECK (type IN ('EMAIL', 'SMS', 'PUSH')),
    status        VARCHAR(20) NOT NULL CHECK (status IN ('SENT', 'DELIVERED', 'READ', 'FAILED')),
    sent_at       TIMESTAMPTZ,
    read_at       TIMESTAMPTZ,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Bảng notification_preferences
CREATE TABLE notification_preferences
(
    id                       BIGSERIAL PRIMARY KEY,
    user_public_id           UUID NOT NULL UNIQUE, -- References users.public_id from member-service
    email_enabled            BOOLEAN NOT NULL DEFAULT TRUE,
    sms_enabled              BOOLEAN NOT NULL DEFAULT FALSE,
    push_enabled             BOOLEAN NOT NULL DEFAULT TRUE,
    borrow_notification      BOOLEAN NOT NULL DEFAULT TRUE,
    return_reminder          BOOLEAN NOT NULL DEFAULT TRUE,
    overdue_notification     BOOLEAN NOT NULL DEFAULT TRUE,
    reservation_notification BOOLEAN NOT NULL DEFAULT TRUE,
    created_at               TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at               TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Indexes for notification-service
CREATE INDEX idx_notifications_user_public_id ON notifications(user_public_id);
CREATE INDEX idx_notification_preferences_user_public_id ON notification_preferences(user_public_id);

-- Triggers for notification-service
CREATE OR REPLACE FUNCTION update_timestamp_notification_service()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER set_notification_preferences_timestamp_notification_service
    BEFORE UPDATE ON notification_preferences
    FOR EACH ROW
    EXECUTE FUNCTION update_timestamp_notification_service();