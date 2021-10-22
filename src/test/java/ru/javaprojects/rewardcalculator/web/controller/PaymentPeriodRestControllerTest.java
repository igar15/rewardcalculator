package ru.javaprojects.rewardcalculator.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.javaprojects.rewardcalculator.model.PaymentPeriod;
import ru.javaprojects.rewardcalculator.service.PaymentPeriodService;
import ru.javaprojects.rewardcalculator.util.exception.NotFoundException;
import ru.javaprojects.rewardcalculator.web.json.JsonUtil;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.javaprojects.rewardcalculator.TestUtil.readFromJson;
import static ru.javaprojects.rewardcalculator.testdata.PaymentPeriodTestData.NOT_FOUND;
import static ru.javaprojects.rewardcalculator.testdata.PaymentPeriodTestData.getNew;
import static ru.javaprojects.rewardcalculator.testdata.PaymentPeriodTestData.getUpdated;
import static ru.javaprojects.rewardcalculator.testdata.PaymentPeriodTestData.*;
import static ru.javaprojects.rewardcalculator.testdata.PositionTestData.POSITION_1_ID;
import static ru.javaprojects.rewardcalculator.testdata.UserTestData.*;
import static ru.javaprojects.rewardcalculator.util.exception.ErrorType.*;
import static ru.javaprojects.rewardcalculator.web.AppExceptionHandler.EXCEPTION_DUPLICATE_PAYMENT_PERIOD;

class PaymentPeriodRestControllerTest extends AbstractControllerTest {
    private static final String REST_URL = PaymentPeriodRestController.REST_URL + '/';

