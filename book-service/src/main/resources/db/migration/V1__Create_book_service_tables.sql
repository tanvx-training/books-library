-- =====================================================
-- Book Service Database Schema
-- PostgreSQL Database Creation Script
-- =====================================================

-- Enable UUID extension for potential future use
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- =====================================================
-- 1. AUTHORS TABLE
-- =====================================================
CREATE TABLE authors (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    biography TEXT,
    delete_flg BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(36),
    updated_by VARCHAR(36)
);

-- Indexes for authors table
CREATE INDEX idx_authors_name ON authors(name);
CREATE INDEX idx_authors_delete_flg ON authors(delete_flg);
CREATE INDEX idx_authors_created_at ON authors(created_at);

-- =====================================================
-- 2. PUBLISHERS TABLE
-- =====================================================
CREATE TABLE publishers (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(256) NOT NULL,
    address TEXT,
    delete_flg BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(36),
    updated_by VARCHAR(36)
);

-- Indexes for publishers table
CREATE INDEX idx_publishers_name ON publishers(name);
CREATE INDEX idx_publishers_delete_flg ON publishers(delete_flg);
CREATE INDEX idx_publishers_created_at ON publishers(created_at);

-- =====================================================
-- 3. CATEGORIES TABLE
-- =====================================================
CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(256) NOT NULL,
    slug VARCHAR(256) NOT NULL UNIQUE,
    description TEXT,
    delete_flg BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(36),
    updated_by VARCHAR(36)
);

-- Indexes for categories table
CREATE INDEX idx_categories_name ON categories(name);
CREATE INDEX idx_categories_slug ON categories(slug);
CREATE INDEX idx_categories_delete_flg ON categories(delete_flg);
CREATE INDEX idx_categories_created_at ON categories(created_at);

-- =====================================================
-- 4. BOOKS TABLE
-- =====================================================
CREATE TABLE books (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    isbn VARCHAR(20) UNIQUE,
    publisher_id BIGINT,
    publication_year INTEGER,
    description TEXT,
    cover_image_url VARCHAR(500),
    delete_flg BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(36),
    updated_by VARCHAR(36),
    
    -- Foreign key constraint
    CONSTRAINT fk_books_publisher FOREIGN KEY (publisher_id) REFERENCES publishers(id)
);

-- Indexes for books table
CREATE INDEX idx_books_title ON books(title);
CREATE INDEX idx_books_isbn ON books(isbn);
CREATE INDEX idx_books_publisher_id ON books(publisher_id);
CREATE INDEX idx_books_publication_year ON books(publication_year);
CREATE INDEX idx_books_delete_flg ON books(delete_flg);
CREATE INDEX idx_books_created_at ON books(created_at);

-- Full-text search index for book search functionality
CREATE INDEX idx_books_title_gin ON books USING gin(to_tsvector('english', title));
CREATE INDEX idx_books_description_gin ON books USING gin(to_tsvector('english', description));

-- =====================================================
-- 5. BOOK_AUTHORS TABLE (Many-to-Many relationship)
-- =====================================================
CREATE TABLE book_authors (
    book_id BIGINT NOT NULL,
    author_id BIGINT NOT NULL,
    
    PRIMARY KEY (book_id, author_id),
    
    -- Foreign key constraints
    CONSTRAINT fk_book_authors_book FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE,
    CONSTRAINT fk_book_authors_author FOREIGN KEY (author_id) REFERENCES authors(id) ON DELETE CASCADE
);

-- Indexes for book_authors table
CREATE INDEX idx_book_authors_book_id ON book_authors(book_id);
CREATE INDEX idx_book_authors_author_id ON book_authors(author_id);

-- =====================================================
-- 6. BOOK_CATEGORIES TABLE (Many-to-Many relationship)
-- =====================================================
CREATE TABLE book_categories (
    book_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    
    PRIMARY KEY (book_id, category_id),
    
    -- Foreign key constraints
    CONSTRAINT fk_book_categories_book FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE,
    CONSTRAINT fk_book_categories_category FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE
);

-- Indexes for book_categories table
CREATE INDEX idx_book_categories_book_id ON book_categories(book_id);
CREATE INDEX idx_book_categories_category_id ON book_categories(category_id);

