package ru.javaprojects.rewardcalculator.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import ru.javaprojects.rewardcalculator.model.Department;
import ru.javaprojects.rewardcalculator.util.exception.NotFoundException;

import javax.validation.ConstraintViolationException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.javaprojects.rewardcalculator.DepartmentTestData.*;

class DepartmentServiceTest extends AbstractServiceTest {

    @Autowired
    private DepartmentService service;

    @Test
    void create() {
        Department created = service.create(getNew());
        int newId = created.id();
        Department newDepartment = getNew();
        newDepartment.setId(newId);
        DEPARTMENT_MATCHER.assertMatch(created, newDepartment);
        DEPARTMENT_MATCHER.assertMatch(service.get(newId), newDepartment);
    }

    @Test
    void duplicateNameCreate() {
        assertThrows(DataAccessException.class, () -> service.create(new Department(null, department1.getName())));
    }

    @Test
    void get() {
        Department department = service.get(DEPARTMENT_1_ID);
        DEPARTMENT_MATCHER.assertMatch(department, department1);
    }

    @Test
    void getNotFound() {
        assertThrows(NotFoundException.class, () -> service.get(NOT_FOUND));
    }

    @Test
    void getAll() {
        List<Department> departments = service.getAll();
        DEPARTMENT_MATCHER.assertMatch(departments, department1, department3, department2);
    }

    @Test
    void delete() {
        service.delete(DEPARTMENT_2_ID);
        assertThrows(NotFoundException.class, () -> service.get(DEPARTMENT_2_ID));
    }

    @Test
    void deleteWhenDepartmentHasEmployees() {
        assertThrows(DataAccessException.class, () -> service.delete(DEPARTMENT_1_ID));
    }

    @Test
    void deleteNotFound() {
        assertThrows(NotFoundException.class, () -> service.delete(NOT_FOUND));
    }

    @Test
    void update() {
        service.update(getUpdated());
        DEPARTMENT_MATCHER.assertMatch(service.get(DEPARTMENT_1_ID), getUpdated());
    }

    @Test
    void updateNotFound() {
        Department updated = getUpdated();
        updated.setId(NOT_FOUND);
        assertThrows(NotFoundException.class, () -> service.update(updated));
    }

    @Test
    void createWithException() {
        validateRootCause(ConstraintViolationException.class, () -> service.create(new Department(null, " ")));
        validateRootCause(ConstraintViolationException.class, () -> service.create(new Department(null, "D")));
    }
}