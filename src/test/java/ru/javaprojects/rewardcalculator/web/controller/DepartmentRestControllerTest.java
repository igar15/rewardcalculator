package ru.javaprojects.rewardcalculator.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
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
import static ru.javaprojects.rewardcalculator.testdata.UserTestData.ADMIN_MAIL;
import static ru.javaprojects.rewardcalculator.testdata.UserTestData.DEPARTMENT_HEAD_MAIL;
import static ru.javaprojects.rewardcalculator.testdata.UserTestData.ECONOMIST_MAIL;
import static ru.javaprojects.rewardcalculator.testdata.UserTestData.PERSONNEL_OFFICER_MAIL;
import static ru.javaprojects.rewardcalculator.util.exception.ErrorType.*;
import static ru.javaprojects.rewardcalculator.web.AppExceptionHandler.EXCEPTION_DEPARTMENT_POSITION_HAS_EMPLOYEES;
import static ru.javaprojects.rewardcalculator.web.AppExceptionHandler.EXCEPTION_DUPLICATE_DEPARTMENT;

class DepartmentRestControllerTest extends AbstractControllerTest {
    private static final String REST_URL = DepartmentRestController.REST_URL + '/';

    @Autowired
    private DepartmentService departmentService;

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void getAllWhenAdmin() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(DEPARTMENT_MATCHER.contentJson(department1, department3, department2));
    }

    @Test
    @WithUserDetails(value = ECONOMIST_MAIL)
    void getAllWhenEconomist() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(DEPARTMENT_MATCHER.contentJson(department1, department3, department2));
    }

    @Test
    @WithUserDetails(value = PERSONNEL_OFFICER_MAIL)
    void getAllWhenPersonnelOfficer() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(DEPARTMENT_MATCHER.contentJson(department1, department3, department2));
    }

    @Test
    void getAllUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isUnauthorized())
                .andExpect(errorType(UNAUTHORIZED_ERROR));
    }

    @Test
    @WithUserDetails(value = DEPARTMENT_HEAD_MAIL)
    void getAllForbidden() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isForbidden())
                .andExpect(errorType(ACCESS_DENIED_ERROR));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void getAllByPageWhenAdmin() throws Exception {
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
    @WithUserDetails(value = ECONOMIST_MAIL)
    void getAllByPageWhenEconomist() throws Exception {
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
    @WithUserDetails(value = PERSONNEL_OFFICER_MAIL)
    void getAllByPageWhenPersonnelOfficer() throws Exception {
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
    void getAllByPageUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "byPage")
                .param("page", "0")
                .param("size", "2"))
                .andExpect(status().isUnauthorized())
                .andExpect(errorType(UNAUTHORIZED_ERROR));
    }

    @Test
    @WithUserDetails(value = DEPARTMENT_HEAD_MAIL)
    void getAllByPageForbidden() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "byPage")
                .param("page", "0")
                .param("size", "2"))
                .andExpect(status().isForbidden())
                .andExpect(errorType(ACCESS_DENIED_ERROR));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void getWhenAdmin() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + DEPARTMENT_1_ID))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(DEPARTMENT_MATCHER.contentJson(department1));
    }

    @Test
    @WithUserDetails(value = ECONOMIST_MAIL)
    void getWhenEconomist() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + DEPARTMENT_1_ID))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(DEPARTMENT_MATCHER.contentJson(department1));
    }

    @Test
    @WithUserDetails(value = PERSONNEL_OFFICER_MAIL)
    void getWhenPersonnelOfficer() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + DEPARTMENT_1_ID))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(DEPARTMENT_MATCHER.contentJson(department1));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void getNotFound() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + NOT_FOUND))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(DATA_NOT_FOUND));
    }

    @Test
    void getUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + DEPARTMENT_1_ID))
                .andExpect(status().isUnauthorized())
                .andExpect(errorType(UNAUTHORIZED_ERROR));
    }

    @Test
    @WithUserDetails(value = DEPARTMENT_HEAD_MAIL)
    void getForbidden() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + DEPARTMENT_1_ID))
                .andExpect(status().isForbidden())
                .andExpect(errorType(ACCESS_DENIED_ERROR));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void deleteWhenAdmin() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + DEPARTMENT_2_ID))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertThrows(NotFoundException.class, () -> departmentService.get(DEPARTMENT_2_ID));
    }

    @Test
    @WithUserDetails(value = PERSONNEL_OFFICER_MAIL)
    void deleteWhenPersonnelOfficer() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + DEPARTMENT_2_ID))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertThrows(NotFoundException.class, () -> departmentService.get(DEPARTMENT_2_ID));
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    @WithUserDetails(value = ADMIN_MAIL)
    void deleteWhenPositionHasEmployees() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + DEPARTMENT_1_ID))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR))
                .andExpect(detailMessage(EXCEPTION_DEPARTMENT_POSITION_HAS_EMPLOYEES));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void deleteNotFound() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + NOT_FOUND))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(DATA_NOT_FOUND));
    }

    @Test
    void deleteUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + DEPARTMENT_1_ID))
                .andExpect(status().isUnauthorized())
                .andExpect(errorType(UNAUTHORIZED_ERROR));
    }

    @Test
    @WithUserDetails(value = ECONOMIST_MAIL)
    void deleteForbiddenWhenEconomist() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + DEPARTMENT_1_ID))
                .andExpect(status().isForbidden())
                .andExpect(errorType(ACCESS_DENIED_ERROR));
    }

    @Test
    @WithUserDetails(value = DEPARTMENT_HEAD_MAIL)
    void deleteForbiddenWhenDepartmentHead() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + DEPARTMENT_1_ID))
                .andExpect(status().isForbidden())
                .andExpect(errorType(ACCESS_DENIED_ERROR));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void createWithLocationWhenAdmin() throws Exception {
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
    @WithUserDetails(value = PERSONNEL_OFFICER_MAIL)
    void createWithLocationWhenPersonnelOfficer() throws Exception {
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
    void createUnAuth() throws Exception {
        Department newDepartment = getNew();
        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newDepartment)))
                .andExpect(status().isUnauthorized())
                .andExpect(errorType(UNAUTHORIZED_ERROR));
    }

    @Test
    @WithUserDetails(value = ECONOMIST_MAIL)
    void createForbiddenWhenEconomist() throws Exception {
        Department newDepartment = getNew();
        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newDepartment)))
                .andExpect(status().isForbidden())
                .andExpect(errorType(ACCESS_DENIED_ERROR));
    }

    @Test
    @WithUserDetails(value = DEPARTMENT_HEAD_MAIL)
    void createForbiddenWhenDepartmentHead() throws Exception {
        Department newDepartment = getNew();
        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newDepartment)))
                .andExpect(status().isForbidden())
                .andExpect(errorType(ACCESS_DENIED_ERROR));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void updateWhenAdmin() throws Exception {
        Department updated = getUpdated();
        perform(MockMvcRequestBuilders.put(REST_URL + DEPARTMENT_1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updated)))
                .andExpect(status().isNoContent());

        DEPARTMENT_MATCHER.assertMatch(departmentService.get(DEPARTMENT_1_ID), updated);
    }

    @Test
    @WithUserDetails(value = PERSONNEL_OFFICER_MAIL)
    void updateWhenPersonnelOfficer() throws Exception {
        Department updated = getUpdated();
        perform(MockMvcRequestBuilders.put(REST_URL + DEPARTMENT_1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updated)))
                .andExpect(status().isNoContent());

        DEPARTMENT_MATCHER.assertMatch(departmentService.get(DEPARTMENT_1_ID), updated);
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
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
    @WithUserDetails(value = ADMIN_MAIL)
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
    void updateUnAuth() throws Exception {
        Department updated = getUpdated();
        perform(MockMvcRequestBuilders.put(REST_URL + DEPARTMENT_1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updated)))
                .andExpect(status().isUnauthorized())
                .andExpect(errorType(UNAUTHORIZED_ERROR));
    }

    @Test
    @WithUserDetails(value = ECONOMIST_MAIL)
    void updateForbiddenWhenEconomist() throws Exception {
        Department updated = getUpdated();
        perform(MockMvcRequestBuilders.put(REST_URL + DEPARTMENT_1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updated)))
                .andExpect(status().isForbidden())
                .andExpect(errorType(ACCESS_DENIED_ERROR));
    }

    @Test
    @WithUserDetails(value = DEPARTMENT_HEAD_MAIL)
    void updateForbiddenWhenDepartmentHead() throws Exception {
        Department updated = getUpdated();
        perform(MockMvcRequestBuilders.put(REST_URL + DEPARTMENT_1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updated)))
                .andExpect(status().isForbidden())
                .andExpect(errorType(ACCESS_DENIED_ERROR));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
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
    @WithUserDetails(value = ADMIN_MAIL)
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
    @WithUserDetails(value = ADMIN_MAIL)
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
    @WithUserDetails(value = ADMIN_MAIL)
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