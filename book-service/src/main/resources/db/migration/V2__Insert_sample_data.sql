-- =====================================================
-- Book Service Sample Data
-- PostgreSQL Sample Data Insertion Script
-- =====================================================

-- =====================================================
-- 1. SAMPLE AUTHORS
-- =====================================================
INSERT INTO authors (id, name, biography, delete_flg, created_by, updated_by) VALUES
(1, 'F. Scott Fitzgerald', 'American novelist and short story writer, widely regarded as one of the greatest American writers of the 20th century.', FALSE, 'system', 'system'),
(2, 'Harper Lee', 'American novelist best known for her 1960 novel To Kill a Mockingbird.', FALSE, 'system', 'system'),
(3, 'George Orwell', 'English novelist, essayist, journalist and critic whose work is marked by lucid prose, awareness of social injustice, and fierce opposition to totalitarianism.', FALSE, 'system', 'system'),
(4, 'Jane Austen', 'English novelist known primarily for her six major novels, which interpret, critique and comment upon the British landed gentry at the end of the 18th century.', FALSE, 'system', 'system'),
(5, 'Mark Twain', 'American writer, humorist, entrepreneur, publisher, and lecturer.', FALSE, 'system', 'system'),
(6, 'Ernest Hemingway', 'American novelist, short-story writer, and journalist.', FALSE, 'system', 'system'),
(7, 'Agatha Christie', 'English writer known for her sixty-six detective novels and fourteen short story collections.', FALSE, 'system', 'system'),
(8, 'J.K. Rowling', 'British author, best known for the Harry Potter series.', FALSE, 'system', 'system'),
(9, 'Stephen King', 'American author of horror, supernatural fiction, suspense, crime, science-fiction, and fantasy novels.', FALSE, 'system', 'system'),
(10, 'Toni Morrison', 'American novelist, essayist, book editor, and college professor.', FALSE, 'system', 'system');

-- =====================================================
-- 2. SAMPLE PUBLISHERS
-- =====================================================
INSERT INTO publishers (id, name, address, delete_flg, created_by, updated_by) VALUES
(1, 'Penguin Random House', '1745 Broadway, New York, NY 10019, USA', FALSE, 'system', 'system'),
(2, 'HarperCollins Publishers', '195 Broadway, New York, NY 10007, USA', FALSE, 'system', 'system'),
(3, 'Simon & Schuster', '1230 Avenue of the Americas, New York, NY 10020, USA', FALSE, 'system', 'system'),
(4, 'Macmillan Publishers', '120 Broadway, New York, NY 10271, USA', FALSE, 'system', 'system'),
(5, 'Hachette Book Group', '1290 Avenue of the Americas, New York, NY 10104, USA', FALSE, 'system', 'system'),
(6, 'Scholastic Corporation', '557 Broadway, New York, NY 10012, USA', FALSE, 'system', 'system'),
(7, 'Pearson Education', '221 River Street, Hoboken, NJ 07030, USA', FALSE, 'system', 'system'),
(8, 'Oxford University Press', 'Great Clarendon Street, Oxford OX2 6DP, UK', FALSE, 'system', 'system'),
(9, 'Cambridge University Press', 'University Printing House, Cambridge CB2 8BS, UK', FALSE, 'system', 'system'),
(10, 'Bloomsbury Publishing', '50 Bedford Square, London WC1B 3DP, UK', FALSE, 'system', 'system');

