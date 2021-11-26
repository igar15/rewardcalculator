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
VALUES ('John Smith', 'johnsmith@gmail.com', '{noop}password'),
       ('Viktor Wran', 'admin@gmail.com', '{noop}admin'),
       ('Sarah Connor', 'sarahconnor@gmail.com', '{noop}password'),
       ('Jack London', 'jacklondon@gmail.com', '{noop}password');

INSERT INTO user_roles(role, user_id)
VALUES ('DEPARTMENT_HEAD', 100000),
       ('ADMIN', 100001),
       ('DEPARTMENT_HEAD', 100001),
       ('ECONOMIST', 100001),
       ('ECONOMIST', 100002),
       ('PERSONNEL_OFFICER', 100003);

INSERT INTO departments (name)
VALUES ('Department # 1'),
       ('Department # 3'),
       ('Department # 2');

INSERT INTO user_managed_departments (user_id, department_id)
VALUES (100000, 100004),
       (100000, 100005);

INSERT INTO positions (name, salary, chief_position, department_id)
VALUES ('Web designer', 40200, false, 100004),
       ('Programmer', 35700, true, 100004),
       ('Business analyst', 60100, false, 100004),
       ('Product manager', 25000, false, 100006);

INSERT INTO employees (name, rate, fired, position_id)
VALUES ('John Doe', 'FULL_RATE', false, 100007),
       ('Jessica Parker', 'FULL_RATE', false, 100007),
       ('Bob Smith', 'FULL_RATE', false, 100008),
       ('Alan Wake', 'FULL_RATE', false, 100010);

INSERT INTO payment_periods (period, required_hours_worked)
VALUES ('2021-01-01', 120),
       ('2021-02-01', 150.75),
       ('2021-03-01', 176.50);

INSERT INTO department_rewards (department_id, payment_period_id, allocated_amount, distributed_amount)
VALUES (100004, 100015, 40800, 12060),
       (100004,100016, 40800, 34830),
       (100006, 100016, 40800, 5696),
       (100006, 100015, 120000, 7156);

INSERT INTO employee_rewards (employee_id, department_reward_id, hours_worked, hours_worked_reward, additional_reward, penalty)
VALUES (100011, 100019, 150.75, 12060, 0, 0),
       (100012, 100019, 150.75, 12060, 0, 0),
       (100013, 100019, 150.75, 10710, 0, 0),
       (100011, 100018, 176.50, 12060, 0, 0),
       (100014, 100021, 114.50, 7156, 0, 0),
       (100014, 100020, 114.50, 5696, 0, 0),
       (100012, 100018, 0, 0, 0, 0),
       (100013, 100018, 0, 0, 0, 0);

INSERT INTO employees (name, rate, fired, position_id)
VALUES ('Jack Robinson', 'FULL_RATE', true, 100007),
       ('Kristina Lee', 'FULL_RATE', true, 100007),
       ('Mike Douglas', 'FULL_RATE', true, 100008),
       ('Kate Jackson', 'FULL_RATE', true, 100010);
