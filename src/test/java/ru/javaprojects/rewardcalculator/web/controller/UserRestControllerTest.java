package ru.javaprojects.rewardcalculator.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.javaprojects.rewardcalculator.model.Role;
import ru.javaprojects.rewardcalculator.model.User;
import ru.javaprojects.rewardcalculator.service.UserService;
import ru.javaprojects.rewardcalculator.to.NewUserTo;
import ru.javaprojects.rewardcalculator.to.UserTo;
import ru.javaprojects.rewardcalculator.util.exception.NotFoundException;
import ru.javaprojects.rewardcalculator.web.json.JsonUtil;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.javaprojects.rewardcalculator.TestUtil.readFromJson;
import static ru.javaprojects.rewardcalculator.model.Role.ADMIN;
import static ru.javaprojects.rewardcalculator.model.Role.DEPARTMENT_HEAD;
import static ru.javaprojects.rewardcalculator.testdata.DepartmentTestData.DEPARTMENT_3_ID;
import static ru.javaprojects.rewardcalculator.testdata.UserTestData.*;
import static ru.javaprojects.rewardcalculator.util.exception.ErrorType.*;
import static ru.javaprojects.rewardcalculator.web.AppExceptionHandler.EXCEPTION_DUPLICATE_EMAIL;

class UserRestControllerTest extends AbstractControllerTest {
    private static final String REST_URL = UserRestController.REST_URL + '/';

