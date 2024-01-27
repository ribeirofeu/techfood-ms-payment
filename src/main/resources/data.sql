CREATE TABLE IF NOT EXISTS payment  (
  id INT PRIMARY KEY,
  created_date TIMESTAMP,
  qr_code VARCHAR(255),
  status INT,
  total_value DOUBLE
);

-- Insert initial data into PAYMENT table
INSERT INTO "payment" VALUES (2, '2024-01-21 23:43:00.099574', 'NTc1NjY0NzUzMQ==', 0, 10.1);
INSERT INTO "payment" VALUES (3, '2024-01-21 23:44:00.099574', 'MzM2Nzg3ODQyOQ==', 1, 5.0);
INSERT INTO "payment" VALUES (4, '2024-01-21 23:44:10.099574', 'Nzk2MDM5MDA1MA==', 0, 15.0);
INSERT INTO "payment" VALUES (5, '2024-01-21 23:44:15.099574', 'NjcyMzgzMDgyMA==', 0, 9.0);

