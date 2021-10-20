package ru.javaprojects.rewardcalculator.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.javaprojects.rewardcalculator.model.Department;
import ru.javaprojects.rewardcalculator.service.DepartmentService;
import ru.javaprojects.rewardcalculator.util.exception.NotFoundException;
import ru.javaprojects.rewardcalculator.web.json.JsonUtil;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.javaprojects.rewardcalculator.TestUtil.readFromJson;
import static ru.javaprojects.rewardcalculator.testdata.DepartmentTestData.*;
import static ru.javaprojects.rewardcalculator.util.exception.ErrorType.DATA_NOT_FOUND;
import static ru.javaprojects.rewardcalculator.util.exception.ErrorType.VALIDATION_ERROR;
import static ru.javaprojects.rewardcalculator.web.AppExceptionHandler.EXCEPTION_DEPARTMENT_POSITION_HAS_EMPLOYEES;
import static ru.javaprojects.rewardcalculator.web.AppExceptionHandler.EXCEPTION_DUPLICATE_DEPARTMENT;

class DepartmentRestControllerTest extends AbstractControllerTest {
    private static final String REST_URL = DepartmentRestController.REST_URL + '/';

    @Autowired
    private DepartmentService departmentService;

    @Test
    void getAll() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(DEPARTMENT_MATCHER.contentJson(department1, department3, department2));
    }

    @Test
    void getAllByPage() throws Exception {
        ResultActions action = perform(MockMvcRequestBuilders.get(REST_URL + "byPage")
                .param("page", "0")
                .param("size", "2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        List<Department> departments = JsonUtil.readContentFromPage(action.andReturn().getResponse().getContentAsString(), Department.class);
        DEPARTMENT_MATCHER.assertMatch(departments, department1, department3);
    }

    @Test
    void get() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + DEPARTMENT_1_ID))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(DEPARTMENT_MATCHER.contentJson(department1));
    }

    @Test
    void getNotFound() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + NOT_FOUND))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(DATA_NOT_FOUND));
    }

    @Test
    void delete() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + DEPARTMENT_2_ID))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertThrows(NotFoundException.class, () -> departmentService.get(DEPARTMENT_2_ID));
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void deleteWhenPositionHasEmployees() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + DEPARTMENT_1_ID))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR))
                .andExpect(detailMessage(EXCEPTION_DEPARTMENT_POSITION_HAS_EMPLOYEES));
    }

    @Test
    void deleteNotFound() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + NOT_FOUND))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(DATA_NOT_FOUND));
    }

    @Test
    void createWithLocation() throws Exception {
        Department newDepartment = getNew();
        ResultActions action = perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newDepartment)))
                .andExpect(status().isCreated());

        Department created = readFromJson(action, Department.class);
        int newId = created.id();
        newDepartment.setId(newId);
        DEPARTMENT_MATCHER.assertMatch(created, newDepartment);
        DEPARTMENT_MATCHER.assertMatch(departmentService.get(newId), newDepartment);
    }


    @Test
    void update() throws Exception {
        Department updated = getUpdated();
        perform(MockMvcRequestBuilders.put(REST_URL + DEPARTMENT_1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updated)))
                .andExpect(status().isNoContent());

        DEPARTMENT_MATCHER.assertMatch(departmentService.get(DEPARTMENT_1_ID), updated);
    }

    @Test
    void updateIdNotConsistent() throws Exception {
        Department updated = getUpdated();
        updated.setId(DEPARTMENT_2_ID);
        perform(MockMvcRequestBuilders.put(REST_URL + DEPARTMENT_1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updated)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR));
    }

    @Test
    void updateNotFound() throws Exception {
        Department updated = getUpdated();
        updated.setId(null);
        perform(MockMvcRequestBuilders.put(REST_URL + NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updated)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(DATA_NOT_FOUND));
    }

    @Test
    void createInvalid() throws Exception {
        Department newDepartment = getNew();
        newDepartment.setName(" ");
        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newDepartment)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR));
    }

    @Test
    void updateInvalid() throws Exception {
        Department updated = getUpdated();
        updated.setName(" ");
        perform(MockMvcRequestBuilders.put(REST_URL + DEPARTMENT_1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updated)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR));
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void createDuplicateName() throws Exception {
        Department newDepartment = new Department(null, department1.getName());
        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newDepartment)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR))
                .andExpect(detailMessage(EXCEPTION_DUPLICATE_DEPARTMENT));
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void updateDuplicateName() throws Exception {
        Department updated = new Department(DEPARTMENT_1_ID, department2.getName());
        perform(MockMvcRequestBuilders.put(REST_URL + DEPARTMENT_1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updated)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR))
                .andExpect(detailMessage(EXCEPTION_DUPLICATE_DEPARTMENT));
    }
}