-- =====================================================
-- 3. SAMPLE CATEGORIES
-- =====================================================
INSERT INTO categories (id, name, slug, description, delete_flg, created_by, updated_by) VALUES
(1, 'Fiction', 'fiction', 'Fictional literature including novels, short stories, and novellas', FALSE, 'system', 'system'),
(2, 'Non-Fiction', 'non-fiction', 'Factual books including biographies, history, science, and self-help', FALSE, 'system', 'system'),
(3, 'Science Fiction', 'science-fiction', 'Fiction dealing with futuristic concepts and advanced science and technology', FALSE, 'system', 'system'),
(4, 'Fantasy', 'fantasy', 'Fiction involving magical or supernatural elements', FALSE, 'system', 'system'),
(5, 'Mystery', 'mystery', 'Fiction dealing with puzzling crimes, especially murder', FALSE, 'system', 'system'),
(6, 'Romance', 'romance', 'Fiction focusing on romantic relationships', FALSE, 'system', 'system'),
(7, 'Thriller', 'thriller', 'Fiction characterized by fast pacing, frequent action, and resourceful heroes', FALSE, 'system', 'system'),
(8, 'Horror', 'horror', 'Fiction intended to frighten, unsettle, or create suspense', FALSE, 'system', 'system'),
(9, 'Biography', 'biography', 'Non-fiction accounts of real people''s lives', FALSE, 'system', 'system'),
(10, 'History', 'history', 'Non-fiction books about past events', FALSE, 'system', 'system'),
(11, 'Science', 'science', 'Non-fiction books about scientific topics', FALSE, 'system', 'system'),
(12, 'Technology', 'technology', 'Books about technology, programming, and computing', FALSE, 'system', 'system'),
(13, 'Business', 'business', 'Books about business, economics, and management', FALSE, 'system', 'system'),
(14, 'Self-Help', 'self-help', 'Books designed to help readers improve their lives', FALSE, 'system', 'system'),
(15, 'Children', 'children', 'Books specifically written for children', FALSE, 'system', 'system');

-- =====================================================
-- 4. SAMPLE BOOKS
-- =====================================================
INSERT INTO books (id, title, isbn, publisher_id, publication_year, description, cover_image_url, delete_flg, created_by, updated_by) VALUES
(1, 'The Great Gatsby', '978-0-7432-7356-5', 3, 1925, 'A classic American novel set in the Jazz Age, exploring themes of wealth, love, and the American Dream.', 'https://example.com/covers/great-gatsby.jpg', FALSE, 'system', 'system'),
(2, 'To Kill a Mockingbird', '978-0-06-112008-4', 2, 1960, 'A gripping tale of racial injustice and childhood innocence in the American South.', 'https://example.com/covers/mockingbird.jpg', FALSE, 'system', 'system'),
(3, '1984', '978-0-452-28423-4', 1, 1949, 'A dystopian social science fiction novel about totalitarian control and surveillance.', 'https://example.com/covers/1984.jpg', FALSE, 'system', 'system'),
(4, 'Pride and Prejudice', '978-0-14-143951-8', 1, 1813, 'A romantic novel about manners, upbringing, morality, education, and marriage in Georgian England.', 'https://example.com/covers/pride-prejudice.jpg', FALSE, 'system', 'system'),
(5, 'The Adventures of Huckleberry Finn', '978-0-486-28061-5', 4, 1884, 'A novel about a boy''s journey down the Mississippi River with an escaped slave.', 'https://example.com/covers/huckleberry-finn.jpg', FALSE, 'system', 'system'),
(6, 'The Old Man and the Sea', '978-0-684-80122-3', 3, 1952, 'A short novel about an aging Cuban fisherman''s struggle with a giant marlin.', 'https://example.com/covers/old-man-sea.jpg', FALSE, 'system', 'system'),
(7, 'Murder on the Orient Express', '978-0-06-207350-4', 2, 1934, 'A detective novel featuring Hercule Poirot solving a murder on a luxury train.', 'https://example.com/covers/orient-express.jpg', FALSE, 'system', 'system'),
(8, 'Harry Potter and the Philosopher''s Stone', '978-0-7475-3269-9', 10, 1997, 'The first novel in the Harry Potter series about a young wizard''s adventures.', 'https://example.com/covers/harry-potter-1.jpg', FALSE, 'system', 'system'),
(9, 'The Shining', '978-0-385-12167-5', 1, 1977, 'A horror novel about a family''s terrifying experience at an isolated hotel.', 'https://example.com/covers/shining.jpg', FALSE, 'system', 'system'),
(10, 'Beloved', '978-1-4000-3341-6', 1, 1987, 'A novel about the lasting effects of slavery on a family in post-Civil War Ohio.', 'https://example.com/covers/beloved.jpg', FALSE, 'system', 'system');

