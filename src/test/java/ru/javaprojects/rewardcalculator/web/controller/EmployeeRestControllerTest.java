package ru.javaprojects.rewardcalculator.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.javaprojects.rewardcalculator.model.Employee;
import ru.javaprojects.rewardcalculator.service.EmployeeService;
import ru.javaprojects.rewardcalculator.to.EmployeeTo;
import ru.javaprojects.rewardcalculator.util.exception.NotFoundException;
import ru.javaprojects.rewardcalculator.web.json.JsonUtil;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.javaprojects.rewardcalculator.TestUtil.readFromJson;
import static ru.javaprojects.rewardcalculator.testdata.DepartmentTestData.DEPARTMENT_1_ID;
import static ru.javaprojects.rewardcalculator.testdata.DepartmentTestData.DEPARTMENT_3_ID;
import static ru.javaprojects.rewardcalculator.testdata.EmployeeTestData.NOT_FOUND;
import static ru.javaprojects.rewardcalculator.testdata.EmployeeTestData.getNew;
import static ru.javaprojects.rewardcalculator.testdata.EmployeeTestData.getNewTo;
import static ru.javaprojects.rewardcalculator.testdata.EmployeeTestData.getUpdated;
import static ru.javaprojects.rewardcalculator.testdata.EmployeeTestData.getUpdatedTo;
import static ru.javaprojects.rewardcalculator.testdata.EmployeeTestData.*;
import static ru.javaprojects.rewardcalculator.testdata.PositionTestData.*;
import static ru.javaprojects.rewardcalculator.testdata.UserTestData.*;
import static ru.javaprojects.rewardcalculator.util.exception.ErrorType.*;

class EmployeeRestControllerTest extends AbstractControllerTest {
    private static final String REST_URL = EmployeeRestController.REST_URL + '/';

