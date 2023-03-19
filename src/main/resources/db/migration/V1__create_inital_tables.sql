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
    followers_count INTEGER NOT NULL,
    following_count INTEGER NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    created_date TIMESTAMP NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    updated_date TIMESTAMP NOT NULL,
    mark_for_delete BOOLEAN NOT NULL
);

CREATE TABLE tweet (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    message TEXT NOT NULL,
    creator_id VARCHAR(36) NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    created_date TIMESTAMP NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    updated_date TIMESTAMP NOT NULL,
    mark_for_delete BOOLEAN NOT NULL,
    FOREIGN KEY (creator_id) REFERENCES account(id)
);

CREATE TABLE account_relationship (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    follower_id VARCHAR(36) NOT NULL,
    followed_id VARCHAR(36) NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    created_date TIMESTAMP NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    updated_date TIMESTAMP NOT NULL,
    mark_for_delete BOOLEAN NOT NULL,
    FOREIGN KEY (follower_id) REFERENCES account(id),
    FOREIGN KEY (followed_id) REFERENCES account(id)
);