-- =====================================================
-- 7. BOOK_COPIES TABLE
-- =====================================================
CREATE TABLE book_copies (
    id BIGSERIAL PRIMARY KEY,
    book_id BIGINT NOT NULL,
    copy_number VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
    condition VARCHAR(20) DEFAULT 'GOOD',
    location VARCHAR(50),
    acquired_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    current_borrower_keycloak_id VARCHAR(36),
    borrowed_date TIMESTAMP,
    due_date TIMESTAMP,
    delete_flg BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(36),
    updated_by VARCHAR(36),
    
    -- Foreign key constraint
    CONSTRAINT fk_book_copies_book FOREIGN KEY (book_id) REFERENCES books(id),
    
    -- Unique constraint on book_id and copy_number
    CONSTRAINT uk_book_copies_book_copy UNIQUE (book_id, copy_number),
    
    -- Check constraints for enum values
    CONSTRAINT chk_book_copies_status CHECK (status IN ('AVAILABLE', 'BORROWED', 'RESERVED', 'MAINTENANCE', 'LOST', 'DAMAGED')),
    CONSTRAINT chk_book_copies_condition CHECK (condition IN ('NEW', 'EXCELLENT', 'GOOD', 'FAIR', 'POOR')),
    
    -- Business logic constraints
    CONSTRAINT chk_book_copies_borrower_dates CHECK (
        (status = 'BORROWED' AND current_borrower_keycloak_id IS NOT NULL AND borrowed_date IS NOT NULL AND due_date IS NOT NULL) OR
        (status != 'BORROWED' AND (current_borrower_keycloak_id IS NULL OR borrowed_date IS NULL OR due_date IS NULL))
    )
);

-- Indexes for book_copies table
CREATE INDEX idx_book_copies_book_id ON book_copies(book_id);
CREATE INDEX idx_book_copies_status ON book_copies(status);
CREATE INDEX idx_book_copies_condition ON book_copies(condition);
CREATE INDEX idx_book_copies_location ON book_copies(location);
CREATE INDEX idx_book_copies_borrower ON book_copies(current_borrower_keycloak_id);
CREATE INDEX idx_book_copies_due_date ON book_copies(due_date);
CREATE INDEX idx_book_copies_delete_flg ON book_copies(delete_flg);
CREATE INDEX idx_book_copies_created_at ON book_copies(created_at);

-- Composite indexes for common queries
CREATE INDEX idx_book_copies_book_status ON book_copies(book_id, status);
CREATE INDEX idx_book_copies_borrower_status ON book_copies(current_borrower_keycloak_id, status);

-- =====================================================
-- 8. TRIGGERS FOR UPDATED_AT TIMESTAMPS
-- =====================================================

-- Function to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Triggers for all tables
CREATE TRIGGER update_authors_updated_at BEFORE UPDATE ON authors FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_publishers_updated_at BEFORE UPDATE ON publishers FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_categories_updated_at BEFORE UPDATE ON categories FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_books_updated_at BEFORE UPDATE ON books FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_book_copies_updated_at BEFORE UPDATE ON book_copies FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- =====================================================
-- 9. VIEWS FOR COMMON QUERIES
-- =====================================================

-- View for books with complete information
CREATE VIEW v_books_complete AS
SELECT 
    b.id,
    b.title,
    b.isbn,
    b.publication_year,
    b.description,
    b.cover_image_url,
    b.delete_flg,
    b.created_at,
    b.updated_at,
    p.id as publisher_id,
    p.name as publisher_name,
    p.address as publisher_address,
    -- Count of available copies
    COALESCE(bc_stats.total_copies, 0) as total_copies,
    COALESCE(bc_stats.available_copies, 0) as available_copies,
    COALESCE(bc_stats.borrowed_copies, 0) as borrowed_copies
FROM books b
LEFT JOIN publishers p ON b.publisher_id = p.id
LEFT JOIN (
    SELECT 
        book_id,
        COUNT(*) as total_copies,
        COUNT(CASE WHEN status = 'AVAILABLE' THEN 1 END) as available_copies,
        COUNT(CASE WHEN status = 'BORROWED' THEN 1 END) as borrowed_copies
    FROM book_copies 
    WHERE delete_flg = FALSE
    GROUP BY book_id
) bc_stats ON b.id = bc_stats.book_id
WHERE b.delete_flg = FALSE;

