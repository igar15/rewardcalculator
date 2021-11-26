package ru.javaprojects.rewardcalculator.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.javaprojects.rewardcalculator.model.Employee;
import ru.javaprojects.rewardcalculator.to.EmployeeTo;
import ru.javaprojects.rewardcalculator.util.exception.NotFoundException;

import javax.validation.ConstraintViolationException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.javaprojects.rewardcalculator.model.Rate.FULL_RATE;
import static ru.javaprojects.rewardcalculator.testdata.DepartmentTestData.*;
import static ru.javaprojects.rewardcalculator.testdata.EmployeeTestData.NOT_FOUND;
import static ru.javaprojects.rewardcalculator.testdata.EmployeeTestData.getNew;
import static ru.javaprojects.rewardcalculator.testdata.EmployeeTestData.getNewTo;
import static ru.javaprojects.rewardcalculator.testdata.EmployeeTestData.getUpdated;
import static ru.javaprojects.rewardcalculator.testdata.EmployeeTestData.getUpdatedTo;
import static ru.javaprojects.rewardcalculator.testdata.EmployeeTestData.*;
import static ru.javaprojects.rewardcalculator.testdata.PositionTestData.*;

class EmployeeServiceTest extends AbstractServiceTest {

    @Autowired
    private EmployeeService service;

    @Test
    void create() {
        Employee created = service.create(getNewTo());
        int newId = created.id();
        Employee newEmployee = getNew();
        newEmployee.setId(newId);
        EMPLOYEE_MATCHER.assertMatch(created, newEmployee);
        EMPLOYEE_MATCHER.assertMatch(service.get(newId), newEmployee);
    }

    @Test
    void createWithNotExistedPosition() {
        EmployeeTo newEmployeeTo = getNewTo();
        newEmployeeTo.setPositionId(NOT_FOUND);
        assertThrows(NotFoundException.class, () -> service.create(newEmployeeTo));
    }

    @Test
    void get() {
        Employee employee = service.get(EMPLOYEE_1_ID);
        EMPLOYEE_MATCHER.assertMatch(employee, employee1);
    }

    @Test
    void getWithPositionDepartment() {
        Employee employee = service.getWithPositionDepartment(EMPLOYEE_1_ID);
        EMPLOYEE_MATCHER.assertMatch(employee, employee1);
        POSITION_MATCHER.assertMatch(employee.getPosition(), position1);
        DEPARTMENT_MATCHER.assertMatch(employee.getPosition().getDepartment(), department1);
    }

    @Test
    void getNotFound() {
        assertThrows(NotFoundException.class, () -> service.get(NOT_FOUND));
    }

    @Test
    void getAllNotFiredByDepartmentId() {
        List<Employee> employees = service.getAllNotFiredByDepartmentId(DEPARTMENT_1_ID);
        EMPLOYEE_MATCHER.assertMatch(employees, employee1, employee2, employee3);
    }

    @Test
    void getAllNotFiredByDepartmentIdWithNotExistedDepartment() {
        assertThrows(NotFoundException.class, () -> service.getAllNotFiredByDepartmentId(NOT_FOUND));
    }

    @Test
    void getAllFiredByDepartmentId() {
        List<Employee> employees = service.getAllFiredByDepartmentId(DEPARTMENT_1_ID);
        EMPLOYEE_MATCHER.assertMatch(employees, firedEmployee1, firedEmployee2, firedEmployee3);
    }

    @Test
    void getAllFiredByDepartmentIdWithNotExistedDepartment() {
        assertThrows(NotFoundException.class, () -> service.getAllFiredByDepartmentId(NOT_FOUND));
    }

    @Test
    void delete() {
        service.delete(EMPLOYEE_1_ID);
        assertThrows(NotFoundException.class, () -> service.get(EMPLOYEE_1_ID));
    }

    @Test
    void deleteNotFound() {
        assertThrows(NotFoundException.class, () -> service.delete(NOT_FOUND));
    }

    @Test
    void update() {
        service.update(getUpdatedTo());
        EMPLOYEE_MATCHER.assertMatch(service.get(EMPLOYEE_1_ID), getUpdated());
    }

    @Test
    void updateWithPositionChanging() {
        EmployeeTo updatedTo = getUpdatedTo();
        updatedTo.setPositionId(POSITION_3_ID);
        service.update(updatedTo);
        Employee employee = service.getWithPositionDepartment(EMPLOYEE_1_ID);
        EMPLOYEE_MATCHER.assertMatch(employee, getUpdated());
        POSITION_MATCHER.assertMatch(employee.getPosition(), position3);
    }

    @Test
    void updateNotFound() {
        EmployeeTo updatedTo = getUpdatedTo();
        updatedTo.setId(NOT_FOUND);
        assertThrows(NotFoundException.class, () -> service.update(updatedTo));
    }

    @Test
    void updateWithNotExistedPosition() {
        EmployeeTo updatedTo = getUpdatedTo();
        updatedTo.setPositionId(NOT_FOUND);
        assertThrows(NotFoundException.class, () -> service.update(updatedTo));
    }

    @Test
    void changeWorkingStatus() {
        service.changeWorkingStatus(EMPLOYEE_1_ID, true);
        assertTrue(service.get(EMPLOYEE_1_ID).isFired());
        service.changeWorkingStatus(EMPLOYEE_1_ID, false);
        assertFalse(service.get(EMPLOYEE_1_ID).isFired());
    }

    @Test
    void changeWorkingStatusNotFound() {
        assertThrows(NotFoundException.class, () -> service.changeWorkingStatus(NOT_FOUND, true));
    }

    @Test
    void createWithException() {
        validateRootCause(ConstraintViolationException.class, () -> service.create(new EmployeeTo(null, " ", FULL_RATE, POSITION_1_ID)));
        validateRootCause(ConstraintViolationException.class, () -> service.create(new EmployeeTo(null, "employee name", null, POSITION_1_ID)));
    }
}