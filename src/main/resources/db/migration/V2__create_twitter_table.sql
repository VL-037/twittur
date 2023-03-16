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