-- View for overdue book copies
CREATE VIEW v_overdue_book_copies AS
SELECT 
    bc.id,
    bc.book_id,
    bc.copy_number,
    bc.current_borrower_keycloak_id,
    bc.borrowed_date,
    bc.due_date,
    CURRENT_DATE - bc.due_date::date as days_overdue,
    b.title as book_title,
    b.isbn
FROM book_copies bc
JOIN books b ON bc.book_id = b.id
WHERE bc.status = 'BORROWED' 
  AND bc.due_date < CURRENT_TIMESTAMP
  AND bc.delete_flg = FALSE
  AND b.delete_flg = FALSE;

-- =====================================================
-- 10. FUNCTIONS FOR BUSINESS LOGIC
-- =====================================================

-- Function to check if a book can be deleted
CREATE OR REPLACE FUNCTION can_book_be_deleted(book_id_param BIGINT)
RETURNS BOOLEAN AS $$
BEGIN
    -- Check if there are any active book copies (borrowed, reserved, etc.)
    RETURN NOT EXISTS (
        SELECT 1 FROM book_copies 
        WHERE book_id = book_id_param 
          AND status IN ('BORROWED', 'RESERVED')
          AND delete_flg = FALSE
    );
END;
$$ LANGUAGE plpgsql;

-- Function to check if an author can be deleted
CREATE OR REPLACE FUNCTION can_author_be_deleted(author_id_param BIGINT)
RETURNS BOOLEAN AS $$
BEGIN
    -- Check if author has any books
    RETURN NOT EXISTS (
        SELECT 1 FROM book_authors ba
        JOIN books b ON ba.book_id = b.id
        WHERE ba.author_id = author_id_param 
          AND b.delete_flg = FALSE
    );
END;
$$ LANGUAGE plpgsql;

-- Function to check if a publisher can be deleted
CREATE OR REPLACE FUNCTION can_publisher_be_deleted(publisher_id_param BIGINT)
RETURNS BOOLEAN AS $$
BEGIN
    -- Check if publisher has any books
    RETURN NOT EXISTS (
        SELECT 1 FROM books 
        WHERE publisher_id = publisher_id_param 
          AND delete_flg = FALSE
    );
END;
$$ LANGUAGE plpgsql;

-- Function to check if a category can be deleted
CREATE OR REPLACE FUNCTION can_category_be_deleted(category_id_param BIGINT)
RETURNS BOOLEAN AS $$
BEGIN
    -- Check if category has any books
    RETURN NOT EXISTS (
        SELECT 1 FROM book_categories bc
        JOIN books b ON bc.book_id = b.id
        WHERE bc.category_id = category_id_param 
          AND b.delete_flg = FALSE
    );
END;
$$ LANGUAGE plpgsql;

-- =====================================================
-- 11. COMMENTS FOR DOCUMENTATION
-- =====================================================

-- Table comments
COMMENT ON TABLE authors IS 'Authors of books in the library system';
COMMENT ON TABLE publishers IS 'Publishers of books in the library system';
COMMENT ON TABLE categories IS 'Categories for organizing books';
COMMENT ON TABLE books IS 'Books available in the library system';
COMMENT ON TABLE book_authors IS 'Many-to-many relationship between books and authors';
COMMENT ON TABLE book_categories IS 'Many-to-many relationship between books and categories';
COMMENT ON TABLE book_copies IS 'Physical copies of books available for borrowing';

-- Column comments for important fields
COMMENT ON COLUMN book_copies.status IS 'Status of the book copy: AVAILABLE, BORROWED, RESERVED, MAINTENANCE, LOST, DAMAGED';
COMMENT ON COLUMN book_copies.condition IS 'Physical condition: NEW, EXCELLENT, GOOD, FAIR, POOR';
COMMENT ON COLUMN book_copies.current_borrower_keycloak_id IS 'Keycloak ID of the current borrower (if borrowed)';
COMMENT ON COLUMN book_copies.copy_number IS 'Unique identifier for this copy within the book';

-- =====================================================
-- 12. INITIAL SEQUENCES SETUP
-- =====================================================

-- Ensure sequences start from a reasonable number
SELECT setval('authors_id_seq', 1000, false);
SELECT setval('publishers_id_seq', 1000, false);
SELECT setval('categories_id_seq', 1000, false);
SELECT setval('books_id_seq', 1000, false);
SELECT setval('book_copies_id_seq', 1000, false);