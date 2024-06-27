-- Create additional databases
CREATE DATABASE bank_userservice;
CREATE DATABASE bank_marketservice;

-- Create user and grant privileges
-- CREATE USER myusername WITH PASSWORD 'mypassword';
GRANT ALL PRIVILEGES ON DATABASE bank_marketservice TO myusername;
GRANT ALL PRIVILEGES ON DATABASE bank_userservice TO myusername;

CREATE SEQUENCE user_sequence START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;