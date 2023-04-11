CREATE TABLE account (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    date_of_birth DATE,
    username VARCHAR(255) NOT NULL,
    account_name VARCHAR(255) NOT NULL,
    bio TEXT,
    email_address VARCHAR(255) NOT NULL,
    phone_number VARCHAR(255),
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    tweets_count INTEGER NOT NULL,
    followers_count INTEGER NOT NULL,
    following_count INTEGER NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    created_date TIMESTAMP NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    updated_date TIMESTAMP NOT NULL,
    mark_for_delete BOOLEAN NOT NULL
);

CREATE TABLE token(
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    account_id VARCHAR(36) NOT NULL,
    token VARCHAR(255) NOT NULL,
    type VARCHAR(20) NOT NULL,
    is_expired BOOLEAN NOT NULL,
    is_revoked BOOLEAN NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    created_date TIMESTAMP NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    updated_date TIMESTAMP NOT NULL,
    mark_for_delete BOOLEAN NOT NULL,
    FOREIGN KEY (account_id) REFERENCES account(id)
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

CREATE TABLE direct_message (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    sender_id VARCHAR(36) NOT NULL,
    recipient_id VARCHAR(36) NOT NULL,
    message TEXT NOT NULL,
    status VARCHAR(10) NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    created_date TIMESTAMP NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    updated_date TIMESTAMP NOT NULL,
    mark_for_delete BOOLEAN NOT NULL,
    FOREIGN KEY (sender_id) REFERENCES account(id),
    FOREIGN KEY (recipient_id) REFERENCES account(id)
);

CREATE TABLE notification (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    sender_id VARCHAR(36) NOT NULL,
    recipient_id VARCHAR(36) NOT NULL,
    title TEXT NOT NULL,
    message TEXT NOT NULL,
    image_url TEXT NOT NULL,
    redirect_url TEXT NOT NULL,
    type VARCHAR(20) NOT NULL,
    has_read BOOLEAN NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    created_date TIMESTAMP NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    updated_date TIMESTAMP NOT NULL,
    mark_for_delete BOOLEAN NOT NULL,
    FOREIGN KEY (recipient_id) REFERENCES account(id)
);

CREATE TABLE email (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    recipient VARCHAR(255) NOT NULL,
    subject VARCHAR(255) NOT NULL,
    body TEXT NOT NULL,
    is_sent BOOLEAN NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    created_date TIMESTAMP NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    updated_date TIMESTAMP NOT NULL,
    mark_for_delete BOOLEAN NOT NULL
);