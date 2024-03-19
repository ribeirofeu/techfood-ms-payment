-- Insert initial data into PAYMENT table
INSERT IGNORE INTO payment (id, customer_id, total_value, created_date, status) VALUES (1, 2, 0, NOW(), 0);
