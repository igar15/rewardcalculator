package ru.javaprojects.rewardcalculator.web.controller;

import org.hibernate.Hibernate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.javaprojects.rewardcalculator.model.Department;
import ru.javaprojects.rewardcalculator.model.Position;
import ru.javaprojects.rewardcalculator.repository.PositionRepository;
import ru.javaprojects.rewardcalculator.service.PositionService;
import ru.javaprojects.rewardcalculator.to.PositionTo;
import ru.javaprojects.rewardcalculator.util.exception.NotFoundException;
import ru.javaprojects.rewardcalculator.web.json.JsonUtil;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.javaprojects.rewardcalculator.testdata.DepartmentRewardTestData.NOT_FOUND;
import static ru.javaprojects.rewardcalculator.testdata.DepartmentTestData.*;
import static ru.javaprojects.rewardcalculator.testdata.PositionTestData.getNew;
import static ru.javaprojects.rewardcalculator.testdata.PositionTestData.getUpdated;
import static ru.javaprojects.rewardcalculator.testdata.PositionTestData.*;
import static ru.javaprojects.rewardcalculator.TestUtil.readFromJson;
import static ru.javaprojects.rewardcalculator.util.exception.ErrorType.DATA_NOT_FOUND;
import static ru.javaprojects.rewardcalculator.util.exception.ErrorType.VALIDATION_ERROR;
import static ru.javaprojects.rewardcalculator.web.AppExceptionHandler.EXCEPTION_DEPARTMENT_POSITION_HAS_EMPLOYEES;
import static ru.javaprojects.rewardcalculator.web.AppExceptionHandler.EXCEPTION_DUPLICATE_POSITION;

class PositionRestControllerTest extends AbstractControllerTest {
    private static final String REST_URL = PositionRestController.REST_URL + '/';

    @Autowired
    private PositionService service;

    @Autowired
    private PositionRepository repository;

    @Test
    void getAll() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "departments/" + DEPARTMENT_1_ID + "/positions"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(POSITION_MATCHER.contentJson(position1, position2, position3));
    }

    @Test
    void getAllWithNotExistedDepartment() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "departments/" + NOT_FOUND + "/positions"))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(DATA_NOT_FOUND));
    }

    @Test
    void get() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "positions/" + POSITION_1_ID))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(POSITION_MATCHER.contentJson(position1));
    }

    @Test
    void getNotFound() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "positions/" + NOT_FOUND))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(DATA_NOT_FOUND));
    }

    @Test
    void delete() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + "positions/" + POSITION_3_ID))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertThrows(NotFoundException.class, () -> service.get(POSITION_3_ID));
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void deleteWhenPositionHasEmployees() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + "positions/" + POSITION_1_ID))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR))
                .andExpect(detailMessage(EXCEPTION_DEPARTMENT_POSITION_HAS_EMPLOYEES));
    }

    @Test
    void deleteNotFound() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + "positions/" + NOT_FOUND))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(DATA_NOT_FOUND));
    }

    @Test
    void createWithLocation() throws Exception {
        PositionTo newPositionTo = getNewTo();
        ResultActions action = perform(MockMvcRequestBuilders.post(REST_URL + "/positions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newPositionTo)))
                .andExpect(status().isCreated());

        Position created = readFromJson(action, Position.class);
        int newId = created.id();
        Position newPosition = getNew();
        newPosition.setId(newId);
        POSITION_MATCHER.assertMatch(created, newPosition);
        POSITION_MATCHER.assertMatch(service.get(newId), newPosition);
    }

    @Test
    void createWithNotExistedDepartment() throws Exception {
        PositionTo newPositionTo = getNewTo();
        newPositionTo.setDepartmentId(NOT_FOUND);
        perform(MockMvcRequestBuilders.post(REST_URL + "/positions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newPositionTo)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(DATA_NOT_FOUND));
    }

    @Test
    void update() throws Exception {
        PositionTo updatedTo = getUpdatedTo();
        perform(MockMvcRequestBuilders.put(REST_URL + "positions/" + POSITION_1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedTo)))
                .andExpect(status().isNoContent());

        POSITION_MATCHER.assertMatch(service.get(POSITION_1_ID), getUpdated());
    }

    // Department does not change on update. So we can pass any department id, even not existing.
    @Test
    void updateWithNotExistedDepartment() throws Exception {
        PositionTo updatedTo = getUpdatedTo();
        updatedTo.setDepartmentId(NOT_FOUND);
        perform(MockMvcRequestBuilders.put(REST_URL + "positions/" + POSITION_1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedTo)))
                .andExpect(status().isNoContent());

        Position position = repository.findByIdWithDepartment(POSITION_1_ID);
        POSITION_MATCHER.assertMatch(position, getUpdated());
        DEPARTMENT_MATCHER.assertMatch(Hibernate.unproxy(position.getDepartment(), Department.class), department1);
    }

    // Department does not change on update. So we can pass any department id, even not existing.
    @Test
    void updateWithTryingToChangeDepartment() throws Exception {
        PositionTo updatedTo = getUpdatedTo();
        updatedTo.setDepartmentId(DEPARTMENT_2_ID);
        perform(MockMvcRequestBuilders.put(REST_URL + "positions/" + POSITION_1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedTo)))
                .andExpect(status().isNoContent());

        Position position = repository.findByIdWithDepartment(POSITION_1_ID);
        POSITION_MATCHER.assertMatch(position, getUpdated());
        DEPARTMENT_MATCHER.assertMatch(Hibernate.unproxy(position.getDepartment(), Department.class), department1);
    }

    @Test
    void updateNotFound() throws Exception {
        PositionTo updatedTo = getUpdatedTo();
        updatedTo.setId(null);
        perform(MockMvcRequestBuilders.put(REST_URL + "positions/" + NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedTo)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(DATA_NOT_FOUND));
    }

    @Test
    void updateIdNotConsistent() throws Exception {
        PositionTo updatedTo = getUpdatedTo();
        updatedTo.setId(POSITION_2_ID);
        perform(MockMvcRequestBuilders.put(REST_URL + "positions/" + POSITION_1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedTo)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR));
    }

    @Test
    void createInvalid() throws Exception {
        PositionTo newPositionTo = getNewTo();
        newPositionTo.setName(" ");
        perform(MockMvcRequestBuilders.post(REST_URL + "/positions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newPositionTo)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR));
    }

    @Test
    void updateInvalid() throws Exception {
        PositionTo updatedTo = getUpdatedTo();
        updatedTo.setSalary(null);
        perform(MockMvcRequestBuilders.put(REST_URL + "/positions/" + POSITION_1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedTo)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR));
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void createDuplicateNameInDepartment() throws Exception {
        PositionTo newPositionTo = new PositionTo(null, position1.getName(), 20000, DEPARTMENT_1_ID);
        perform(MockMvcRequestBuilders.post(REST_URL + "/positions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newPositionTo)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR))
                .andExpect(detailMessage(EXCEPTION_DUPLICATE_POSITION));
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void updateDuplicateNameInDepartment() throws Exception {
        PositionTo updatedTo = new PositionTo(POSITION_2_ID, position1.getName(), 20000, DEPARTMENT_2_ID);
        perform(MockMvcRequestBuilders.put(REST_URL + "/positions/" + POSITION_2_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedTo)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR))
                .andExpect(detailMessage(EXCEPTION_DUPLICATE_POSITION));
    }
}