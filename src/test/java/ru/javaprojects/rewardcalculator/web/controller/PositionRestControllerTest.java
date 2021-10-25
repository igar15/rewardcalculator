package ru.javaprojects.rewardcalculator.web.controller;

import org.hibernate.Hibernate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.javaprojects.rewardcalculator.model.Department;
import ru.javaprojects.rewardcalculator.model.Position;
import ru.javaprojects.rewardcalculator.service.PositionService;
import ru.javaprojects.rewardcalculator.to.PositionTo;
import ru.javaprojects.rewardcalculator.util.exception.NotFoundException;
import ru.javaprojects.rewardcalculator.web.json.JsonUtil;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.javaprojects.rewardcalculator.TestUtil.readFromJson;
import static ru.javaprojects.rewardcalculator.testdata.DepartmentRewardTestData.NOT_FOUND;
import static ru.javaprojects.rewardcalculator.testdata.DepartmentTestData.*;
import static ru.javaprojects.rewardcalculator.testdata.PositionTestData.getNew;
import static ru.javaprojects.rewardcalculator.testdata.PositionTestData.getNewTo;
import static ru.javaprojects.rewardcalculator.testdata.PositionTestData.getUpdated;
import static ru.javaprojects.rewardcalculator.testdata.PositionTestData.getUpdatedTo;
import static ru.javaprojects.rewardcalculator.testdata.PositionTestData.*;
import static ru.javaprojects.rewardcalculator.testdata.UserTestData.*;
import static ru.javaprojects.rewardcalculator.util.exception.ErrorType.*;
import static ru.javaprojects.rewardcalculator.web.AppExceptionHandler.EXCEPTION_DEPARTMENT_POSITION_HAS_EMPLOYEES;
import static ru.javaprojects.rewardcalculator.web.AppExceptionHandler.EXCEPTION_DUPLICATE_POSITION;

class PositionRestControllerTest extends AbstractControllerTest {
    private static final String REST_URL = PositionRestController.REST_URL + '/';

