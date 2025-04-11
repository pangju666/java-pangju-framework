CREATE TABLE `test`
(
    `id`          bigint       NOT NULL AUTO_INCREMENT,
    `name`        varchar(255) NULL,
    `json_value`  json         NULL,
    `json_array`  json         NULL,
    list          varchar(500) NULL,
    clz           varchar(500) NULL,
    create_time   DATETIME     not null default CURRENT_TIMESTAMP,
    update_time   DATETIME     not null default CURRENT_TIMESTAMP,
    delete_status TINYINT(1)   not null default 0,
    delete_time   DATETIME     null,
    PRIMARY KEY (`id`)
);

INSERT INTO `test` (`id`, `name`, `json_value`, `json_array`, `list`, `clz`)
VALUES (1, 'test1', '{}', '[]', '', '');
INSERT INTO `test` (`id`, `name`, `json_value`, `json_array`, `list`, `clz`)
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
        ]',
        '123,321,123213,232131',
        'java.lang.String');
INSERT INTO `test` (`ID`, `NAME`, `json_value`, `json_array`, LIST, CLZ)
VALUES (3, 'test1', '{}', '[]', '', '');
INSERT INTO `test` (`ID`, `NAME`, `json_value`, `json_array`, LIST, CLZ)
VALUES (4, null, null, null, null, null);