    @Autowired
    private UserService userService;

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void getAll() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(USER_MATCHER.contentJson(personnelOfficer, departmentHead, economist, admin));
    }

    @Test
    void getAllUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isUnauthorized())
                .andExpect(errorType(UNAUTHORIZED_ERROR));
    }

    @Test
    @WithUserDetails(value = DEPARTMENT_HEAD_MAIL)
    void getAllForbiddenWhenDepartmentHead() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isForbidden())
                .andExpect(errorType(ACCESS_DENIED_ERROR));
    }

    @Test
    @WithUserDetails(value = ECONOMIST_MAIL)
    void getAllForbiddenWhenEconomist() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isForbidden())
                .andExpect(errorType(ACCESS_DENIED_ERROR));
    }

    @Test
    @WithUserDetails(value = PERSONNEL_OFFICER_MAIL)
    void getAllForbiddenWhenPersonnelOfficer() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isForbidden())
                .andExpect(errorType(ACCESS_DENIED_ERROR));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void getAllByKeyWord() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "by")
                .param("keyWord", "admin"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(USER_MATCHER.contentJson(List.of(admin)));
    }

    @Test
    void getAllByKeyWordUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "by")
                .param("keyWord", "admin"))
                .andExpect(status().isUnauthorized())
                .andExpect(errorType(UNAUTHORIZED_ERROR));
    }

    @Test
    @WithUserDetails(value = DEPARTMENT_HEAD_MAIL)
    void getAllByKeyWordForbiddenWhenDepartmentHead() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "by")
                .param("keyWord", "admin"))
                .andExpect(status().isForbidden())
                .andExpect(errorType(ACCESS_DENIED_ERROR));
    }

    @Test
    @WithUserDetails(value = ECONOMIST_MAIL)
    void getAllByKeyWordForbiddenWhenEconomist() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "by")
                .param("keyWord", "admin"))
                .andExpect(status().isForbidden())
                .andExpect(errorType(ACCESS_DENIED_ERROR));
    }

    @Test
    @WithUserDetails(value = PERSONNEL_OFFICER_MAIL)
    void getAllByKeyWordForbiddenWhenPersonnelOfficer() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "by")
                .param("keyWord", "admin"))
                .andExpect(status().isForbidden())
                .andExpect(errorType(ACCESS_DENIED_ERROR));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void get() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + DEPARTMENT_HEAD_ID))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(USER_MATCHER.contentJson(departmentHead));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void getNotFound() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + NOT_FOUND))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(DATA_NOT_FOUND));
    }

    @Test
    void getUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + DEPARTMENT_HEAD_ID))
                .andExpect(status().isUnauthorized())
                .andExpect(errorType(UNAUTHORIZED_ERROR));
    }

    @Test
    @WithUserDetails(value = DEPARTMENT_HEAD_MAIL)
    void getForbiddenWhenDepartmentHead() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + DEPARTMENT_HEAD_ID))
                .andExpect(status().isForbidden())
                .andExpect(errorType(ACCESS_DENIED_ERROR));
    }

    @Test
    @WithUserDetails(value = ECONOMIST_MAIL)
    void getForbiddenWhenEconomist() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + DEPARTMENT_HEAD_ID))
                .andExpect(status().isForbidden())
                .andExpect(errorType(ACCESS_DENIED_ERROR));
    }

    @Test
    @WithUserDetails(value = PERSONNEL_OFFICER_MAIL)
    void getForbiddenWhenPersonnelOfficer() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + DEPARTMENT_HEAD_ID))
                .andExpect(status().isForbidden())
                .andExpect(errorType(ACCESS_DENIED_ERROR));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
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
    @WithUserDetails(value = ADMIN_MAIL)
    void createWithManagedDepartmentsWhenNotDepartmentHeadWithLocation() throws Exception {
        NewUserTo newUserTo = getNewToWithManagedDepartmentsId();
        newUserTo.setRoles(Set.of(Role.ECONOMIST));
        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPassword(newUserTo, "newPass")))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR));
    }

    @Test
    void createUnAuth() throws Exception {
        NewUserTo newUserTo = getNewToWithManagedDepartmentsId();
        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPassword(newUserTo, "newPass")))
                .andExpect(status().isUnauthorized())
                .andExpect(errorType(UNAUTHORIZED_ERROR));
    }

    @Test
    @WithUserDetails(value = DEPARTMENT_HEAD_MAIL)
    void createForbiddenWhenDepartmentHead() throws Exception {
        NewUserTo newUserTo = getNewToWithManagedDepartmentsId();
        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPassword(newUserTo, "newPass")))
                .andExpect(status().isForbidden())
                .andExpect(errorType(ACCESS_DENIED_ERROR));
    }

    @Test
    @WithUserDetails(value = ECONOMIST_MAIL)
    void createForbiddenWhenEconomist() throws Exception {
        NewUserTo newUserTo = getNewToWithManagedDepartmentsId();
        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPassword(newUserTo, "newPass")))
                .andExpect(status().isForbidden())
                .andExpect(errorType(ACCESS_DENIED_ERROR));
    }

    @Test
    @WithUserDetails(value = PERSONNEL_OFFICER_MAIL)
    void createForbiddenWhenPersonnelOfficer() throws Exception {
        NewUserTo newUserTo = getNewToWithManagedDepartmentsId();
        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPassword(newUserTo, "newPass")))
                .andExpect(status().isForbidden())
                .andExpect(errorType(ACCESS_DENIED_ERROR));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void delete() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + DEPARTMENT_HEAD_ID))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertThrows(NotFoundException.class, () -> userService.get(DEPARTMENT_HEAD_ID));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void deleteNotFound() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + NOT_FOUND))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(DATA_NOT_FOUND));
    }

    @Test
    void deleteUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + DEPARTMENT_HEAD_ID))
                .andExpect(status().isUnauthorized())
                .andExpect(errorType(UNAUTHORIZED_ERROR));
    }

    @Test
    @WithUserDetails(value = DEPARTMENT_HEAD_MAIL)
    void deleteForbiddenWhenDepartmentHead() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + DEPARTMENT_HEAD_ID))
                .andExpect(status().isForbidden())
                .andExpect(errorType(ACCESS_DENIED_ERROR));
    }

    @Test
    @WithUserDetails(value = ECONOMIST_MAIL)
    void deleteForbiddenWhenEconomist() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + DEPARTMENT_HEAD_ID))
                .andExpect(status().isForbidden())
                .andExpect(errorType(ACCESS_DENIED_ERROR));
    }

    @Test
    @WithUserDetails(value = PERSONNEL_OFFICER_MAIL)
    void deleteForbiddenWhenPersonnelOfficer() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + DEPARTMENT_HEAD_ID))
                .andExpect(status().isForbidden())
                .andExpect(errorType(ACCESS_DENIED_ERROR));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void update() throws Exception {
        UserTo updatedTo = getUpdatedTo();
        perform(MockMvcRequestBuilders.put(REST_URL + DEPARTMENT_HEAD_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedTo)))
                .andDo(print())
                .andExpect(status().isNoContent());

        USER_MATCHER.assertMatch(userService.get(DEPARTMENT_HEAD_ID), getUpdated());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void updateIdNotConsistent() throws Exception {
        UserTo updatedTo = getUpdatedTo();
        updatedTo.setId(ADMIN_ID);
        perform(MockMvcRequestBuilders.put(REST_URL + DEPARTMENT_HEAD_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedTo)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void updateWithManagedDepartmentsWhenNotDepartmentHead() throws Exception {
        UserTo updatedTo = getUpdatedTo();
        updatedTo.setRoles(Set.of(Role.ECONOMIST));
        perform(MockMvcRequestBuilders.put(REST_URL + DEPARTMENT_HEAD_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedTo)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
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
    void updateUnAuth() throws Exception {
        UserTo updatedTo = getUpdatedTo();
        perform(MockMvcRequestBuilders.put(REST_URL + DEPARTMENT_HEAD_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedTo)))
                .andExpect(status().isUnauthorized())
                .andExpect(errorType(UNAUTHORIZED_ERROR));
    }

    @Test
    @WithUserDetails(value = DEPARTMENT_HEAD_MAIL)
    void updateForbiddenWhenDepartmentHead() throws Exception {
        UserTo updatedTo = getUpdatedTo();
        perform(MockMvcRequestBuilders.put(REST_URL + DEPARTMENT_HEAD_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedTo)))
                .andExpect(status().isForbidden())
                .andExpect(errorType(ACCESS_DENIED_ERROR));
    }

    @Test
    @WithUserDetails(value = ECONOMIST_MAIL)
    void updateForbiddenWhenEconomist() throws Exception {
        UserTo updatedTo = getUpdatedTo();
        perform(MockMvcRequestBuilders.put(REST_URL + DEPARTMENT_HEAD_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedTo)))
                .andExpect(status().isForbidden())
                .andExpect(errorType(ACCESS_DENIED_ERROR));
    }

    @Test
    @WithUserDetails(value = PERSONNEL_OFFICER_MAIL)
    void updateForbiddenWhenPersonnelOfficer() throws Exception {
        UserTo updatedTo = getUpdatedTo();
        perform(MockMvcRequestBuilders.put(REST_URL + DEPARTMENT_HEAD_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedTo)))
                .andExpect(status().isForbidden())
                .andExpect(errorType(ACCESS_DENIED_ERROR));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void enable() throws Exception {
        perform(MockMvcRequestBuilders.patch(REST_URL + DEPARTMENT_HEAD_ID)
                .param("enabled", "false")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());

        assertFalse(userService.get(DEPARTMENT_HEAD_ID).isEnabled());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void enableNotFound() throws Exception {
        perform(MockMvcRequestBuilders.patch(REST_URL + NOT_FOUND)
                .param("enabled", "false")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(DATA_NOT_FOUND));
    }

    @Test
    void enableUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.patch(REST_URL + DEPARTMENT_HEAD_ID)
                .param("enabled", "false")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(errorType(UNAUTHORIZED_ERROR));
    }

    @Test
    @WithUserDetails(value = DEPARTMENT_HEAD_MAIL)
    void enableForbiddenWhenDepartmentHead() throws Exception {
        perform(MockMvcRequestBuilders.patch(REST_URL + DEPARTMENT_HEAD_ID)
                .param("enabled", "false")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(errorType(ACCESS_DENIED_ERROR));
    }

    @Test
    @WithUserDetails(value = ECONOMIST_MAIL)
    void enableForbiddenWhenEconomist() throws Exception {
        perform(MockMvcRequestBuilders.patch(REST_URL + DEPARTMENT_HEAD_ID)
                .param("enabled", "false")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(errorType(ACCESS_DENIED_ERROR));
    }

    @Test
    @WithUserDetails(value = PERSONNEL_OFFICER_MAIL)
    void enableForbiddenWhenPersonnelOfficer() throws Exception {
        perform(MockMvcRequestBuilders.patch(REST_URL + DEPARTMENT_HEAD_ID)
                .param("enabled", "false")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(errorType(ACCESS_DENIED_ERROR));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void changePassword() throws Exception {
        perform(MockMvcRequestBuilders.patch(REST_URL + DEPARTMENT_HEAD_ID + "/password")
                .param("password", "newPassword"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void changePasswordNotFound() throws Exception {
        perform(MockMvcRequestBuilders.patch(REST_URL + NOT_FOUND + "/password")
                .param("password", "newPassword"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(DATA_NOT_FOUND));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void changePasswordInvalid() throws Exception {
        perform(MockMvcRequestBuilders.patch(REST_URL + DEPARTMENT_HEAD_ID + "/password")
                .param("password", "1234"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR));

    }

    @Test
    void changePasswordUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.patch(REST_URL + DEPARTMENT_HEAD_ID + "/password")
                .param("password", "newPassword"))
                .andExpect(status().isUnauthorized())
                .andExpect(errorType(UNAUTHORIZED_ERROR));
    }

    @Test
    @WithUserDetails(value = DEPARTMENT_HEAD_MAIL)
    void changePasswordForbiddenWhenDepartmentHead() throws Exception {
        perform(MockMvcRequestBuilders.patch(REST_URL + DEPARTMENT_HEAD_ID + "/password")
                .param("password", "newPassword"))
                .andExpect(status().isForbidden())
                .andExpect(errorType(ACCESS_DENIED_ERROR));
    }

    @Test
    @WithUserDetails(value = ECONOMIST_MAIL)
    void changePasswordForbiddenWhenEconomist() throws Exception {
        perform(MockMvcRequestBuilders.patch(REST_URL + DEPARTMENT_HEAD_ID + "/password")
                .param("password", "newPassword"))
                .andExpect(status().isForbidden())
                .andExpect(errorType(ACCESS_DENIED_ERROR));
    }

    @Test
    @WithUserDetails(value = PERSONNEL_OFFICER_MAIL)
    void changePasswordForbiddenWhenPersonnelOfficer() throws Exception {
        perform(MockMvcRequestBuilders.patch(REST_URL + DEPARTMENT_HEAD_ID + "/password")
                .param("password", "newPassword"))
                .andExpect(status().isForbidden())
                .andExpect(errorType(ACCESS_DENIED_ERROR));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
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
    @WithUserDetails(value = ADMIN_MAIL)
    void updateInvalid() throws Exception {
        UserTo updatedTo = getUpdatedTo();
        updatedTo.setRoles(Set.of());
        perform(MockMvcRequestBuilders.put(REST_URL + DEPARTMENT_HEAD_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedTo)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR));
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    @WithUserDetails(value = ADMIN_MAIL)
    void createDuplicateEmail() throws Exception {
        NewUserTo newUserTo = new NewUserTo(null, "NewName", departmentHead.getEmail(), "newPass", true, Set.of(DEPARTMENT_HEAD), Set.of());
        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPassword(newUserTo, "newPass")))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(errorType(DATA_ERROR))
                .andExpect(detailMessage(EXCEPTION_DUPLICATE_EMAIL));
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    @WithUserDetails(value = ADMIN_MAIL)
    void updateDuplicateEmail() throws Exception {
        UserTo updatedTo = new UserTo(null, "Updated", admin.getEmail(), Set.of(DEPARTMENT_HEAD, ADMIN), Set.of(DEPARTMENT_3_ID));
        perform(MockMvcRequestBuilders.put(REST_URL + DEPARTMENT_HEAD_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedTo)))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(errorType(DATA_ERROR))
                .andExpect(detailMessage(EXCEPTION_DUPLICATE_EMAIL));
    }
}