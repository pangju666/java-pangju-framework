create table PUBLIC.TEST
(
    ID INTEGER auto_increment,
    NAME CHARACTER VARYING,
    JSON_VALUE JSON,
    JSON_ARRAY JSON,
    constraint TEST_PK
        primary key (ID)
);

INSERT INTO `TEST` (`ID`, `NAME`, `JSON_VALUE`, `JSON_ARRAY`)
VALUES (1, 'test1', '{}', '[]');
INSERT INTO `TEST` (`ID`, `NAME`, `JSON_VALUE`, `JSON_ARRAY`)
VALUES (2, 'test2',
        '{
          "age": 1,
          "obj": {
            "child": "test"
          },
          "flag": false,
          "test": "hello world",
          "array": [
            "hello world",
            {
              "child": "test"
            },
            null
          ],
          "null_value": null
        }',
        '[
          "hello world",
          {
            "child": "test"
          },
          null
        ]');
INSERT INTO `TEST` (`ID`, `NAME`, `JSON_VALUE`, `JSON_ARRAY`)
VALUES (3, 'test1', '{}', '[]');
INSERT INTO `TEST` (`ID`, `NAME`, `JSON_VALUE`, `JSON_ARRAY`)
VALUES (4, null, null, null);