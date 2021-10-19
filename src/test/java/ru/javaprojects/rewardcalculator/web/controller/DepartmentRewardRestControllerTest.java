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
import ru.javaprojects.rewardcalculator.model.DepartmentReward;
import ru.javaprojects.rewardcalculator.model.PaymentPeriod;
import ru.javaprojects.rewardcalculator.repository.DepartmentRewardRepository;
import ru.javaprojects.rewardcalculator.service.DepartmentRewardService;
import ru.javaprojects.rewardcalculator.to.DepartmentRewardTo;
import ru.javaprojects.rewardcalculator.util.exception.NotFoundException;
import ru.javaprojects.rewardcalculator.web.json.JsonUtil;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.javaprojects.rewardcalculator.testdata.DepartmentRewardTestData.NOT_FOUND;
import static ru.javaprojects.rewardcalculator.testdata.DepartmentRewardTestData.getNew;
import static ru.javaprojects.rewardcalculator.testdata.DepartmentRewardTestData.getUpdated;
import static ru.javaprojects.rewardcalculator.testdata.DepartmentRewardTestData.*;
import static ru.javaprojects.rewardcalculator.testdata.DepartmentTestData.*;
import static ru.javaprojects.rewardcalculator.testdata.PaymentPeriodTestData.*;
import static ru.javaprojects.rewardcalculator.TestUtil.readFromJson;
import static ru.javaprojects.rewardcalculator.util.exception.ErrorType.DATA_NOT_FOUND;
import static ru.javaprojects.rewardcalculator.util.exception.ErrorType.VALIDATION_ERROR;
import static ru.javaprojects.rewardcalculator.web.AppExceptionHandler.EXCEPTION_DUPLICATE_DEPARTMENT_REWARD;

class DepartmentRewardRestControllerTest extends AbstractControllerTest {
    private static final String REST_URL = DepartmentRewardRestController.REST_URL + '/';

    @Autowired
    private DepartmentRewardService service;

    @Autowired
    private DepartmentRewardRepository repository;

