DROP TABLE IF EXISTS users;
CREATE TABLE users
(
    `id`    BIGINT PRIMARY KEY,
    `name`  VARCHAR(64),
    `age`   INT,
    `email` VARCHAR(128)
);

DROP TABLE IF EXISTS docs;
CREATE TABLE docs
(
    `id`    BIGINT PRIMARY KEY,
    `title` VARCHAR(64),
    `meta`  JSON,
    `tags`  JSON
);