DELETE FROM user_managed_departments;
DELETE FROM user_roles;
DELETE FROM users;
DELETE FROM departments;
ALTER SEQUENCE global_seq RESTART WITH 100000;

INSERT INTO users (name, email, password)
VALUES ('User', 'user@yandex.ru', 'password'),
       ('Admin', 'admin@gmail.com', 'admin');

INSERT INTO user_roles(role, user_id)
VALUES ('DEPARTMENT_HEAD', 100000),
       ('ADMIN', 100001),
       ('DEPARTMENT_HEAD', 100001),
       ('ECONOMIST', 100001);

INSERT INTO departments (name)
VALUES ('Отдел № 1'),
       ('Отдел № 3'),
       ('Отдел № 2');

INSERT INTO user_managed_departments (user_id, department_id)
VALUES (100000, 100002),
       (100000, 100003);