    @Test
    void getAll() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "departments/" + DEPARTMENT_1_ID + "/departmentrewards"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(DEPARTMENT_REWARD_MATCHER.contentJson(departmentReward2, departmentReward1));
    }

    @Test
    void getAllWithNotExistedDepartment() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "departments/" + NOT_FOUND + "/departmentrewards"))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(DATA_NOT_FOUND));
    }


    @Test
    void get() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "departmentrewards/" + DEPARTMENT_REWARD_1_ID))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(DEPARTMENT_REWARD_MATCHER.contentJson(departmentReward1));
    }

    @Test
    void getNotFound() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "departmentrewards/" + NOT_FOUND))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(DATA_NOT_FOUND));
    }

    @Test
    void delete() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + "departmentrewards/" + DEPARTMENT_REWARD_1_ID))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertThrows(NotFoundException.class, () -> service.get(DEPARTMENT_REWARD_1_ID));
    }

    @Test
    void deleteNotFound() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + "departmentrewards/" + NOT_FOUND))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(DATA_NOT_FOUND));
    }

    @Test
    void createWithLocation() throws Exception {
        DepartmentRewardTo newDepartmentRewardTo = getNewTo();
        ResultActions action = perform(MockMvcRequestBuilders.post(REST_URL + "/departmentrewards")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newDepartmentRewardTo)))
                .andExpect(status().isCreated());

        DepartmentReward created = readFromJson(action, DepartmentReward.class);
        int newId = created.id();
        DepartmentReward newDepartmentReward = getNew();
        newDepartmentReward.setId(newId);
        DEPARTMENT_REWARD_MATCHER.assertMatch(created, newDepartmentReward);
        DEPARTMENT_REWARD_MATCHER.assertMatch(service.get(newId), newDepartmentReward);
    }

    @Test
    void createWithNotExistedDepartment() throws Exception {
        DepartmentRewardTo newDepartmentRewardTo = getNewTo();
        newDepartmentRewardTo.setDepartmentId(NOT_FOUND);
        perform(MockMvcRequestBuilders.post(REST_URL + "/departmentrewards")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newDepartmentRewardTo)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(DATA_NOT_FOUND));
    }

    @Test
    void createWithNotExistedPaymentPeriod() throws Exception {
        DepartmentRewardTo newDepartmentRewardTo = getNewTo();
        newDepartmentRewardTo.setPaymentPeriodId(NOT_FOUND);
        perform(MockMvcRequestBuilders.post(REST_URL + "/departmentrewards")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newDepartmentRewardTo)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(DATA_NOT_FOUND));
    }

    @Test
    void update() throws Exception {
        DepartmentRewardTo updatedTo = getUpdatedTo();
        perform(MockMvcRequestBuilders.put(REST_URL + "departmentrewards/" + DEPARTMENT_REWARD_1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedTo)))
                .andExpect(status().isNoContent());

        DEPARTMENT_REWARD_MATCHER.assertMatch(service.get(DEPARTMENT_REWARD_1_ID), getUpdated());
    }

    @Test
    void updateWithAllocatedAMountLessThanExistedDistributedAmount() throws Exception {
        DepartmentRewardTo updatedTo = getUpdatedTo();
        updatedTo.setAllocatedAmount(30000);
        perform(MockMvcRequestBuilders.put(REST_URL + "departmentrewards/" + DEPARTMENT_REWARD_1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedTo)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR));
    }

    // Department does not change on update. So we can pass any department id, even not existing.
    @Test
    void updateWithNotExistedDepartment() throws Exception {
        DepartmentRewardTo updatedTo = getUpdatedTo();
        updatedTo.setDepartmentId(NOT_FOUND);
        perform(MockMvcRequestBuilders.put(REST_URL + "departmentrewards/" + DEPARTMENT_REWARD_1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedTo)))
                .andExpect(status().isNoContent());

        DepartmentReward departmentReward = repository.findByIdWithDepartmentAndPaymentPeriod(DEPARTMENT_REWARD_1_ID);
        DEPARTMENT_REWARD_MATCHER.assertMatch(departmentReward, getUpdated());
        DEPARTMENT_MATCHER.assertMatch(Hibernate.unproxy(departmentReward.getDepartment(), Department.class), department1);
    }

    // Department does not change on update. So we can pass any department id, even not existing.
    @Test
    void updateWithTryingToChangeDepartment() throws Exception {
        DepartmentRewardTo updatedTo = getUpdatedTo();
        updatedTo.setDepartmentId(DEPARTMENT_2_ID);
        perform(MockMvcRequestBuilders.put(REST_URL + "departmentrewards/" + DEPARTMENT_REWARD_1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedTo)))
                .andExpect(status().isNoContent());

        DepartmentReward departmentReward = repository.findByIdWithDepartmentAndPaymentPeriod(DEPARTMENT_REWARD_1_ID);
        DEPARTMENT_REWARD_MATCHER.assertMatch(departmentReward, getUpdated());
        DEPARTMENT_MATCHER.assertMatch(Hibernate.unproxy(departmentReward.getDepartment(), Department.class), department1);
    }

    // Payment period does not change on update. So we can pass any payment period id, even not existing.
    @Test
    void updateWithNotExistedPaymentPeriod() throws Exception {
        DepartmentRewardTo updatedTo = getUpdatedTo();
        updatedTo.setPaymentPeriodId(NOT_FOUND);
        perform(MockMvcRequestBuilders.put(REST_URL + "departmentrewards/" + DEPARTMENT_REWARD_1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedTo)))
                .andExpect(status().isNoContent());

        DepartmentReward departmentReward = repository.findByIdWithDepartmentAndPaymentPeriod(DEPARTMENT_REWARD_1_ID);
        DEPARTMENT_REWARD_MATCHER.assertMatch(departmentReward, getUpdated());
        PAYMENT_PERIOD_MATCHER.assertMatch(Hibernate.unproxy(departmentReward.getPaymentPeriod(), PaymentPeriod.class), paymentPeriod1);
    }

    // Payment period does not change on update. So we can pass any payment period id, even not existing or duplicate.
    @Test
    void updateWithTryingToChangePaymentPeriod() throws Exception {
        DepartmentRewardTo updatedTo = getUpdatedTo();
        updatedTo.setPaymentPeriodId(PAYMENT_PERIOD_2_ID);
        perform(MockMvcRequestBuilders.put(REST_URL + "departmentrewards/" + DEPARTMENT_REWARD_1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedTo)))
                .andExpect(status().isNoContent());

        DepartmentReward departmentReward = repository.findByIdWithDepartmentAndPaymentPeriod(DEPARTMENT_REWARD_1_ID);
        DEPARTMENT_REWARD_MATCHER.assertMatch(departmentReward, getUpdated());
        PAYMENT_PERIOD_MATCHER.assertMatch(Hibernate.unproxy(departmentReward.getPaymentPeriod(), PaymentPeriod.class), paymentPeriod1);
    }

    @Test
    void updateNotFound() throws Exception {
        DepartmentRewardTo updatedTo = getUpdatedTo();
        updatedTo.setId(null);
        perform(MockMvcRequestBuilders.put(REST_URL + "departmentrewards/" + NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedTo)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(DATA_NOT_FOUND));
    }

    @Test
    void updateIdNotConsistent() throws Exception {
        DepartmentRewardTo updatedTo = getUpdatedTo();
        updatedTo.setId(DEPARTMENT_REWARD_2_ID);
        perform(MockMvcRequestBuilders.put(REST_URL + "departmentrewards/" + DEPARTMENT_REWARD_1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedTo)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR));
    }

    @Test
    void createInvalid() throws Exception {
        DepartmentRewardTo newDepartmentRewardTo = getNewTo();
        newDepartmentRewardTo.setAllocatedAmount(-100000);
        perform(MockMvcRequestBuilders.post(REST_URL + "/departmentrewards")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newDepartmentRewardTo)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR));
    }

    @Test
    void updateInvalid() throws Exception {
        DepartmentRewardTo updatedTo = getUpdatedTo();
        updatedTo.setAllocatedAmount(6_000_000);
        perform(MockMvcRequestBuilders.post(REST_URL + "/departmentrewards")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedTo)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR));
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void createDuplicateDepartmentPaymentPeriod() throws Exception {
        DepartmentRewardTo newDepartmentRewardTo = new DepartmentRewardTo(null, DEPARTMENT_1_ID, PAYMENT_PERIOD_1_ID, 200000);
        perform(MockMvcRequestBuilders.post(REST_URL + "/departmentrewards")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newDepartmentRewardTo)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR))
                .andExpect(detailMessage(EXCEPTION_DUPLICATE_DEPARTMENT_REWARD));
    }
}