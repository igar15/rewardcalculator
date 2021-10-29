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
VALUES ('Department head name', 'departmenthead@yandex.ru', '{noop}password'),
       ('Admin name', 'admin@gmail.com', '{noop}admin'),
       ('Economist name', 'economist@yandex.ru', '{noop}password'),
       ('Personnel Officer name', 'personnelofficer@yandex.ru', '{noop}password');

INSERT INTO user_roles(role, user_id)
VALUES ('DEPARTMENT_HEAD', 100000),
       ('ADMIN', 100001),
       ('DEPARTMENT_HEAD', 100001),
       ('ECONOMIST', 100001),
       ('ECONOMIST', 100002),
       ('PERSONNEL_OFFICER', 100003);

INSERT INTO departments (name)
VALUES ('Отдел № 1'),
       ('Отдел № 3'),
       ('Отдел № 2');

INSERT INTO user_managed_departments (user_id, department_id)
VALUES (100000, 100004),
       (100000, 100005);

INSERT INTO positions (name, salary, chief_position, department_id)
VALUES ('position 1 name', 40200, false, 100004),
       ('position 2 name', 35700, false, 100004),
       ('position 3 name', 60100, true, 100004),
       ('position name', 25000, false, 100006);

INSERT INTO employees (name, position_id)
VALUES ('employee 1 name', 100007),
       ('employee 2 name', 100007),
       ('employee 3 name', 100008),
       ('employee name', 100010);

INSERT INTO payment_periods (period, required_hours_worked)
VALUES ('2021-01-01', 120),
       ('2021-02-01', 150.75),
       ('2021-03-01', 176.50);

INSERT INTO department_rewards (department_id, payment_period_id, allocated_amount, distributed_amount)
VALUES (100004, 100015, 40800, 40800),
       (100004,100016, 40800, 40800),
       (100005, 100015, 40800, 40800),
       (100006, 100015, 120000, 120000);

INSERT INTO employee_rewards (employee_id, department_reward_id, hours_worked, hours_worked_reward, additional_reward, penalty)
VALUES (100011, 100019, 150.75, 12060, 0, 0),
       (100012, 100019, 150.75, 10710, 0, 0),
       (100013, 100019, 150.75, 18030, 0, 0),
       (100011, 100020, 176.50, 12060, 0, 0),
       (100014, 100021, 114.50, 10000, 0, 0);