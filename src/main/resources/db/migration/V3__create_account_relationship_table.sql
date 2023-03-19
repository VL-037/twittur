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