    @Autowired
    private PositionService service;

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void getAllWhenAdmin() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "departments/" + DEPARTMENT_1_ID + "/positions"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(POSITION_MATCHER.contentJson(position1, position2, position3));
    }

    @Test
    @WithUserDetails(value = ECONOMIST_MAIL)
    void getAllWhenEconomist() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "departments/" + DEPARTMENT_1_ID + "/positions"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(POSITION_MATCHER.contentJson(position1, position2, position3));
    }

    @Test
    @WithUserDetails(value = PERSONNEL_OFFICER_MAIL)
    void getAllWhenPersonnelOfficer() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "departments/" + DEPARTMENT_1_ID + "/positions"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(POSITION_MATCHER.contentJson(position1, position2, position3));
    }

    @Test
    @WithUserDetails(value = DEPARTMENT_HEAD_MAIL)
    void getAllWhenDepartmentHead() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "departments/" + DEPARTMENT_1_ID + "/positions"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(POSITION_MATCHER.contentJson(position1, position2, position3));
    }

    @Test
    @WithUserDetails(value = DEPARTMENT_HEAD_MAIL)
    void getAllForbiddenWhenDepartmentHead() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "departments/" + DEPARTMENT_3_ID + "/positions"))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(errorType(ACCESS_DENIED_ERROR));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void getAllWithNotExistedDepartment() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "departments/" + NOT_FOUND + "/positions"))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(DATA_NOT_FOUND));
    }

    @Test
    void getAllUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "departments/" + DEPARTMENT_1_ID + "/positions"))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(errorType(UNAUTHORIZED_ERROR));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void getWhenAdmin() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "positions/" + POSITION_1_ID))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(POSITION_MATCHER.contentJson(position1));
    }

    @Test
    @WithUserDetails(value = PERSONNEL_OFFICER_MAIL)
    void getWhenPersonnelOfficer() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "positions/" + POSITION_1_ID))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(POSITION_MATCHER.contentJson(position1));
    }

    @Test
    @WithUserDetails(value = ECONOMIST_MAIL)
    void getWhenEconomist() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "positions/" + POSITION_1_ID))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(POSITION_MATCHER.contentJson(position1));
    }

    @Test
    @WithUserDetails(value = DEPARTMENT_HEAD_MAIL)
    void getWhenDepartmentHead() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "positions/" + POSITION_1_ID))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(POSITION_MATCHER.contentJson(position1));
    }

    @Test
    @WithUserDetails(value = DEPARTMENT_HEAD_MAIL)
    void getForbiddenWhenDepartmentHead() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "positions/" + POSITION_ANOTHER_DEPARTMENT_ID))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(errorType(ACCESS_DENIED_ERROR));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void getNotFound() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "positions/" + NOT_FOUND))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(DATA_NOT_FOUND));
    }

    @Test
    void getUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "positions/" + POSITION_1_ID))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(errorType(UNAUTHORIZED_ERROR));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void deleteWhenAdmin() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + "positions/" + POSITION_3_ID))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertThrows(NotFoundException.class, () -> service.get(POSITION_3_ID));
    }

    @Test
    @WithUserDetails(value = PERSONNEL_OFFICER_MAIL)
    void deleteWhenPersonnelOfficer() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + "positions/" + POSITION_3_ID))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertThrows(NotFoundException.class, () -> service.get(POSITION_3_ID));
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    @WithUserDetails(value = ADMIN_MAIL)
    void deleteWhenPositionHasEmployees() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + "positions/" + POSITION_1_ID))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR))
                .andExpect(detailMessage(EXCEPTION_DEPARTMENT_POSITION_HAS_EMPLOYEES));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void deleteNotFound() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + "positions/" + NOT_FOUND))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(DATA_NOT_FOUND));
    }

    @Test
    void deleteUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + "positions/" + POSITION_1_ID))
                .andExpect(status().isUnauthorized())
                .andExpect(errorType(UNAUTHORIZED_ERROR));
    }

    @Test
    @WithUserDetails(value = ECONOMIST_MAIL)
    void deleteForbiddenWhenEconomist() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + "positions/" + POSITION_1_ID))
                .andExpect(status().isForbidden())
                .andExpect(errorType(ACCESS_DENIED_ERROR));
    }

    @Test
    @WithUserDetails(value = DEPARTMENT_HEAD_MAIL)
    void deleteForbiddenWhenDepartmentHead() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + "positions/" + POSITION_1_ID))
                .andExpect(status().isForbidden())
                .andExpect(errorType(ACCESS_DENIED_ERROR));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void createWithLocationWhenAdmin() throws Exception {
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
    @WithUserDetails(value = PERSONNEL_OFFICER_MAIL)
    void createWithLocationWhenPersonnelOfficer() throws Exception {
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
    @WithUserDetails(value = ADMIN_MAIL)
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
    void createUnAuth() throws Exception {
        PositionTo newPositionTo = getNewTo();
        perform(MockMvcRequestBuilders.post(REST_URL + "/positions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newPositionTo)))
                .andExpect(status().isUnauthorized())
                .andExpect(errorType(UNAUTHORIZED_ERROR));
    }

    @Test
    @WithUserDetails(value = ECONOMIST_MAIL)
    void createForbiddenWhenEconomist() throws Exception {
        PositionTo newPositionTo = getNewTo();
        perform(MockMvcRequestBuilders.post(REST_URL + "/positions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newPositionTo)))
                .andExpect(status().isForbidden())
                .andExpect(errorType(ACCESS_DENIED_ERROR));
    }

    @Test
    @WithUserDetails(value = DEPARTMENT_HEAD_MAIL)
    void createForbiddenWhenDepartmentHead() throws Exception {
        PositionTo newPositionTo = getNewTo();
        perform(MockMvcRequestBuilders.post(REST_URL + "/positions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newPositionTo)))
                .andExpect(status().isForbidden())
                .andExpect(errorType(ACCESS_DENIED_ERROR));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void updateWhenAdmin() throws Exception {
        PositionTo updatedTo = getUpdatedTo();
        perform(MockMvcRequestBuilders.put(REST_URL + "positions/" + POSITION_1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedTo)))
                .andExpect(status().isNoContent());

        POSITION_MATCHER.assertMatch(service.get(POSITION_1_ID), getUpdated());
    }

    @Test
    @WithUserDetails(value = PERSONNEL_OFFICER_MAIL)
    void updateWhenPersonnelOfficer() throws Exception {
        PositionTo updatedTo = getUpdatedTo();
        perform(MockMvcRequestBuilders.put(REST_URL + "positions/" + POSITION_1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedTo)))
                .andExpect(status().isNoContent());

        POSITION_MATCHER.assertMatch(service.get(POSITION_1_ID), getUpdated());
    }

    // Department does not change on update. So we can pass any department id, even not existing.
    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void updateWithNotExistedDepartment() throws Exception {
        PositionTo updatedTo = getUpdatedTo();
        updatedTo.setDepartmentId(NOT_FOUND);
        perform(MockMvcRequestBuilders.put(REST_URL + "positions/" + POSITION_1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedTo)))
                .andExpect(status().isNoContent());

        Position position = service.getWithDepartment(POSITION_1_ID);
        POSITION_MATCHER.assertMatch(position, getUpdated());
        DEPARTMENT_MATCHER.assertMatch(Hibernate.unproxy(position.getDepartment(), Department.class), department1);
    }

    // Department does not change on update. So we can pass any department id, even not existing.
    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void updateWithTryingToChangeDepartment() throws Exception {
        PositionTo updatedTo = getUpdatedTo();
        updatedTo.setDepartmentId(DEPARTMENT_2_ID);
        perform(MockMvcRequestBuilders.put(REST_URL + "positions/" + POSITION_1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedTo)))
                .andExpect(status().isNoContent());

        Position position = service.getWithDepartment(POSITION_1_ID);
        POSITION_MATCHER.assertMatch(position, getUpdated());
        DEPARTMENT_MATCHER.assertMatch(Hibernate.unproxy(position.getDepartment(), Department.class), department1);
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
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
    @WithUserDetails(value = ADMIN_MAIL)
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
    void updateUnAuth() throws Exception {
        PositionTo updatedTo = getUpdatedTo();
        perform(MockMvcRequestBuilders.put(REST_URL + "positions/" + POSITION_1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedTo)))
                .andExpect(status().isUnauthorized())
                .andExpect(errorType(UNAUTHORIZED_ERROR));
    }

    @Test
    @WithUserDetails(value = ECONOMIST_MAIL)
    void updateForbiddenWhenEconomist() throws Exception {
        PositionTo updatedTo = getUpdatedTo();
        perform(MockMvcRequestBuilders.put(REST_URL + "positions/" + POSITION_1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedTo)))
                .andExpect(status().isForbidden())
                .andExpect(errorType(ACCESS_DENIED_ERROR));
    }

    @Test
    @WithUserDetails(value = DEPARTMENT_HEAD_MAIL)
    void updateForbiddenWhenDepartmentHead() throws Exception {
        PositionTo updatedTo = getUpdatedTo();
        perform(MockMvcRequestBuilders.put(REST_URL + "positions/" + POSITION_1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedTo)))
                .andExpect(status().isForbidden())
                .andExpect(errorType(ACCESS_DENIED_ERROR));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
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
    @WithUserDetails(value = ADMIN_MAIL)
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
    @WithUserDetails(value = ADMIN_MAIL)
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
    @WithUserDetails(value = ADMIN_MAIL)
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