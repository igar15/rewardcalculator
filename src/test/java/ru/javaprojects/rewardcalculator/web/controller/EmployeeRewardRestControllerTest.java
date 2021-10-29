package ru.javaprojects.rewardcalculator.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.javaprojects.rewardcalculator.service.DepartmentRewardService;
import ru.javaprojects.rewardcalculator.service.EmployeeRewardService;
import ru.javaprojects.rewardcalculator.to.EmployeeRewardTo;
import ru.javaprojects.rewardcalculator.web.json.JsonUtil;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.javaprojects.rewardcalculator.TestUtil.checkPdf;
import static ru.javaprojects.rewardcalculator.testdata.DepartmentRewardTestData.NOT_FOUND;
import static ru.javaprojects.rewardcalculator.testdata.DepartmentRewardTestData.*;
import static ru.javaprojects.rewardcalculator.testdata.EmployeeRewardTestData.getUpdated;
import static ru.javaprojects.rewardcalculator.testdata.EmployeeRewardTestData.getUpdatedTo;
import static ru.javaprojects.rewardcalculator.testdata.EmployeeRewardTestData.*;
import static ru.javaprojects.rewardcalculator.testdata.UserTestData.*;
import static ru.javaprojects.rewardcalculator.util.exception.ErrorType.*;

class EmployeeRewardRestControllerTest extends AbstractControllerTest {
    private static final String REST_URL = EmployeeRewardRestController.REST_URL + '/';

    @Autowired
    private EmployeeRewardService employeeRewardService;