-- =====================================================
-- 5. BOOK-AUTHOR RELATIONSHIPS
-- =====================================================
INSERT INTO book_authors (book_id, author_id) VALUES
(1, 1),  -- The Great Gatsby - F. Scott Fitzgerald
(2, 2),  -- To Kill a Mockingbird - Harper Lee
(3, 3),  -- 1984 - George Orwell
(4, 4),  -- Pride and Prejudice - Jane Austen
(5, 5),  -- The Adventures of Huckleberry Finn - Mark Twain
(6, 6),  -- The Old Man and the Sea - Ernest Hemingway
(7, 7),  -- Murder on the Orient Express - Agatha Christie
(8, 8),  -- Harry Potter - J.K. Rowling
(9, 9),  -- The Shining - Stephen King
(10, 10); -- Beloved - Toni Morrison

-- =====================================================
-- 6. BOOK-CATEGORY RELATIONSHIPS
-- =====================================================
INSERT INTO book_categories (book_id, category_id) VALUES
(1, 1),   -- The Great Gatsby - Fiction
(2, 1),   -- To Kill a Mockingbird - Fiction
(3, 1),   -- 1984 - Fiction
(3, 3),   -- 1984 - Science Fiction
(4, 1),   -- Pride and Prejudice - Fiction
(4, 6),   -- Pride and Prejudice - Romance
(5, 1),   -- The Adventures of Huckleberry Finn - Fiction
(6, 1),   -- The Old Man and the Sea - Fiction
(7, 1),   -- Murder on the Orient Express - Fiction
(7, 5),   -- Murder on the Orient Express - Mystery
(8, 1),   -- Harry Potter - Fiction
(8, 4),   -- Harry Potter - Fantasy
(8, 15),  -- Harry Potter - Children
(9, 1),   -- The Shining - Fiction
(9, 8),   -- The Shining - Horror
(10, 1);  -- Beloved - Fiction

-- =====================================================
-- 7. SAMPLE BOOK COPIES
-- =====================================================
INSERT INTO book_copies (id, book_id, copy_number, status, condition, location, acquired_date, created_by, updated_by) VALUES
-- The Great Gatsby copies
(1, 1, 'COPY-001', 'AVAILABLE', 'EXCELLENT', 'A-1-001', '2024-01-01 10:00:00', 'system', 'system'),
(2, 1, 'COPY-002', 'AVAILABLE', 'GOOD', 'A-1-002', '2024-01-01 10:00:00', 'system', 'system'),
(3, 1, 'COPY-003', 'BORROWED', 'GOOD', 'A-1-003', '2024-01-01 10:00:00', 'system', 'system'),

-- To Kill a Mockingbird copies
(4, 2, 'COPY-001', 'AVAILABLE', 'NEW', 'A-2-001', '2024-01-02 10:00:00', 'system', 'system'),
(5, 2, 'COPY-002', 'AVAILABLE', 'EXCELLENT', 'A-2-002', '2024-01-02 10:00:00', 'system', 'system'),

-- 1984 copies
(6, 3, 'COPY-001', 'AVAILABLE', 'GOOD', 'A-3-001', '2024-01-03 10:00:00', 'system', 'system'),
(7, 3, 'COPY-002', 'RESERVED', 'EXCELLENT', 'A-3-002', '2024-01-03 10:00:00', 'system', 'system'),
(8, 3, 'COPY-003', 'AVAILABLE', 'FAIR', 'A-3-003', '2024-01-03 10:00:00', 'system', 'system'),

-- Pride and Prejudice copies
(9, 4, 'COPY-001', 'AVAILABLE', 'EXCELLENT', 'A-4-001', '2024-01-04 10:00:00', 'system', 'system'),
(10, 4, 'COPY-002', 'AVAILABLE', 'GOOD', 'A-4-002', '2024-01-04 10:00:00', 'system', 'system'),

-- The Adventures of Huckleberry Finn copies
(11, 5, 'COPY-001', 'AVAILABLE', 'GOOD', 'A-5-001', '2024-01-05 10:00:00', 'system', 'system'),
(12, 5, 'COPY-002', 'MAINTENANCE', 'POOR', 'MAINTENANCE', '2024-01-05 10:00:00', 'system', 'system'),

-- The Old Man and the Sea copies
(13, 6, 'COPY-001', 'AVAILABLE', 'EXCELLENT', 'A-6-001', '2024-01-06 10:00:00', 'system', 'system'),
(14, 6, 'COPY-002', 'AVAILABLE', 'GOOD', 'A-6-002', '2024-01-06 10:00:00', 'system', 'system'),
(15, 6, 'COPY-003', 'AVAILABLE', 'FAIR', 'A-6-003', '2024-01-06 10:00:00', 'system', 'system'),