    @Autowired
    private PaymentPeriodService service;

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void getAllWhenAdmin() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(PAYMENT_PERIOD_MATCHER.contentJson(paymentPeriod3, paymentPeriod2, paymentPeriod1));
    }

    @Test
    @WithUserDetails(value = DEPARTMENT_HEAD_MAIL)
    void getAllWhenDepartmentHead() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(PAYMENT_PERIOD_MATCHER.contentJson(paymentPeriod3, paymentPeriod2, paymentPeriod1));
    }

    @Test
    @WithUserDetails(value = ECONOMIST_MAIL)
    void getAllWhenEconomist() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(PAYMENT_PERIOD_MATCHER.contentJson(paymentPeriod3, paymentPeriod2, paymentPeriod1));
    }

    @Test
    @WithUserDetails(value = PERSONNEL_OFFICER_MAIL)
    void getAllWhenPersonnelOfficer() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(PAYMENT_PERIOD_MATCHER.contentJson(paymentPeriod3, paymentPeriod2, paymentPeriod1));
    }

    @Test
    void getAllUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isUnauthorized())
                .andExpect(errorType(UNAUTHORIZED_ERROR));
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

        List<PaymentPeriod> paymentPeriods = JsonUtil.readContentFromPage(action.andReturn().getResponse().getContentAsString(), PaymentPeriod.class);
        PAYMENT_PERIOD_MATCHER.assertMatch(paymentPeriods, paymentPeriod3, paymentPeriod2);
    }

    @Test
    @WithUserDetails(value = DEPARTMENT_HEAD_MAIL)
    void getAllByPageWhenDepartmentHead() throws Exception {
        ResultActions action = perform(MockMvcRequestBuilders.get(REST_URL + "byPage")
                .param("page", "0")
                .param("size", "2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        List<PaymentPeriod> paymentPeriods = JsonUtil.readContentFromPage(action.andReturn().getResponse().getContentAsString(), PaymentPeriod.class);
        PAYMENT_PERIOD_MATCHER.assertMatch(paymentPeriods, paymentPeriod3, paymentPeriod2);
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

        List<PaymentPeriod> paymentPeriods = JsonUtil.readContentFromPage(action.andReturn().getResponse().getContentAsString(), PaymentPeriod.class);
        PAYMENT_PERIOD_MATCHER.assertMatch(paymentPeriods, paymentPeriod3, paymentPeriod2);
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

        List<PaymentPeriod> paymentPeriods = JsonUtil.readContentFromPage(action.andReturn().getResponse().getContentAsString(), PaymentPeriod.class);
        PAYMENT_PERIOD_MATCHER.assertMatch(paymentPeriods, paymentPeriod3, paymentPeriod2);
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
    @WithUserDetails(value = ADMIN_MAIL)
    void getWhenAdmin() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + PAYMENT_PERIOD_1_ID))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(PAYMENT_PERIOD_MATCHER.contentJson(paymentPeriod1));
    }

    @Test
    @WithUserDetails(value = DEPARTMENT_HEAD_MAIL)
    void getWhenDepartmentHead() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + PAYMENT_PERIOD_1_ID))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(PAYMENT_PERIOD_MATCHER.contentJson(paymentPeriod1));
    }

    @Test
    @WithUserDetails(value = ECONOMIST_MAIL)
    void getWhenEconomist() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + PAYMENT_PERIOD_1_ID))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(PAYMENT_PERIOD_MATCHER.contentJson(paymentPeriod1));
    }

    @Test
    @WithUserDetails(value = PERSONNEL_OFFICER_MAIL)
    void getWhenPersonnelOfficer() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + PAYMENT_PERIOD_1_ID))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(PAYMENT_PERIOD_MATCHER.contentJson(paymentPeriod1));
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
        perform(MockMvcRequestBuilders.get(REST_URL + PAYMENT_PERIOD_1_ID))
                .andExpect(status().isUnauthorized())
                .andExpect(errorType(UNAUTHORIZED_ERROR));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void deleteWhenAdmin() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + PAYMENT_PERIOD_1_ID))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertThrows(NotFoundException.class, () -> service.get(PAYMENT_PERIOD_1_ID));
    }

    @Test
    @WithUserDetails(value = ECONOMIST_MAIL)
    void deleteWhenEconomist() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + PAYMENT_PERIOD_1_ID))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertThrows(NotFoundException.class, () -> service.get(PAYMENT_PERIOD_1_ID));
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
        perform(MockMvcRequestBuilders.delete(REST_URL + PAYMENT_PERIOD_1_ID))
                .andExpect(status().isUnauthorized())
                .andExpect(errorType(UNAUTHORIZED_ERROR));
    }

    @Test
    @WithUserDetails(value = DEPARTMENT_HEAD_MAIL)
    void deleteForbiddenWhenDepartmentHead() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + PAYMENT_PERIOD_1_ID))
                .andExpect(status().isForbidden())
                .andExpect(errorType(ACCESS_DENIED_ERROR));
    }

    @Test
    @WithUserDetails(value = PERSONNEL_OFFICER_MAIL)
    void deleteForbiddenWhenPersonnelOfficer() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + PAYMENT_PERIOD_1_ID))
                .andExpect(status().isForbidden())
                .andExpect(errorType(ACCESS_DENIED_ERROR));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void createWithLocationWhenAdmin() throws Exception {
        PaymentPeriod newPaymentPeriod = getNew();
        ResultActions action = perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newPaymentPeriod)))
                .andExpect(status().isCreated());

        PaymentPeriod created = readFromJson(action, PaymentPeriod.class);
        int newId = created.id();
        newPaymentPeriod.setId(newId);
        PAYMENT_PERIOD_MATCHER.assertMatch(created, newPaymentPeriod);
        PAYMENT_PERIOD_MATCHER.assertMatch(service.get(newId), newPaymentPeriod);
    }

    @Test
    @WithUserDetails(value = ECONOMIST_MAIL)
    void createWithLocationWhenEconomist() throws Exception {
        PaymentPeriod newPaymentPeriod = getNew();
        ResultActions action = perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newPaymentPeriod)))
                .andExpect(status().isCreated());

        PaymentPeriod created = readFromJson(action, PaymentPeriod.class);
        int newId = created.id();
        newPaymentPeriod.setId(newId);
        PAYMENT_PERIOD_MATCHER.assertMatch(created, newPaymentPeriod);
        PAYMENT_PERIOD_MATCHER.assertMatch(service.get(newId), newPaymentPeriod);
    }

    @Test
    void createUnAuth() throws Exception {
        PaymentPeriod newPaymentPeriod = getNew();
        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newPaymentPeriod)))
                .andExpect(status().isUnauthorized())
                .andExpect(errorType(UNAUTHORIZED_ERROR));
    }

    @Test
    @WithUserDetails(value = DEPARTMENT_HEAD_MAIL)
    void createForbiddenWhenDepartmentHead() throws Exception {
        PaymentPeriod newPaymentPeriod = getNew();
        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newPaymentPeriod)))
                .andExpect(status().isForbidden())
                .andExpect(errorType(ACCESS_DENIED_ERROR));
    }

    @Test
    @WithUserDetails(value = PERSONNEL_OFFICER_MAIL)
    void createForbiddenWhenPersonnelOfficer() throws Exception {
        PaymentPeriod newPaymentPeriod = getNew();
        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newPaymentPeriod)))
                .andExpect(status().isForbidden())
                .andExpect(errorType(ACCESS_DENIED_ERROR));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void updateWhenAdmin() throws Exception {
        PaymentPeriod updated = getUpdated();
        perform(MockMvcRequestBuilders.put(REST_URL + PAYMENT_PERIOD_1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updated)))
                .andExpect(status().isNoContent());

        PAYMENT_PERIOD_MATCHER.assertMatch(service.get(PAYMENT_PERIOD_1_ID), updated);
    }

    @Test
    @WithUserDetails(value = ECONOMIST_MAIL)
    void updateWhenEconomist() throws Exception {
        PaymentPeriod updated = getUpdated();
        perform(MockMvcRequestBuilders.put(REST_URL + PAYMENT_PERIOD_1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updated)))
                .andExpect(status().isNoContent());

        PAYMENT_PERIOD_MATCHER.assertMatch(service.get(PAYMENT_PERIOD_1_ID), updated);
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void updateIdNotConsistent() throws Exception {
        PaymentPeriod updated = getUpdated();
        updated.setId(PAYMENT_PERIOD_2_ID);
        perform(MockMvcRequestBuilders.put(REST_URL + POSITION_1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updated)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void updateNotFound() throws Exception {
        PaymentPeriod updated = getUpdated();
        updated.setId(null);
        perform(MockMvcRequestBuilders.put(REST_URL + NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updated)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(DATA_NOT_FOUND));
    }

    @Test
    void updateUnAuth() throws Exception {
        PaymentPeriod updated = getUpdated();
        perform(MockMvcRequestBuilders.put(REST_URL + POSITION_1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updated)))
                .andExpect(status().isUnauthorized())
                .andExpect(errorType(UNAUTHORIZED_ERROR));
    }

    @Test
    @WithUserDetails(value = DEPARTMENT_HEAD_MAIL)
    void updateForbiddenWhenDepartmentHead() throws Exception {
        PaymentPeriod updated = getUpdated();
        perform(MockMvcRequestBuilders.put(REST_URL + POSITION_1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updated)))
                .andExpect(status().isForbidden())
                .andExpect(errorType(ACCESS_DENIED_ERROR));
    }

    @Test
    @WithUserDetails(value = PERSONNEL_OFFICER_MAIL)
    void updateForbiddenWhenPersonnelOfficer() throws Exception {
        PaymentPeriod updated = getUpdated();
        perform(MockMvcRequestBuilders.put(REST_URL + POSITION_1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updated)))
                .andExpect(status().isForbidden())
                .andExpect(errorType(ACCESS_DENIED_ERROR));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void createInvalid() throws Exception {
        PaymentPeriod newPaymentPeriod = getNew();
        newPaymentPeriod.setRequiredHoursWorked(-10d);
        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newPaymentPeriod)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void updateInvalid() throws Exception {
        PaymentPeriod updated = getUpdated();
        updated.setRequiredHoursWorked(-10d);
        perform(MockMvcRequestBuilders.put(REST_URL + PAYMENT_PERIOD_1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updated)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR));
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    @WithUserDetails(value = ADMIN_MAIL)
    void createDuplicatePeriod() throws Exception {
        PaymentPeriod newDepartment = new PaymentPeriod(null, paymentPeriod1.getPeriod(), 150d);
        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newDepartment)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR))
                .andExpect(detailMessage(EXCEPTION_DUPLICATE_PAYMENT_PERIOD));
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    @WithUserDetails(value = ADMIN_MAIL)
    void updateDuplicatePeriod() throws Exception {
        PaymentPeriod updated = new PaymentPeriod(PAYMENT_PERIOD_1_ID, paymentPeriod2.getPeriod(), 150d);
        perform(MockMvcRequestBuilders.put(REST_URL + PAYMENT_PERIOD_1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updated)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR))
                .andExpect(detailMessage(EXCEPTION_DUPLICATE_PAYMENT_PERIOD));
    }
}