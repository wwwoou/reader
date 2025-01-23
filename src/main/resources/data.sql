-- 插入默认分类
INSERT INTO categories (name, description) VALUES
('Fiction', 'Fictional literature including novels and short stories'),
('Non-Fiction', 'Non-fictional works including biographies and educational books'),
('Science', 'Scientific books including physics, chemistry, and biology'),
('Technology', 'Technology related books including programming and engineering'),
('History', 'Historical books and documentaries'),
('Philosophy', 'Books about philosophical thoughts and theories')
ON DUPLICATE KEY UPDATE name=name;

-- 插入示例图书
INSERT INTO books (title, author, isbn, category_id, publisher, publication_year, total_copies, available_copies, description) VALUES
('Introduction to Java Programming', 'John Smith', '9781234567890', 
 (SELECT id FROM categories WHERE name = 'Technology'),
 'Tech Publications', 2023, 5, 5, 'A comprehensive guide to Java programming'),
 
('The Art of Computer Programming', 'Donald Knuth', '9780201896831', 
 (SELECT id FROM categories WHERE name = 'Technology'),
 'Addison-Wesley Professional', 1968, 3, 3, 'Fundamental algorithms and programming techniques'),
 
('A Brief History of Time', 'Stephen Hawking', '9780553380163', 
 (SELECT id FROM categories WHERE name = 'Science'),
 'Bantam', 1988, 4, 4, 'An exploration of cosmology and physics')
ON DUPLICATE KEY UPDATE isbn=isbn; 