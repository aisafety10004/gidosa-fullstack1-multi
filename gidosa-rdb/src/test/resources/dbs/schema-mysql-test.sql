-- schema-postgres-test.sql

 drop table if exists abc1^;
 create table abc1 (
     id varchar(64) primary key ,
     name varchar(64) not null,
     mobile varchar(13),
     email varchar(64)
 )^;
-- drop table if exists users.abc1^;
