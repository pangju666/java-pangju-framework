CREATE TABLE `test`
(
    `id`         bigint UNSIGNED NOT NULL AUTO_INCREMENT,
    `name`       varchar(255)    NULL,
    `json`       json            NULL,
    `json_array` json            NULL,
    PRIMARY KEY (`id`)
);

INSERT INTO `test` (`id`, `name`, `json`, `json_array`)
VALUES (1, 'test1', '{}', '[]');
INSERT INTO `test` (`id`, `name`, `json`, `json_array`)
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