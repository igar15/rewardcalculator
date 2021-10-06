DELETE FROM user_managed_departments;
DELETE FROM user_roles;
DELETE FROM users;
DELETE FROM employees;
DELETE FROM departments;
DELETE FROM positions;
DELETE FROM payment_periods;
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

INSERT INTO positions (name, salary)
VALUES ('position 1 name', 40200),
       ('position 2 name', 35700),
       ('position 3 name', 60100);

INSERT INTO employees (name, department_id, position_id)
VALUES ('employee 1 name', 100002, 100005),
       ('employee 2 name', 100002, 100005),
       ('employee 3 name', 100002, 100006);

INSERT INTO payment_periods (period, required_hours_worked)
VALUES ('2021-01-01', 120),
       ('2021-02-01', 150.75),
       ('2021-03-01', 176.50);