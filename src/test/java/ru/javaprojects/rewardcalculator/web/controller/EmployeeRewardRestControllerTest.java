package ru.javaprojects.rewardcalculator.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.javaprojects.rewardcalculator.service.DepartmentRewardService;
import ru.javaprojects.rewardcalculator.service.EmployeeRewardService;
import ru.javaprojects.rewardcalculator.to.EmployeeRewardTo;
import ru.javaprojects.rewardcalculator.web.json.JsonUtil;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.javaprojects.rewardcalculator.testdata.DepartmentRewardTestData.NOT_FOUND;
import static ru.javaprojects.rewardcalculator.testdata.DepartmentRewardTestData.*;
import static ru.javaprojects.rewardcalculator.testdata.EmployeeRewardTestData.getUpdated;
import static ru.javaprojects.rewardcalculator.testdata.EmployeeRewardTestData.getUpdatedTo;
import static ru.javaprojects.rewardcalculator.testdata.EmployeeRewardTestData.*;
import static ru.javaprojects.rewardcalculator.util.exception.ErrorType.DATA_NOT_FOUND;
import static ru.javaprojects.rewardcalculator.util.exception.ErrorType.VALIDATION_ERROR;

class EmployeeRewardRestControllerTest extends AbstractControllerTest {
    private static final String REST_URL = EmployeeRewardRestController.REST_URL + '/';

    @Autowired
    private EmployeeRewardService employeeRewardService;

    @Autowired
    private DepartmentRewardService departmentRewardService;

    @Test
    void getAll() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "departmentrewards/" + DEPARTMENT_REWARD_2_ID + "/employeerewards"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(EMPLOYEE_REWARD_MATCHER.contentJson(employeeReward1, employeeReward2, employeeReward3));
    }

    @Test
    void getAllWithNotExistedDepartmentReward() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "departmentrewards/" + NOT_FOUND + "/employeerewards"))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(DATA_NOT_FOUND));
    }

    @Test
    void get() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "employeerewards/" + EMPLOYEE_REWARD_1_ID))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(EMPLOYEE_REWARD_MATCHER.contentJson(employeeReward1));
    }

    @Test
    void getNotFound() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "employeerewards/" + NOT_FOUND))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(DATA_NOT_FOUND));
    }

    @Test
    void update() throws Exception {
        EmployeeRewardTo updatedTo = getUpdatedTo();
        perform(MockMvcRequestBuilders.put(REST_URL + "employeerewards/" + EMPLOYEE_REWARD_1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedTo)))
                .andExpect(status().isNoContent());

        EMPLOYEE_REWARD_MATCHER.assertMatch(employeeRewardService.get(EMPLOYEE_REWARD_1_ID), getUpdated());
        DEPARTMENT_REWARD_MATCHER.assertMatch(departmentRewardService.get(DEPARTMENT_REWARD_2_ID), departmentReward2Updated);
    }

    @Test
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