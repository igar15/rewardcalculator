DELETE FROM user_roles;
DELETE FROM users;
ALTER SEQUENCE global_seq RESTART WITH 100000;

INSERT INTO users (name, email, password)
VALUES ('User', 'user@yandex.ru', 'password'),
       ('Admin', 'admin@gmail.com', 'admin');

INSERT INTO user_roles(role, user_id)
VALUES ('DEPARTMENT_HEAD', 100000),
       ('ADMIN', 100001),
       ('DEPARTMENT_HEAD', 100001),
       ('ECONOMIST', 100001);
