-- user-service
CREATE TABLE users
(
    id         SERIAL PRIMARY KEY,
    username   VARCHAR(50) UNIQUE                    NOT NULL,
    email      VARCHAR(100) UNIQUE                   NOT NULL,
    password   VARCHAR(100)                          NOT NULL,
    first_name VARCHAR(50),
    last_name  VARCHAR(50),
    phone      VARCHAR(20),
    created_at TIMESTAMP   DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by VARCHAR(20) DEFAULT 'SYSTEM'          NOT NULL,
    updated_at TIMESTAMP,
    updated_by VARCHAR(20),
    delete_flg BOOLEAN     DEFAULT FALSE
);

CREATE TABLE roles
(
    id         SERIAL PRIMARY KEY,
    name       VARCHAR(20) UNIQUE                    NOT NULL,
    created_at TIMESTAMP   DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by VARCHAR(20) DEFAULT 'SYSTEM'          NOT NULL,
    updated_at TIMESTAMP,
    updated_by VARCHAR(20),
    delete_flg BOOLEAN     DEFAULT FALSE
);

CREATE TABLE user_roles
(
    user_id INT REFERENCES users (id),
    role_id INT REFERENCES roles (id),
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE library_cards
(
    id          SERIAL PRIMARY KEY,
    user_id     INT REFERENCES users (id),
    card_number VARCHAR(20) UNIQUE                    NOT NULL,
    issue_date  DATE                                  NOT NULL,
    expiry_date DATE                                  NOT NULL,
    status      VARCHAR(20)                           NOT NULL,
    created_at  TIMESTAMP   DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by  VARCHAR(20) DEFAULT 'SYSTEM'          NOT NULL,
    updated_at  TIMESTAMP,
    updated_by  VARCHAR(20),
    delete_flg  BOOLEAN     DEFAULT FALSE
);

-- book-service
CREATE TABLE authors
(
    id         SERIAL PRIMARY KEY,
    name       VARCHAR(100)                          NOT NULL,
    biography  TEXT,
    created_at TIMESTAMP   DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by VARCHAR(20) DEFAULT 'SYSTEM'          NOT NULL,
    updated_at TIMESTAMP,
    updated_by VARCHAR(20),
    delete_flg BOOLEAN     DEFAULT FALSE
);

CREATE TABLE categories
(
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(256) UNIQUE                    NOT NULL,
    slug        VARCHAR(256) UNIQUE                    NOT NULL,
    description TEXT,
    created_at  TIMESTAMP   DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by  VARCHAR(20) DEFAULT 'SYSTEM'          NOT NULL,
    updated_at  TIMESTAMP,
    updated_by  VARCHAR(20),
    delete_flg  BOOLEAN     DEFAULT FALSE
);

CREATE TABLE publishers
(
    id         SERIAL PRIMARY KEY,
    name       VARCHAR(256)                          NOT NULL,
    address    TEXT,
    created_at TIMESTAMP   DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by VARCHAR(20) DEFAULT 'SYSTEM'          NOT NULL,
    updated_at TIMESTAMP,
    updated_by VARCHAR(20),
    delete_flg BOOLEAN     DEFAULT FALSE
);

CREATE TABLE books
(
    id               SERIAL PRIMARY KEY,
    title            VARCHAR(256)                          NOT NULL,
    isbn             VARCHAR(20) UNIQUE,
    publisher_id     INT REFERENCES publishers (id),
    publication_year INT,
    description      TEXT,
    cover_image_url  VARCHAR(1000),
    created_at       TIMESTAMP   DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by       VARCHAR(20) DEFAULT 'SYSTEM'          NOT NULL,
    updated_at       TIMESTAMP,
    updated_by       VARCHAR(20),
    delete_flg       BOOLEAN     DEFAULT FALSE
);

CREATE TABLE book_authors
(
    book_id   INT REFERENCES books (id),
    author_id INT REFERENCES authors (id),
    PRIMARY KEY (book_id, author_id)
);

CREATE TABLE book_copies
(
    id          SERIAL PRIMARY KEY,
    book_id     INT REFERENCES books (id),
    copy_number VARCHAR(20)                           NOT NULL,
    status      VARCHAR(20)                           NOT NULL, -- AVAILABLE, BORROWED, RESERVED, MAINTENANCE
    condition   VARCHAR(20),                                    -- NEW, GOOD, FAIR, POOR
    location    VARCHAR(50),                                    -- Shelf location
    created_at  TIMESTAMP   DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by  VARCHAR(20) DEFAULT 'SYSTEM'          NOT NULL,
    updated_at  TIMESTAMP,
    updated_by  VARCHAR(20),
    delete_flg  BOOLEAN     DEFAULT FALSE
        UNIQUE (book_id, copy_number)
);

CREATE TABLE borrowings
(
    id           SERIAL PRIMARY KEY,
    book_copy_id INT REFERENCES book_copies (id),
    user_id      INT                                      NOT NULL, -- References user_id from user-service
    borrow_date  DATE                                     NOT NULL,
    due_date     DATE                                     NOT NULL,
    return_date  DATE,
    fine_amount  DECIMAL(10, 2) DEFAULT 0.00,
    status       VARCHAR(20)                              NOT NULL, -- ACTIVE, RETURNED, OVERDUE
    created_at   TIMESTAMP      DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by   VARCHAR(20)    DEFAULT 'SYSTEM'          NOT NULL,
    updated_at   TIMESTAMP,
    updated_by   VARCHAR(20),
    delete_flg   BOOLEAN        DEFAULT FALSE
);

CREATE TABLE reservations
(
    id               SERIAL PRIMARY KEY,
    book_id          INT REFERENCES books (id),
    user_id          INT                                   NOT NULL, -- References user_id from user-service
    reservation_date TIMESTAMP                             NOT NULL,
    expiry_date      TIMESTAMP                             NOT NULL,
    status           VARCHAR(20)                           NOT NULL, -- PENDING, FULFILLED, CANCELLED, EXPIRED
    created_at       TIMESTAMP   DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by       VARCHAR(20) DEFAULT 'SYSTEM'          NOT NULL,
    updated_at       TIMESTAMP,
    updated_by       VARCHAR(20),
    delete_flg       BOOLEAN     DEFAULT FALSE
);

-- notification-service
CREATE TABLE notification_templates
(
    id         SERIAL PRIMARY KEY,
    name       VARCHAR(100)                          NOT NULL,
    type       VARCHAR(20)                           NOT NULL, -- EMAIL, SMS, PUSH
    subject    VARCHAR(200),
    content    TEXT                                  NOT NULL,
    created_at TIMESTAMP   DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by VARCHAR(20) DEFAULT 'SYSTEM'          NOT NULL,
    updated_at TIMESTAMP,
    updated_by VARCHAR(20),
    delete_flg BOOLEAN     DEFAULT FALSE
);

CREATE TABLE notification_preferences
(
    id                       SERIAL PRIMARY KEY,
    user_id                  INT                                   NOT NULL, -- References user_id from user-service
    email_enabled            BOOLEAN     DEFAULT TRUE,
    sms_enabled              BOOLEAN     DEFAULT FALSE,
    push_enabled             BOOLEAN     DEFAULT TRUE,
    borrow_notification      BOOLEAN     DEFAULT TRUE,
    return_reminder          BOOLEAN     DEFAULT TRUE,
    overdue_notification     BOOLEAN     DEFAULT TRUE,
    reservation_notification BOOLEAN     DEFAULT TRUE,
    created_at               TIMESTAMP   DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by               VARCHAR(20) DEFAULT 'SYSTEM'          NOT NULL,
    updated_at               TIMESTAMP,
    updated_by               VARCHAR(20),
    delete_flg               BOOLEAN     DEFAULT FALSE
        UNIQUE (user_id)
);

CREATE TABLE notifications
(
    id          SERIAL PRIMARY KEY,
    user_id     INT                                   NOT NULL, -- References user_id from user-service
    template_id INT REFERENCES notification_templates (id),
    title       VARCHAR(200)                          NOT NULL,
    content     TEXT                                  NOT NULL,
    type        VARCHAR(20)                           NOT NULL, -- EMAIL, SMS, PUSH
    status      VARCHAR(20)                           NOT NULL, -- SENT, DELIVERED, READ, FAILED
    sent_at     TIMESTAMP,
    read_at     TIMESTAMP,
    created_at  TIMESTAMP   DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by  VARCHAR(20) DEFAULT 'SYSTEM'          NOT NULL,
    updated_at  TIMESTAMP,
    updated_by  VARCHAR(20),
    delete_flg  BOOLEAN     DEFAULT FALSE
);

