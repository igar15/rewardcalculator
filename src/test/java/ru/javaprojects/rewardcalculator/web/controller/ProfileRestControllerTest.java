package ru.javaprojects.rewardcalculator.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.javaprojects.rewardcalculator.service.UserService;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.javaprojects.rewardcalculator.testdata.UserTestData.*;
import static ru.javaprojects.rewardcalculator.util.exception.ErrorType.*;
import static ru.javaprojects.rewardcalculator.web.AppExceptionHandler.EXCEPTION_DISABLED;
import static ru.javaprojects.rewardcalculator.web.AppExceptionHandler.EXCEPTION_INVALID_PASSWORD;
import static ru.javaprojects.rewardcalculator.web.controller.ProfileRestController.REST_URL;

class ProfileRestControllerTest extends AbstractControllerTest {

    @Autowired
    private UserService service;

    @Test
    void login() throws Exception {
        perform(MockMvcRequestBuilders.post(REST_URL + "/login")
                .param("email", departmentHead.getEmail())
                .param("password", departmentHead.getPassword()))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(USER_MATCHER.contentJson(departmentHead));
    }

    @Test
    void loginFailed() throws Exception {
        perform(MockMvcRequestBuilders.post(REST_URL + "/login")
                .param("email", departmentHead.getEmail())
                .param("password", "wrongPassword"))
                .andExpect(status().isBadRequest())
                .andExpect(errorType(BAD_CREDENTIALS_ERROR));
    }

    @Test
    void loginDisabled() throws Exception {
        service.enable(DEPARTMENT_HEAD_ID, false);

        perform(MockMvcRequestBuilders.post(REST_URL + "/login")
                .param("email", departmentHead.getEmail())
                .param("password", departmentHead.getPassword()))
                .andExpect(status().isForbidden())
                .andExpect(errorType(DISABLED_ERROR))
                .andExpect(detailMessage(EXCEPTION_DISABLED));
    }

    @Test
    @WithUserDetails(value = DEPARTMENT_HEAD_MAIL)
    void get() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(USER_MATCHER.contentJson(departmentHead));
    }

    @Test
    void getUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isUnauthorized())
                .andExpect(errorType(UNAUTHORIZED_ERROR));
    }

    @Test
    @WithUserDetails(value = DEPARTMENT_HEAD_MAIL)
    void changePassword() throws Exception {
        perform((MockMvcRequestBuilders.patch(REST_URL + "/password"))
                .param("password", "newPassword"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithUserDetails(value = DEPARTMENT_HEAD_MAIL)
    void changePasswordInvalid() throws Exception {
        perform((MockMvcRequestBuilders.patch(REST_URL + "/password"))
                .param("password", "new"))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR))
                .andExpect(detailMessage(EXCEPTION_INVALID_PASSWORD));
    }
}