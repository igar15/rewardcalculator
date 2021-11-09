[![Codacy Badge](https://app.codacy.com/project/badge/Grade/911c756386094a2cb1035edef231969c)](https://www.codacy.com/gh/igar15/rewardcalculator/dashboard)
[![Build Status](https://app.travis-ci.com/igar15/rewardcalculator.svg?branch=master)](https://app.travis-ci.com/github/igar15/rewardcalculator)

Reward Calculating System project 
=================================

This is the REST API implementation of Reward Calculating System designed for calculating monthly reward of employees
of the department based on the reward allocated to the department, the number of hours worked by the employee, and his success in work.

### Technology stack used: 
* Maven
* Spring Boot 2
* Spring MVC
* Spring Data JPA (Hibernate)
* Spring Security
* REST (Jackson)
* JSON Web Token
* JUnit 5
* OpenAPI 3
* iText PDF

### Project key logic:
* System main purpose: calculating monthly reward of employees of the department based on the reward allocated
 to the department, the number of hours worked by the employee, and his success in work. Obtaining a reporting form for rewards in pdf format.  
 The system also allows to receive data of the payment periods of the company, departments, positions and employees of the department.
* There are 4 types of users: admin, personnel officer, economist and department head.
* Admins have full control over the system. They can receive, create, update and delete any data. But their main task is user management.
* Personnel officers can manage data about departments of the company, data about positions in departments, as well as data about employees.
They also have read-only access to payment periods data in the company.
* Economists can manage data of payments periods in the company, as well as data about department rewards.  
They also have read-only access to departments, employees and positions data in the company.  
* Department heads can manage data of employee rewards of their department.
They have read-only access to payment periods data in the company, as well as data about their department, their department's positions and employees.
* Every user has access to their profile data and can also change their password. 

###Application Domain Model Schema
![domain model](https://user-images.githubusercontent.com/60218699/139638775-d0d25436-ef09-4978-aa1e-4fcc59ca3031.png)

### API documentation:
#### Swagger documentation
- (/v3/api-docs)
- (/swagger-ui.html)
#### Profile
- GET /api/profile (get user profile)
- PATCH /api/profile/password?password={password} (change user profile password)
- POST /api/profile/login (login to application)
#### Users
- GET /api/users (get all users)
- GET /api/users/by?keyWord={keyWord} (get all users by keyWord (search by name and email contains the keyWord))
- GET /api/users/{userId} (get user with id = userId)
- POST /api/users (create a new user)
- PUT /api/users/{userId} (update user with id = userId)
- PATCH /api/users/{userId}?enabled={enabledValue} (change user's enabled status to enabledValue for user with id = userId)
- PATCH /api/users/{userId}/password?password={password} (change user's password to 'password' request param for user with id = userId)
- DELETE /api/users/{userId} (delete user with id = userId)
#### Payment Periods
- POST /api/paymentperiods (create a new payment period)
- GET /api/paymentperiods (get all payment periods)
- GET /api/paymentperiods/byPage?page={pageNumber}&size={sizeNumber} (get page of payment periods)
- GET /api/paymentperiods/{paymentPeriodId} (get payment period with id = paymentPeriodId)
- PUT /api/paymentperiods/{paymentPeriodId} (update payment period with id = paymentPeriodId)
- DELETE /api/paymentperiods/{paymentPeriodId} (delete payment period with id = paymentPeriodId)
#### Departments
- POST /api/departments (create a new department)
- GET /api/departments (get all departments)
- GET /api/departments/byPage?page={pageNumber}&size={sizeNumber} (get page of departments)
- GET /api/departments/{departmentId} (get department with id = departmentId)
- PUT /api/departments/{departmentId} (update department with id = departmentId)
- DELETE /api/departments/{departmentId} (delete department with id = departmentId)
#### Positions
- POST /api/positions (create a new position)
- GET /api/departments/{departmentId}/positions (get all positions of the department with id = departmentId)
- GET /api/positions/{positionId} (get position with id = positionId)
- PUT /api/positions/{positionId} (update position with id = positionId)
- DELETE /api/positions/{positionId} (delete position with id = positionId)
#### Employees
- POST /api/employees (create a new employee)
- GET /api/departments/{departmentId}/employees (get all employees of the department with id = departmentId)
- GET /api/employees/{employeeId} (get employee with id = employeeId)
- PUT /api/employees/{employeeId} (update employee with id = employeeId)
- DELETE /api/employees/{employeeId} (delete employee with id = employeeId)
#### Department Rewards
- POST /api/departmentrewards (create a new department reward)
- GET /api/departments/{departmentId}/departmentrewards (get all department rewards of the department with id = departmentId)
- GET /api/departments/{departmentId}/departmentrewards/byPage?page={pageNumber}&size={sizeNumber} (get page of department rewards of the department with id = departmentId)
- GET /api/departmentrewards/{departmentRewardId} (get department reward with id = departmentRewardId)
- PUT /api/departmentrewards/{departmentRewardId} (update department reward with id = departmentRewardId)
- DELETE /api/departmentrewards/{departmentRewardId} (delete department reward with id = departmentRewardId)
#### Employee Rewards
- GET /api/departmentrewards/{departmentRewardId}/employeerewards (get all employee rewards of the department reward with id = departmentRewardId)
- GET /api/departmentrewards/{departmentRewardId}/employeerewards/pdf?approvingPosition={approvingPosition}&approvingName={approvingName} (get all employee rewards of the department reward with id = departmentRewardId in pdf form. If approvingPosition and approvingName params passed also adds approving signature to the form)
- GET /api/employeerewards/{employeeRewardId} (get employee reward with id = employeeRewardId)
- PUT /api/employeerewards/{employeeRewardId} (update employee reward with id = employeeRewardId)

### Caching strategy
####Spring caching (Ehcache provider):
- Get user (multiNonExpiryCache, cache key = {email}, evicts all entries, when update/delete/enable/change_password of any user)  
  *This caching is designed to minimize the number of similar requests to the database to get the user by his email, 
  which occur during the verification of the JWT-token for each http request that requires authorization.*
- Get all payment periods (singleNonExpiryCache, evicts when create/update/delete any payment period)  
  *This caching is designed to minimize the number of similar queries to the database for a list of all payment periods. 
  This request, according to the business logic of the application, occurs quite often.*
- Get all departments (singleNonExpiryCache, evicts when create/update/delete any department)  
  *This caching is designed to minimize the number of similar queries to the database for a list of all departments. 
  This request, according to the business logic of the application, occurs quite often.*
- Get all positions of the department (multiNonExpiryCache, cache key = {departmentId}, evicts all entries, 
when update/delete any position, evicts entry by key when create a new position or when the department deletes)  
  *This caching is designed to minimize the number of similar queries to the database for a list of all positions of the selected department. 
  This request, according to the business logic of the application, occurs quite often.*
- Get all employees of the department (multiNonExpiryCache, cache key = {departmentId}, evicts all entries, when create/update/delete any employee, and when the department deletes)  
  *This caching is designed to minimize the number of similar queries to the database for a list of all employees of the selected department. 
  This request, according to the business logic of the application, occurs quite often.*
 
####Hibernate 2nd level cache:
- PaymentPeriod entity (CacheConcurrencyStrategy: NONSTRICT_READ_WRITE)
- Department entity (CacheConcurrencyStrategy: NONSTRICT_READ_WRITE)
- Position entity (CacheConcurrencyStrategy: NONSTRICT_READ_WRITE)
- Employee entity (CacheConcurrencyStrategy: NONSTRICT_READ_WRITE)
- DepartmentReward entity (CacheConcurrencyStrategy: NONSTRICT_READ_WRITE)