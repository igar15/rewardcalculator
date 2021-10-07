DROP TABLE IF EXISTS user_roles;
DROP TABLE IF EXISTS user_managed_departments;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS department_rewards;
DROP TABLE IF EXISTS employee_rewards;
DROP TABLE IF EXISTS payment_periods;
DROP TABLE IF EXISTS employees;
DROP TABLE IF EXISTS positions;
DROP TABLE IF EXISTS departments;
DROP SEQUENCE IF EXISTS global_seq;

CREATE SEQUENCE global_seq START WITH 100000;

CREATE TABLE users
(
    id         INTEGER PRIMARY KEY DEFAULT nextval('global_seq'),
    name       VARCHAR                           NOT NULL,
    email      VARCHAR                           NOT NULL,
    password   VARCHAR                           NOT NULL,
    enabled    BOOL                DEFAULT TRUE  NOT NULL,
    registered TIMESTAMP           DEFAULT now() NOT NULL
);
CREATE UNIQUE INDEX users_unique_email_idx ON users (email);

CREATE TABLE user_roles
(
    user_id INTEGER NOT NULL,
    role    VARCHAR,
    CONSTRAINT user_roles_unique_idx UNIQUE (user_id, role),
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE departments
(
    id   INTEGER PRIMARY KEY DEFAULT nextval('global_seq'),
    name VARCHAR NOT NULL
);
CREATE UNIQUE INDEX departments_unique_name_idx ON departments (name);

CREATE TABLE user_managed_departments
(
    user_id       INTEGER NOT NULL,
    department_id INTEGER NOT NULL,
    CONSTRAINT user_department_unique_idx UNIQUE (user_id, department_id),
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (department_id) REFERENCES departments (id) ON DELETE CASCADE
);

CREATE TABLE positions
(
    id     INTEGER PRIMARY KEY DEFAULT nextval('global_seq'),
    name   VARCHAR NOT NULL,
    salary INTEGER NOT NULL
);
CREATE UNIQUE INDEX positions_unique_name_idx ON positions (name);

CREATE TABLE employees
(
    id            INTEGER PRIMARY KEY DEFAULT nextval('global_seq'),
    name          VARCHAR NOT NULL,
    department_id INTEGER NOT NULL,
    position_id   INTEGER NOT NULL,
    FOREIGN KEY (department_id) REFERENCES departments (id),
    FOREIGN KEY (position_id) REFERENCES positions (id)
);

CREATE TABLE payment_periods
(
    id                    INTEGER PRIMARY KEY DEFAULT nextval('global_seq'),
    period                DATE NOT NULL,
    required_hours_worked NUMERIC NOT NULL
);
CREATE UNIQUE INDEX payment_periods_unique_period_idx ON payment_periods (period);

CREATE TABLE department_rewards
(
    id                 INTEGER PRIMARY KEY DEFAULT nextval('global_seq'),
    department_id      INTEGER NOT NULL,
    payment_period_id  INTEGER NOT NULL,
    allocated_amount   INTEGER NOT NULL,
    distributed_amount INTEGER DEFAULT 0 NOT NULL CHECK (distributed_amount <= allocated_amount),
    FOREIGN KEY (department_id) REFERENCES departments (id) ON DELETE CASCADE,
    FOREIGN KEY (payment_period_id) REFERENCES payment_periods (id) ON DELETE CASCADE
);
CREATE UNIQUE INDEX department_rewards_unique_department_id_payment_period_id_idx ON department_rewards (department_id, payment_period_id);

CREATE TABLE employee_rewards
(
    id                  INTEGER PRIMARY KEY DEFAULT nextval('global_seq'),
    employee_id         INTEGER NOT NULL,
    payment_period_id   INTEGER NOT NULL,
    hours_worked        NUMERIC NOT NULL,
    hours_worked_reward INTEGER NOT NULL,
    additional_reward   INTEGER NOT NULL,
    penalty             INTEGER NOT NULL,
    FOREIGN KEY (employee_id) REFERENCES employees (id) ON DELETE CASCADE,
    FOREIGN KEY (payment_period_id) REFERENCES payment_periods (id) ON DELETE CASCADE
);
CREATE UNIQUE INDEX employee_rewards_unique_department_id_payment_period_id_idx ON employee_rewards (employee_id, payment_period_id);