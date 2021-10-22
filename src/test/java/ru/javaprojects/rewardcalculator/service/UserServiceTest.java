package ru.javaprojects.rewardcalculator.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import ru.javaprojects.rewardcalculator.testdata.UserTestData;
import ru.javaprojects.rewardcalculator.model.User;
import ru.javaprojects.rewardcalculator.to.NewUserTo;
import ru.javaprojects.rewardcalculator.to.UserTo;
import ru.javaprojects.rewardcalculator.util.exception.NotFoundException;

import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static ru.javaprojects.rewardcalculator.testdata.UserTestData.*;
import static ru.javaprojects.rewardcalculator.model.Role.DEPARTMENT_HEAD;

class UserServiceTest extends AbstractServiceTest {

    @Autowired
    private UserService service;

    @Test
    void create() {
        User created = service.create(getNewTo());
        int newId = created.id();
        User newUser = getNew();
        newUser.setId(newId);
        USER_MATCHER.assertMatch(created, newUser);
        USER_MATCHER.assertMatch(service.get(newId), newUser);
    }

    @Test
    void createWithManagedDepartments() {
        User created = service.create(getNewToWithManagedDepartmentsId());
        int newId = created.id();
        User newUser = getNewWithManagedDepartments();
        newUser.setId(newId);
        USER_MATCHER.assertMatch(created, newUser);
        USER_MATCHER.assertMatch(service.get(newId), newUser);
    }

    @Test
    void createWithNotExistedManagedDepartments() {
        NewUserTo newUserTo = getNewTo();
        newUserTo.setManagedDepartmentsId(Set.of(NOT_FOUND));
        assertThrows(NotFoundException.class, () -> service.create(newUserTo));
    }

    @Test
    void duplicateEmailCreate() {
        assertThrows(DataAccessException.class, () -> service.create(new NewUserTo(null, "newName", departmentHead.getEmail(), "newPass", true, Set.of(DEPARTMENT_HEAD), Set.of())));
    }

    @Test
    void get() {
        User user = service.get(ADMIN_ID);
        USER_MATCHER.assertMatch(user, admin);

        user = service.get(DEPARTMENT_HEAD_ID);
        USER_MATCHER.assertMatch(user, UserTestData.departmentHead);
    }

    @Test
    void getNotFound() {
        assertThrows(NotFoundException.class, () -> service.get(NOT_FOUND));
    }

    @Test
    void getByEmail() {
        User user = service.getByEmail(UserTestData.departmentHead.getEmail());
        USER_MATCHER.assertMatch(user, UserTestData.departmentHead);
    }

    @Test
    void getByEmailNotFound() {
        assertThrows(NotFoundException.class, () -> service.getByEmail("notExistedEmail@yandex.ru"));
    }

    @Test
    void getAll() {
        List<User> users = service.getAll();
        USER_MATCHER.assertMatch(users, admin, departmentHead, economist, personnelOfficer);
    }

    @Test
    void delete() {
        service.delete(DEPARTMENT_HEAD_ID);
        assertThrows(NotFoundException.class, () -> service.get(DEPARTMENT_HEAD_ID));
    }

    @Test
    void deleteNotFound() {
        assertThrows(NotFoundException.class, () -> service.delete(NOT_FOUND));
    }

    @Test
    void update() {
        service.update(getUpdatedTo());
        USER_MATCHER.assertMatch(service.get(DEPARTMENT_HEAD_ID), getUpdated());
    }

    @Test
    void updateNotFound() {
        UserTo updated = getUpdatedTo();
        updated.setId(NOT_FOUND);
        assertThrows(NotFoundException.class, () -> service.update(updated));
    }

    @Test
    void createWithException() {
        validateRootCause(ConstraintViolationException.class, () -> service.create(new NewUserTo(null, "  ", "mail@yandex.ru", "password", true, Set.of(DEPARTMENT_HEAD), Set.of())));
        validateRootCause(ConstraintViolationException.class, () -> service.create(new NewUserTo(null, "User", "  ", "password", true, Set.of(DEPARTMENT_HEAD), Set.of())));
        NewUserTo newUserTo = getNewTo();
        newUserTo.setRoles(Set.of());
        validateRootCause(ConstraintViolationException.class, () -> service.create(newUserTo));
    }

    @Test
    void enable() {
        service.enable(DEPARTMENT_HEAD_ID, false);
        assertFalse(service.get(DEPARTMENT_HEAD_ID).isEnabled());
        service.enable(DEPARTMENT_HEAD_ID, true);
        assertTrue(service.get(DEPARTMENT_HEAD_ID).isEnabled());
    }

    @Test
    void enableNotFound() {
        assertThrows(NotFoundException.class, () -> service.enable(NOT_FOUND, false));
    }

    @Test
    void changePassword() {
        User user = service.get(DEPARTMENT_HEAD_ID);
        service.changePassword(DEPARTMENT_HEAD_ID, "newPassword");
        assertNotEquals(user.getPassword(), service.get(DEPARTMENT_HEAD_ID).getPassword());
    }

    @Test
    void changePasswordNotFound() {
        assertThrows(NotFoundException.class, () -> service.changePassword(NOT_FOUND, "newPassword"));
    }
}