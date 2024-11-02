CREATE TABLE IF NOT EXISTS user_profile (
  id INT NOT NULL generated always as identity PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  email VARCHAR(255) NOT NULL
);