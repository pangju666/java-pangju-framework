INSERT INTO users (id, name, age, email)
VALUES (1, 'Alice', 30, 'alice@example.com'),
       (2, 'Bob', 25, NULL),
       (3, 'Alice', 28, 'alice2@example.com'),
       (4, 'Carol', 25, 'carol@example.com');

INSERT INTO docs (id, title, meta, tags)
VALUES (1, 'doc1', '{"author":"Alice","views":10}', '["news","tech"]'),
       (2, 'doc2', '{"author":"Bob","views":7}', '[]'),
       (3, 'doc3', '{}', '["misc"]'),
       (4, 'doc4', NULL, NULL);