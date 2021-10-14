DELETE FROM user_managed_departments;
DELETE FROM user_roles;
DELETE FROM users;
DELETE FROM employee_rewards;
DELETE FROM employees;
DELETE FROM department_rewards;
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

INSERT INTO positions (name, salary, department_id)
VALUES ('position 1 name', 40200, 100002),
       ('position 2 name', 35700, 100002),
       ('position 3 name', 60100, 100002);

INSERT INTO employees (name, position_id)
VALUES ('employee 1 name', 100005),
       ('employee 2 name', 100005),
       ('employee 3 name', 100006);

INSERT INTO payment_periods (period, required_hours_worked)
VALUES ('2021-01-01', 120),
       ('2021-02-01', 150.75),
       ('2021-03-01', 176.50);

INSERT INTO department_rewards (department_id, payment_period_id, allocated_amount, distributed_amount)
VALUES (100002, 100011, 40800, 40800),
       (100002, 100012, 40800, 40800),
       (100003, 100011, 40800, 40800);

INSERT INTO employee_rewards (employee_id, department_reward_id, hours_worked, hours_worked_reward, additional_reward, penalty)
VALUES (100008, 100015, 150.75, 12060, 0, 0),
       (100009, 100015, 150.75, 10710, 0, 0),
       (100010, 100015, 150.75, 18030, 0, 0),
       (100008, 100016, 176.50, 12060, 0, 0);