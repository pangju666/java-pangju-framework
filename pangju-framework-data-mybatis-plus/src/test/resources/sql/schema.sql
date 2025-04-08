create table PUBLIC.TEST
(
    ID INTEGER auto_increment,
    NAME CHARACTER VARYING,
    JSON JSON,
    constraint TEST_PK
        primary key (ID)
);

insert into PUBLIC.TEST (ID, NAME, JSON)
values (8, null, '{
  "test": "hello world"
}'),
       (9, null, '{}');