    @Autowired
    private EmployeeService service;

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void getAllNotFiredWhenAdmin() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "departments/" + DEPARTMENT_1_ID + "/employees"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(EMPLOYEE_MATCHER.contentJson(employee1, employee2, employee3));
    }

    @Test
    @WithUserDetails(value = PERSONNEL_OFFICER_MAIL)
    void getAllNotFiredWhenPersonnelOfficer() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "departments/" + DEPARTMENT_1_ID + "/employees"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(EMPLOYEE_MATCHER.contentJson(employee1, employee2, employee3));
    }

    @Test
    @WithUserDetails(value = ECONOMIST_MAIL)
    void getAllNotFiredWhenEconomist() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "departments/" + DEPARTMENT_1_ID + "/employees"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(EMPLOYEE_MATCHER.contentJson(employee1, employee2, employee3));
    }

    @Test
    @WithUserDetails(value = DEPARTMENT_HEAD_MAIL)
    void getAllNotFiredWhenDepartmentHead() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "departments/" + DEPARTMENT_1_ID + "/employees"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(EMPLOYEE_MATCHER.contentJson(employee1, employee2, employee3));
    }

    @Test
    @WithUserDetails(value = DEPARTMENT_HEAD_MAIL)
    void getAllNotFiredForbiddenWhenDepartmentHead() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "departments/" + DEPARTMENT_3_ID + "/employees"))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(errorType(ACCESS_DENIED_ERROR));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void getAllNotFiredWithNotExistedDepartment() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "departments/" + NOT_FOUND + "/employees"))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(DATA_NOT_FOUND));
    }

    @Test
    void getAllNotFiredUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "departments/" + DEPARTMENT_1_ID + "/employees"))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(errorType(UNAUTHORIZED_ERROR));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void getAllFiredWhenAdmin() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "departments/" + DEPARTMENT_1_ID + "/employees/fired"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(EMPLOYEE_MATCHER.contentJson(firedEmployee1, firedEmployee2, firedEmployee3));
    }

    @Test
    @WithUserDetails(value = PERSONNEL_OFFICER_MAIL)
    void getAllFiredWhenPersonnelOfficer() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "departments/" + DEPARTMENT_1_ID + "/employees/fired"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(EMPLOYEE_MATCHER.contentJson(firedEmployee1, firedEmployee2, firedEmployee3));
    }

    @Test
    @WithUserDetails(value = ECONOMIST_MAIL)
    void getAllFiredWhenEconomist() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "departments/" + DEPARTMENT_1_ID + "/employees/fired"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(EMPLOYEE_MATCHER.contentJson(firedEmployee1, firedEmployee2, firedEmployee3));
    }

    @Test
    @WithUserDetails(value = DEPARTMENT_HEAD_MAIL)
    void getAllFiredWhenDepartmentHead() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "departments/" + DEPARTMENT_1_ID + "/employees/fired"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(EMPLOYEE_MATCHER.contentJson(firedEmployee1, firedEmployee2, firedEmployee3));
    }

    @Test
    @WithUserDetails(value = DEPARTMENT_HEAD_MAIL)
    void getAllFiredForbiddenWhenDepartmentHead() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "departments/" + DEPARTMENT_3_ID + "/employees/fired"))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(errorType(ACCESS_DENIED_ERROR));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void getAllFiredWithNotExistedDepartment() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "departments/" + NOT_FOUND + "/employees/fired"))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(DATA_NOT_FOUND));
    }

    @Test
    void getAllFiredUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "departments/" + DEPARTMENT_1_ID + "/employees/fired"))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(errorType(UNAUTHORIZED_ERROR));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void getWhenAdmin() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "employees/" + EMPLOYEE_1_ID))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(EMPLOYEE_MATCHER.contentJson(employee1));
    }

    @Test
    @WithUserDetails(value = PERSONNEL_OFFICER_MAIL)
    void getWhenPersonnelOfficer() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "employees/" + EMPLOYEE_1_ID))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(EMPLOYEE_MATCHER.contentJson(employee1));
    }

    @Test
    @WithUserDetails(value = ECONOMIST_MAIL)
    void getWhenEconomist() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "employees/" + EMPLOYEE_1_ID))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(EMPLOYEE_MATCHER.contentJson(employee1));
    }

    @Test
    @WithUserDetails(value = DEPARTMENT_HEAD_MAIL)
    void getWhenDepartmentHead() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "employees/" + EMPLOYEE_1_ID))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(EMPLOYEE_MATCHER.contentJson(employee1));
    }

    @Test
    @WithUserDetails(value = DEPARTMENT_HEAD_MAIL)
    void getForbiddenWhenDepartmentHead() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "employees/" + EMPLOYEE_ANOTHER_DEPARTMENT_ID))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(errorType(ACCESS_DENIED_ERROR));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void getNotFound() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "employees/" + NOT_FOUND))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(DATA_NOT_FOUND));
    }

    @Test
    void getUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "employees/" + EMPLOYEE_1_ID))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(errorType(UNAUTHORIZED_ERROR));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void deleteWhenAdmin() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + "employees/" + EMPLOYEE_3_ID))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertThrows(NotFoundException.class, () -> service.get(EMPLOYEE_3_ID));
    }

    @Test
    @WithUserDetails(value = PERSONNEL_OFFICER_MAIL)
    void deleteWhenPersonnelOfficer() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + "employees/" + EMPLOYEE_3_ID))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertThrows(NotFoundException.class, () -> service.get(EMPLOYEE_3_ID));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void deleteNotFound() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + "employees/" + NOT_FOUND))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(DATA_NOT_FOUND));
    }

    @Test
    void deleteUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + "employees/" + EMPLOYEE_3_ID))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(errorType(UNAUTHORIZED_ERROR));
    }

    @Test
    @WithUserDetails(value = ECONOMIST_MAIL)
    void deleteForbiddenWhenEconomist() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + "employees/" + EMPLOYEE_3_ID))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(errorType(ACCESS_DENIED_ERROR));
    }

    @Test
    @WithUserDetails(value = DEPARTMENT_HEAD_MAIL)
    void deleteForbiddenWhenDepartmentHead() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + "employees/" + EMPLOYEE_3_ID))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(errorType(ACCESS_DENIED_ERROR));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void createWithLocationWhenAdmin() throws Exception {
        EmployeeTo newEmployeeTo = getNewTo();
        ResultActions action = perform(MockMvcRequestBuilders.post(REST_URL + "/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newEmployeeTo)))
                .andExpect(status().isCreated());

        Employee created = readFromJson(action, Employee.class);
        int newId = created.id();
        Employee newEmployee = getNew();
        newEmployee.setId(newId);
        EMPLOYEE_MATCHER.assertMatch(created, newEmployee);
        EMPLOYEE_MATCHER.assertMatch(service.get(newId), newEmployee);
    }

    @Test
    @WithUserDetails(value = PERSONNEL_OFFICER_MAIL)
    void createWithLocationWhenPersonnelOfficer() throws Exception {
        EmployeeTo newEmployeeTo = getNewTo();
        ResultActions action = perform(MockMvcRequestBuilders.post(REST_URL + "/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newEmployeeTo)))
                .andExpect(status().isCreated());

        Employee created = readFromJson(action, Employee.class);
        int newId = created.id();
        Employee newEmployee = getNew();
        newEmployee.setId(newId);
        EMPLOYEE_MATCHER.assertMatch(created, newEmployee);
        EMPLOYEE_MATCHER.assertMatch(service.get(newId), newEmployee);
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void createWithNotExistedPosition() throws Exception {
        EmployeeTo newEmployeeTo = getNewTo();
        newEmployeeTo.setPositionId(NOT_FOUND);
        perform(MockMvcRequestBuilders.post(REST_URL + "/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newEmployeeTo)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(DATA_NOT_FOUND));
    }

    @Test
    void createUnAuth() throws Exception {
        EmployeeTo newEmployeeTo = getNewTo();
        perform(MockMvcRequestBuilders.post(REST_URL + "/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newEmployeeTo)))
                .andExpect(status().isUnauthorized())
                .andExpect(errorType(UNAUTHORIZED_ERROR));
    }

    @Test
    @WithUserDetails(value = ECONOMIST_MAIL)
    void createForbiddenWhenEconomist() throws Exception {
        EmployeeTo newEmployeeTo = getNewTo();
        perform(MockMvcRequestBuilders.post(REST_URL + "/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newEmployeeTo)))
                .andExpect(status().isForbidden())
                .andExpect(errorType(ACCESS_DENIED_ERROR));
    }

    @Test
    @WithUserDetails(value = DEPARTMENT_HEAD_MAIL)
    void createForbiddenWhenDepartmentHead() throws Exception {
        EmployeeTo newEmployeeTo = getNewTo();
        perform(MockMvcRequestBuilders.post(REST_URL + "/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newEmployeeTo)))
                .andExpect(status().isForbidden())
                .andExpect(errorType(ACCESS_DENIED_ERROR));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void updateWhenAdmin() throws Exception {
        EmployeeTo updatedTo = getUpdatedTo();
        perform(MockMvcRequestBuilders.put(REST_URL + "employees/" + EMPLOYEE_1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedTo)))
                .andExpect(status().isNoContent());

        EMPLOYEE_MATCHER.assertMatch(service.get(EMPLOYEE_1_ID), getUpdated());
    }

    @Test
    @WithUserDetails(value = PERSONNEL_OFFICER_MAIL)
    void updateWhenPersonnelOfficer() throws Exception {
        EmployeeTo updatedTo = getUpdatedTo();
        perform(MockMvcRequestBuilders.put(REST_URL + "employees/" + EMPLOYEE_1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedTo)))
                .andExpect(status().isNoContent());

        EMPLOYEE_MATCHER.assertMatch(service.get(EMPLOYEE_1_ID), getUpdated());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void updateWithNotExistedPosition() throws Exception {
        EmployeeTo updatedTo = getUpdatedTo();
        updatedTo.setPositionId(NOT_FOUND);
        perform(MockMvcRequestBuilders.put(REST_URL + "employees/" + EMPLOYEE_1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedTo)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(DATA_NOT_FOUND));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void updateChangePosition() throws Exception {
        EmployeeTo updatedTo = getUpdatedTo();
        updatedTo.setPositionId(POSITION_2_ID);
        perform(MockMvcRequestBuilders.put(REST_URL + "employees/" + EMPLOYEE_1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedTo)))
                .andExpect(status().isNoContent());

        Employee employee = service.getWithPositionDepartment(EMPLOYEE_1_ID);
        EMPLOYEE_MATCHER.assertMatch(employee, getUpdated());
        POSITION_MATCHER.assertMatch(employee.getPosition(), position2);
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void updateIdNotConsistent() throws Exception {
        EmployeeTo updatedTo = getUpdatedTo();
        updatedTo.setId(EMPLOYEE_2_ID);
        perform(MockMvcRequestBuilders.put(REST_URL + "employees/" + EMPLOYEE_1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedTo)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void updateNotFound() throws Exception {
        EmployeeTo updatedTo = getUpdatedTo();
        updatedTo.setId(null);
        perform(MockMvcRequestBuilders.put(REST_URL + "employees/" + NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedTo)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(DATA_NOT_FOUND));
    }

    @Test
    void updateUnAuth() throws Exception {
        EmployeeTo updatedTo = getUpdatedTo();
        perform(MockMvcRequestBuilders.put(REST_URL + "employees/" + EMPLOYEE_1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedTo)))
                .andExpect(status().isUnauthorized())
                .andExpect(errorType(UNAUTHORIZED_ERROR));
    }

    @Test
    @WithUserDetails(value = ECONOMIST_MAIL)
    void updateForbiddenWhenEconomist() throws Exception {
        EmployeeTo updatedTo = getUpdatedTo();
        perform(MockMvcRequestBuilders.put(REST_URL + "employees/" + EMPLOYEE_1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedTo)))
                .andExpect(status().isForbidden())
                .andExpect(errorType(ACCESS_DENIED_ERROR));
    }

    @Test
    @WithUserDetails(value = DEPARTMENT_HEAD_MAIL)
    void updateForbiddenWhenDepartmentHead() throws Exception {
        EmployeeTo updatedTo = getUpdatedTo();
        perform(MockMvcRequestBuilders.put(REST_URL + "employees/" + EMPLOYEE_1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedTo)))
                .andExpect(status().isForbidden())
                .andExpect(errorType(ACCESS_DENIED_ERROR));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void changeWorkingStatusWhenAdmin() throws Exception {
        perform(MockMvcRequestBuilders.patch(REST_URL + "employees/" + EMPLOYEE_1_ID)
                .param("fired", "true")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());

        assertTrue(service.get(EMPLOYEE_1_ID).isFired());
    }

    @Test
    @WithUserDetails(value = PERSONNEL_OFFICER_MAIL)
    void changeWorkingStatusWhenPersonnelOfficer() throws Exception {
        perform(MockMvcRequestBuilders.patch(REST_URL + "employees/" + EMPLOYEE_1_ID)
                .param("fired", "true")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());

        assertTrue(service.get(EMPLOYEE_1_ID).isFired());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void changeWorkingStatusNotFound() throws Exception {
        perform(MockMvcRequestBuilders.patch(REST_URL + "employees/" + NOT_FOUND)
                .param("fired", "true")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(DATA_NOT_FOUND));
    }

    @Test
    void changeWorkingStatusUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.patch(REST_URL + "employees/" + EMPLOYEE_1_ID)
                .param("fired", "true")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(errorType(UNAUTHORIZED_ERROR));
    }

    @Test
    @WithUserDetails(value = ECONOMIST_MAIL)
    void changeWorkingStatusForbiddenWhenEconomist() throws Exception {
        perform(MockMvcRequestBuilders.patch(REST_URL + "employees/" + EMPLOYEE_1_ID)
                .param("fired", "true")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(errorType(ACCESS_DENIED_ERROR));
    }

    @Test
    @WithUserDetails(value = DEPARTMENT_HEAD_MAIL)
    void changeWorkingStatusForbiddenWhenDepartmentHead() throws Exception {
        perform(MockMvcRequestBuilders.patch(REST_URL + "employees/" + EMPLOYEE_1_ID)
                .param("fired", "true")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(errorType(ACCESS_DENIED_ERROR));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void createInvalid() throws Exception {
        EmployeeTo newEmployeeTo = getNewTo();
        newEmployeeTo.setName(" ");
        perform(MockMvcRequestBuilders.post(REST_URL + "/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newEmployeeTo)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void updateInvalid() throws Exception {
        EmployeeTo updatedTo = getUpdatedTo();
        updatedTo.setPositionId(null);
        perform(MockMvcRequestBuilders.put(REST_URL + "/employees/" + EMPLOYEE_1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedTo)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR));
    }
}