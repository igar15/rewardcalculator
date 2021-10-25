package ru.javaprojects.rewardcalculator.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import ru.javaprojects.rewardcalculator.model.Position;
import ru.javaprojects.rewardcalculator.to.PositionTo;
import ru.javaprojects.rewardcalculator.util.exception.NotFoundException;

import javax.validation.ConstraintViolationException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.javaprojects.rewardcalculator.testdata.DepartmentTestData.*;
import static ru.javaprojects.rewardcalculator.testdata.EmployeeTestData.NOT_FOUND;
import static ru.javaprojects.rewardcalculator.testdata.PositionTestData.getNew;
import static ru.javaprojects.rewardcalculator.testdata.PositionTestData.getUpdated;
import static ru.javaprojects.rewardcalculator.testdata.PositionTestData.*;

class PositionServiceTest extends AbstractServiceTest {

    @Autowired
    private PositionService service;

    @Test
    void create() {
        Position created = service.create(getNewTo());
        int newId = created.id();
        Position newPosition = getNew();
        newPosition.setId(newId);
        POSITION_MATCHER.assertMatch(created, newPosition);
        POSITION_MATCHER.assertMatch(service.get(newId), newPosition);
    }

    @Test
    void duplicateNameCreate() {
        assertThrows(DataAccessException.class, () -> service.create(new PositionTo(null, position1.getName(), 25000, DEPARTMENT_1_ID)));
    }

    @Test
    void get() {
        Position position = service.get(POSITION_1_ID);
        POSITION_MATCHER.assertMatch(position, position1);
    }

    @Test
    void getWithDepartment() {
        Position position = service.getWithDepartment(POSITION_1_ID);
        POSITION_MATCHER.assertMatch(position, position1);
        DEPARTMENT_MATCHER.assertMatch(position.getDepartment(), department1);
    }

    @Test
    void getNotFound() {
        assertThrows(NotFoundException.class, () -> service.get(NOT_FOUND));
    }

    @Test
    void getAllByDepartmentId() {
        List<Position> positions = service.getAllByDepartmentId(DEPARTMENT_1_ID);
        POSITION_MATCHER.assertMatch(positions, position1, position2, position3);
    }

    @Test
    void getAllByDepartmentIdWithNotExistedDepartment() {
        assertThrows(NotFoundException.class, () -> service.getAllByDepartmentId(NOT_FOUND));
    }

    @Test
    void delete() {
        service.delete(POSITION_3_ID);
        assertThrows(NotFoundException.class, () -> service.get(POSITION_3_ID));
    }

    @Test
    void deleteWhenPositionHasEmployees() {
        assertThrows(DataAccessException.class, () -> service.delete(POSITION_1_ID));
    }

    @Test
    void deleteNotFound() {
        assertThrows(NotFoundException.class, () -> service.delete(NOT_FOUND));
    }

    @Test
    void update() {
        service.update(getUpdatedTo());
        POSITION_MATCHER.assertMatch(service.get(POSITION_1_ID), getUpdated());
    }

    @Test
    void updateNotFound() {
        PositionTo updated = getUpdatedTo();
        updated.setId(NOT_FOUND);
        assertThrows(NotFoundException.class, () -> service.update(updated));
    }

    @Test
    void createWithException() {
        validateRootCause(ConstraintViolationException.class, () -> service.create(new PositionTo(null, " ", 40000, DEPARTMENT_1_ID)));
        validateRootCause(ConstraintViolationException.class, () -> service.create(new PositionTo(null, "Po", 40000, DEPARTMENT_1_ID)));
        validateRootCause(ConstraintViolationException.class, () -> service.create(new PositionTo(null, "Position name", null, DEPARTMENT_1_ID)));
        validateRootCause(ConstraintViolationException.class, () -> service.create(new PositionTo(null, "Position name", 9999, DEPARTMENT_1_ID)));
    }
}