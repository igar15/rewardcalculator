package ru.javaprojects.rewardcalculator.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import ru.javaprojects.rewardcalculator.model.Position;
import ru.javaprojects.rewardcalculator.util.exception.NotFoundException;

import javax.validation.ConstraintViolationException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.javaprojects.rewardcalculator.PositionTestData.*;

class PositionServiceTest extends AbstractServiceTest {

    @Autowired
    private PositionService service;

    @Test
    void create() {
        Position created = service.create(getNew());
        int newId = created.id();
        Position newPosition = getNew();
        newPosition.setId(newId);
        POSITION_MATCHER.assertMatch(created, newPosition);
        POSITION_MATCHER.assertMatch(service.get(newId), newPosition);
    }

    @Test
    void duplicateNameCreate() {
        assertThrows(DataAccessException.class, () -> service.create(new Position(null, position1.getName(), 25000)));
    }

    @Test
    void get() {
        Position position = service.get(POSITION_1_ID);
        POSITION_MATCHER.assertMatch(position, position1);
    }

    @Test
    void getNotFound() {
        assertThrows(NotFoundException.class, () -> service.get(NOT_FOUND));
    }

    @Test
    void getAll() {
        List<Position> positions = service.getAll();
        POSITION_MATCHER.assertMatch(positions, position1, position2, position3);
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
        service.update(getUpdated());
        POSITION_MATCHER.assertMatch(service.get(POSITION_1_ID), getUpdated());
    }

    @Test
    void updateNotFound() {
        Position updated = getUpdated();
        updated.setId(NOT_FOUND);
        assertThrows(NotFoundException.class, () -> service.update(updated));
    }

    @Test
    void createWithException() {
        validateRootCause(ConstraintViolationException.class, () -> service.create(new Position(null, " ", 40000)));
        validateRootCause(ConstraintViolationException.class, () -> service.create(new Position(null, "Po", 40000)));
        validateRootCause(ConstraintViolationException.class, () -> service.create(new Position(null, "Position name", null)));
        validateRootCause(ConstraintViolationException.class, () -> service.create(new Position(null, "Position name", 9999)));
    }
}