package ru.javaprojects.rewardcalculator.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.javaprojects.rewardcalculator.model.User;
import ru.javaprojects.rewardcalculator.service.UserService;
import ru.javaprojects.rewardcalculator.to.NewUserTo;
import ru.javaprojects.rewardcalculator.to.UserTo;
import ru.javaprojects.rewardcalculator.util.UserUtil;
import ru.javaprojects.rewardcalculator.util.exception.NotFoundException;
import ru.javaprojects.rewardcalculator.web.json.JsonUtil;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.javaprojects.rewardcalculator.DepartmentTestData.DEPARTMENT_3_ID;
import static ru.javaprojects.rewardcalculator.TestUtil.readFromJson;
import static ru.javaprojects.rewardcalculator.UserTestData.*;
import static ru.javaprojects.rewardcalculator.model.Role.ADMIN;
import static ru.javaprojects.rewardcalculator.model.Role.DEPARTMENT_HEAD;
import static ru.javaprojects.rewardcalculator.util.exception.ErrorType.DATA_NOT_FOUND;
import static ru.javaprojects.rewardcalculator.util.exception.ErrorType.VALIDATION_ERROR;
import static ru.javaprojects.rewardcalculator.web.AppExceptionHandler.EXCEPTION_DUPLICATE_EMAIL;

class UserRestControllerTest extends AbstractControllerTest {
    private static final String REST_URL = UserRestController.REST_URL + '/';

    @Autowired
    private UserService userService;

    @Test
    void getAll() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(USER_MATCHER.contentJson(admin, user));
    }

    @Test
    void get() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + USER_ID))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(USER_MATCHER.contentJson(user));
    }

    @Test
    void getNotFound() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + NOT_FOUND))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(DATA_NOT_FOUND));
    }

    @Test
    void createWithLocation() throws Exception {
        NewUserTo newUserTo = getNewToWithManagedDepartmentsId();
        ResultActions action = perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPassword(newUserTo, "newPass")))
                .andExpect(status().isCreated());

        User created = readFromJson(action, User.class);
        int newId = created.id();
        User newUser = getNewWithManagedDepartments();
        newUser.setId(newId);
        USER_MATCHER.assertMatch(created, newUser);
        USER_MATCHER.assertMatch(userService.get(newId), newUser);
    }

    @Test
    void delete() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + USER_ID))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertThrows(NotFoundException.class, () -> userService.get(USER_ID));
    }

    @Test
    void deleteNotFound() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + NOT_FOUND))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(DATA_NOT_FOUND));
    }


    @Test
    void update() throws Exception {
        UserTo updatedTo = getUpdatedTo();
        perform(MockMvcRequestBuilders.put(REST_URL + USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedTo)))
                .andDo(print())
                .andExpect(status().isNoContent());

        USER_MATCHER.assertMatch(userService.get(USER_ID), getUpdated());
    }

    @Test
    void updateNotFound() throws Exception {
        UserTo updatedTo = getUpdatedTo();
        updatedTo.setId(null);
        perform(MockMvcRequestBuilders.put(REST_URL + NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedTo)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(DATA_NOT_FOUND));
    }


    @Test
    void enable() throws Exception {
        perform(MockMvcRequestBuilders.patch(REST_URL + USER_ID)
                .param("enabled", "false")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());

        assertFalse(userService.get(USER_ID).isEnabled());
    }

    @Test
    void enableNotFound() throws Exception {
        perform(MockMvcRequestBuilders.patch(REST_URL + NOT_FOUND)
                .param("enabled", "false")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(DATA_NOT_FOUND));
    }


    @Test
    void changePassword() throws Exception {
        perform(MockMvcRequestBuilders.patch(REST_URL + USER_ID + "/password")
                .param("password", "newPassword"))
                .andExpect(status().isNoContent());
    }

    @Test
    void changePasswordNotFound() throws Exception {
        perform(MockMvcRequestBuilders.patch(REST_URL + NOT_FOUND + "/password")
                .param("password", "newPassword"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(DATA_NOT_FOUND));
    }

    @Test
    void changePasswordInvalid() throws Exception {
        perform(MockMvcRequestBuilders.patch(REST_URL + USER_ID + "/password")
                .param("password", "1234"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR));

    }

    @Test
    void createInvalid() throws Exception {
        NewUserTo newUserTo = getNewTo();
        newUserTo.setName(" ");
        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPassword(newUserTo, "newPass")))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR));
    }

    @Test
    void updateInvalid() throws Exception {
        UserTo updatedTo = getUpdatedTo();
        updatedTo.setRoles(Set.of());
        perform(MockMvcRequestBuilders.put(REST_URL + USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedTo)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR));
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void createDuplicateEmail() throws Exception {
        NewUserTo newUserTo = new NewUserTo(null, "NewName", user.getEmail(), "newPass", true, Set.of(DEPARTMENT_HEAD), Set.of());
        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPassword(newUserTo, "newPass")))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR))
                .andExpect(detailMessage(EXCEPTION_DUPLICATE_EMAIL));
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void updateDuplicateEmail() throws Exception {
        UserTo updatedTo = new UserTo(null, "Updated", admin.getEmail(), true, Set.of(DEPARTMENT_HEAD, ADMIN), Set.of(DEPARTMENT_3_ID));
        perform(MockMvcRequestBuilders.put(REST_URL + USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedTo)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR))
                .andExpect(detailMessage(EXCEPTION_DUPLICATE_EMAIL));
    }
}