-- Murder on the Orient Express copies
(16, 7, 'COPY-001', 'AVAILABLE', 'NEW', 'A-7-001', '2024-01-07 10:00:00', 'system', 'system'),
(17, 7, 'COPY-002', 'BORROWED', 'EXCELLENT', 'A-7-002', '2024-01-07 10:00:00', 'system', 'system'),

-- Harry Potter copies
(18, 8, 'COPY-001', 'AVAILABLE', 'EXCELLENT', 'A-8-001', '2024-01-08 10:00:00', 'system', 'system'),
(19, 8, 'COPY-002', 'AVAILABLE', 'GOOD', 'A-8-002', '2024-01-08 10:00:00', 'system', 'system'),
(20, 8, 'COPY-003', 'AVAILABLE', 'GOOD', 'A-8-003', '2024-01-08 10:00:00', 'system', 'system'),
(21, 8, 'COPY-004', 'BORROWED', 'FAIR', 'A-8-004', '2024-01-08 10:00:00', 'system', 'system'),

-- The Shining copies
(22, 9, 'COPY-001', 'AVAILABLE', 'GOOD', 'A-9-001', '2024-01-09 10:00:00', 'system', 'system'),
(23, 9, 'COPY-002', 'AVAILABLE', 'EXCELLENT', 'A-9-002', '2024-01-09 10:00:00', 'system', 'system'),

-- Beloved copies
(24, 10, 'COPY-001', 'AVAILABLE', 'EXCELLENT', 'A-10-001', '2024-01-10 10:00:00', 'system', 'system'),
(25, 10, 'COPY-002', 'RESERVED', 'GOOD', 'A-10-002', '2024-01-10 10:00:00', 'system', 'system');

-- =====================================================
-- 8. UPDATE BORROWED COPIES WITH BORROWER INFO
-- =====================================================
-- Update borrowed copies with sample borrower information
UPDATE book_copies SET 
    current_borrower_keycloak_id = 'user-123-456-789',
    borrowed_date = '2024-01-15 14:30:00',
    due_date = '2024-01-29 23:59:59'
WHERE id = 3; -- The Great Gatsby COPY-003

UPDATE book_copies SET 
    current_borrower_keycloak_id = 'user-987-654-321',
    borrowed_date = '2024-01-16 09:15:00',
    due_date = '2024-01-30 23:59:59'
WHERE id = 17; -- Murder on the Orient Express COPY-002

UPDATE book_copies SET 
    current_borrower_keycloak_id = 'user-555-666-777',
    borrowed_date = '2024-01-17 16:45:00',
    due_date = '2024-01-31 23:59:59'
WHERE id = 21; -- Harry Potter COPY-004

-- =====================================================
-- 9. RESET SEQUENCES TO CONTINUE FROM SAMPLE DATA
-- =====================================================
SELECT setval('authors_id_seq', (SELECT MAX(id) FROM authors) + 1);
SELECT setval('publishers_id_seq', (SELECT MAX(id) FROM publishers) + 1);
SELECT setval('categories_id_seq', (SELECT MAX(id) FROM categories) + 1);
SELECT setval('books_id_seq', (SELECT MAX(id) FROM books) + 1);
SELECT setval('book_copies_id_seq', (SELECT MAX(id) FROM book_copies) + 1);

-- =====================================================
-- 10. VERIFICATION QUERIES (FOR TESTING)
-- =====================================================

-- Verify data insertion
-- SELECT 'Authors: ' || COUNT(*) FROM authors WHERE delete_flg = FALSE;
-- SELECT 'Publishers: ' || COUNT(*) FROM publishers WHERE delete_flg = FALSE;
-- SELECT 'Categories: ' || COUNT(*) FROM categories WHERE delete_flg = FALSE;
-- SELECT 'Books: ' || COUNT(*) FROM books WHERE delete_flg = FALSE;
-- SELECT 'Book Copies: ' || COUNT(*) FROM book_copies WHERE delete_flg = FALSE;

-- Test the complete books view
-- SELECT * FROM v_books_complete LIMIT 5;

-- Test overdue books view
-- SELECT * FROM v_overdue_book_copies;