    @Autowired
    private DepartmentRewardService departmentRewardService;

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void getAllWhenAdmin() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "departmentrewards/" + DEPARTMENT_REWARD_2_ID + "/employeerewards"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(EMPLOYEE_REWARD_MATCHER.contentJson(employeeReward1, employeeReward2, employeeReward3));
    }

    @Test
    @WithUserDetails(value = DEPARTMENT_HEAD_MAIL)
    void getAllWhenDepartmentHead() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "departmentrewards/" + DEPARTMENT_REWARD_2_ID + "/employeerewards"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(EMPLOYEE_REWARD_MATCHER.contentJson(employeeReward1, employeeReward2, employeeReward3));
    }

    @Test
    @WithUserDetails(value = DEPARTMENT_HEAD_MAIL)
    void getAllForbiddenWhenDepartmentHead() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "departmentrewards/" + DEPARTMENT_REWARD_ANOTHER_DEPARTMENT_ID + "/employeerewards"))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(errorType(ACCESS_DENIED_ERROR));
    }

    @Test
    @WithUserDetails(value = ECONOMIST_MAIL)
    void getAllForbiddenWhenEconomist() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "departmentrewards/" + DEPARTMENT_REWARD_2_ID + "/employeerewards"))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(errorType(ACCESS_DENIED_ERROR));
    }

    @Test
    @WithUserDetails(value = PERSONNEL_OFFICER_MAIL)
    void getAllForbiddenWhenPersonnelOfficer() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "departmentrewards/" + DEPARTMENT_REWARD_2_ID + "/employeerewards"))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(errorType(ACCESS_DENIED_ERROR));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void getAllWithNotExistedDepartmentReward() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "departmentrewards/" + NOT_FOUND + "/employeerewards"))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(DATA_NOT_FOUND));
    }

    @Test
    void getAllUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "departmentrewards/" + DEPARTMENT_REWARD_2_ID + "/employeerewards"))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(errorType(UNAUTHORIZED_ERROR));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void getAllInPdfWhenAdmin() throws Exception {
        ResultActions action = perform(MockMvcRequestBuilders.get(REST_URL + "departmentrewards/" + DEPARTMENT_REWARD_2_ID + "/employeerewards/pdf"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PDF));

        byte[] pdfBytes = action.andReturn().getResponse().getContentAsByteArray();
        checkPdf(pdfBytes);
    }

    @Test
    @WithUserDetails(value = DEPARTMENT_HEAD_MAIL)
    void getAllInPdfWhenDepartmentHead() throws Exception {
        ResultActions action = perform(MockMvcRequestBuilders.get(REST_URL + "departmentrewards/" + DEPARTMENT_REWARD_2_ID + "/employeerewards/pdf"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PDF));

        byte[] pdfBytes = action.andReturn().getResponse().getContentAsByteArray();
        checkPdf(pdfBytes);
    }

    @Test
    @WithUserDetails(value = DEPARTMENT_HEAD_MAIL)
    void getAllInPdfForbiddenWhenDepartmentHead() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "departmentrewards/" + DEPARTMENT_REWARD_ANOTHER_DEPARTMENT_ID + "/employeerewards/pdf"))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(errorType(ACCESS_DENIED_ERROR));
    }

    @Test
    @WithUserDetails(value = ECONOMIST_MAIL)
    void getAllInPdfForbiddenWhenEconomist() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "departmentrewards/" + DEPARTMENT_REWARD_2_ID + "/employeerewards/pdf"))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(errorType(ACCESS_DENIED_ERROR));
    }

    @Test
    @WithUserDetails(value = PERSONNEL_OFFICER_MAIL)
    void getAllInPdfForbiddenWhenPersonnelOfficer() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "departmentrewards/" + DEPARTMENT_REWARD_2_ID + "/employeerewards/pdf"))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(errorType(ACCESS_DENIED_ERROR));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void getAllInPdfWithNotExistedDepartmentReward() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "departmentrewards/" + NOT_FOUND + "/employeerewards/pdf"))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(DATA_NOT_FOUND));
    }

    @Test
    void getAllInPdfUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "departmentrewards/" + DEPARTMENT_REWARD_2_ID + "/employeerewards/pdf"))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(errorType(UNAUTHORIZED_ERROR));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void getWhenAdmin() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "employeerewards/" + EMPLOYEE_REWARD_1_ID))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(EMPLOYEE_REWARD_MATCHER.contentJson(employeeReward1));
    }

    @Test
    @WithUserDetails(value = DEPARTMENT_HEAD_MAIL)
    void getWhenDepartmentHead() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "employeerewards/" + EMPLOYEE_REWARD_1_ID))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(EMPLOYEE_REWARD_MATCHER.contentJson(employeeReward1));
    }

    @Test
    @WithUserDetails(value = DEPARTMENT_HEAD_MAIL)
    void getForbiddenWhenDepartmentHead() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "employeerewards/" + EMPLOYEE_REWARD_ANOTHER_DEPARTMENT_ID))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(errorType(ACCESS_DENIED_ERROR));
    }

    @Test
    @WithUserDetails(value = ECONOMIST_MAIL)
    void getForbiddenWhenEconomist() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "employeerewards/" + EMPLOYEE_REWARD_1_ID))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(errorType(ACCESS_DENIED_ERROR));
    }

    @Test
    @WithUserDetails(value = PERSONNEL_OFFICER_MAIL)
    void getForbiddenWhenPersonnelOfficer() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "employeerewards/" + EMPLOYEE_REWARD_1_ID))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(errorType(ACCESS_DENIED_ERROR));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void getNotFound() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "employeerewards/" + NOT_FOUND))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(DATA_NOT_FOUND));
    }

    @Test
    void getUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "employeerewards/" + EMPLOYEE_REWARD_1_ID))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(errorType(UNAUTHORIZED_ERROR));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void updateWhenAdmin() throws Exception {
        EmployeeRewardTo updatedTo = getUpdatedTo();
        perform(MockMvcRequestBuilders.put(REST_URL + "employeerewards/" + EMPLOYEE_REWARD_1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedTo)))
                .andExpect(status().isNoContent());

        EMPLOYEE_REWARD_MATCHER.assertMatch(employeeRewardService.get(EMPLOYEE_REWARD_1_ID), getUpdated());
        DEPARTMENT_REWARD_MATCHER.assertMatch(departmentRewardService.get(DEPARTMENT_REWARD_2_ID), departmentReward2Updated);
    }

    @Test
    @WithUserDetails(value = DEPARTMENT_HEAD_MAIL)
    void updateWhenDepartmentHead() throws Exception {
        EmployeeRewardTo updatedTo = getUpdatedTo();
        perform(MockMvcRequestBuilders.put(REST_URL + "employeerewards/" + EMPLOYEE_REWARD_1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedTo)))
                .andExpect(status().isNoContent());

        EMPLOYEE_REWARD_MATCHER.assertMatch(employeeRewardService.get(EMPLOYEE_REWARD_1_ID), getUpdated());
        DEPARTMENT_REWARD_MATCHER.assertMatch(departmentRewardService.get(DEPARTMENT_REWARD_2_ID), departmentReward2Updated);
    }

    @Test
    @WithUserDetails(value = DEPARTMENT_HEAD_MAIL)
    void updateForbiddenWhenDepartmentHead() throws Exception {
        EmployeeRewardTo updatedTo = getUpdatedTo();
        updatedTo.setId(EMPLOYEE_REWARD_ANOTHER_DEPARTMENT_ID);
        perform(MockMvcRequestBuilders.put(REST_URL + "employeerewards/" + EMPLOYEE_REWARD_ANOTHER_DEPARTMENT_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedTo)))
                .andExpect(status().isForbidden())
                .andExpect(errorType(ACCESS_DENIED_ERROR));
    }

    @Test
    @WithUserDetails(value = ECONOMIST_MAIL)
    void updateForbiddenWhenEconomist() throws Exception {
        EmployeeRewardTo updatedTo = getUpdatedTo();
        perform(MockMvcRequestBuilders.put(REST_URL + "employeerewards/" + EMPLOYEE_REWARD_1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedTo)))
                .andExpect(status().isForbidden())
                .andExpect(errorType(ACCESS_DENIED_ERROR));
    }

    @Test
    @WithUserDetails(value = PERSONNEL_OFFICER_MAIL)
    void updateForbiddenWhenPersonnelOfficer() throws Exception {
        EmployeeRewardTo updatedTo = getUpdatedTo();
        perform(MockMvcRequestBuilders.put(REST_URL + "employeerewards/" + EMPLOYEE_REWARD_1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedTo)))
                .andExpect(status().isForbidden())
                .andExpect(errorType(ACCESS_DENIED_ERROR));
    }

    @Test
    void updateUnAuth() throws Exception {
        EmployeeRewardTo updatedTo = getUpdatedTo();
        perform(MockMvcRequestBuilders.put(REST_URL + "employeerewards/" + EMPLOYEE_REWARD_1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedTo)))
                .andExpect(status().isUnauthorized())
                .andExpect(errorType(UNAUTHORIZED_ERROR));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void updateWithNegativeFullReward() throws Exception {
        EmployeeRewardTo updatedTo = getUpdatedTo();
        updatedTo.setPenalty(11000);
        perform(MockMvcRequestBuilders.put(REST_URL + "employeerewards/" + EMPLOYEE_REWARD_1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedTo)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void updateWithAllocatedAmountExceededTooMuchReward() throws Exception {
        EmployeeRewardTo updatedTo = getUpdatedTo();
        updatedTo.setAdditionalReward(4500);
        perform(MockMvcRequestBuilders.put(REST_URL + "employeerewards/" + EMPLOYEE_REWARD_1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedTo)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void updateWithAllocatedAmountExceededTooMuchHoursWorked() throws Exception {
        EmployeeRewardTo updatedTo = getUpdatedTo();
        updatedTo.setHoursWorked(295d);
        perform(MockMvcRequestBuilders.put(REST_URL + "employeerewards/" + EMPLOYEE_REWARD_1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedTo)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void updateNotFound() throws Exception {
        EmployeeRewardTo updatedTo = getUpdatedTo();
        updatedTo.setId(null);
        perform(MockMvcRequestBuilders.put(REST_URL + "employeerewards/" + NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedTo)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(DATA_NOT_FOUND));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void updateIdNotConsistent() throws Exception {
        EmployeeRewardTo updatedTo = getUpdatedTo();
        updatedTo.setId(EMPLOYEE_REWARD_2_ID);
        perform(MockMvcRequestBuilders.put(REST_URL + "employeerewards/" + EMPLOYEE_REWARD_1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedTo)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void updateInvalid() throws Exception {
        EmployeeRewardTo updatedTo = getUpdatedTo();
        updatedTo.setHoursWorked(-20d);
        perform(MockMvcRequestBuilders.put(REST_URL + "employeerewards/" + EMPLOYEE_REWARD_1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedTo)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR));
    }
}