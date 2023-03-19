CREATE TABLE account (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    date_of_birth DATE,
    username VARCHAR(255) NOT NULL,
    account_name VARCHAR(255) NOT NULL,
    bio TEXT,
    email_address VARCHAR(255) NOT NULL,
    phone_number VARCHAR(255),
    salt VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    tweets_count INTEGER NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    created_date TIMESTAMP NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    updated_date TIMESTAMP NOT NULL,
    mark_for_delete BOOLEAN NOT NULL
);