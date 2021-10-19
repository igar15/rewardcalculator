package ru.javaprojects.rewardcalculator.testdata;

import ru.javaprojects.rewardcalculator.TestMatcher;
import ru.javaprojects.rewardcalculator.model.User;
import ru.javaprojects.rewardcalculator.to.NewUserTo;
import ru.javaprojects.rewardcalculator.to.UserTo;
import ru.javaprojects.rewardcalculator.web.json.JsonUtil;

import java.util.HashSet;
import java.util.Set;

import static ru.javaprojects.rewardcalculator.testdata.DepartmentTestData.*;
import static ru.javaprojects.rewardcalculator.model.AbstractBaseEntity.START_SEQ;
import static ru.javaprojects.rewardcalculator.model.Role.*;

public class UserTestData {
    public static final TestMatcher<User> USER_MATCHER = TestMatcher.usingIgnoringFieldsComparator(User.class, "registered", "password");

    public static final int USER_ID = START_SEQ;
    public static final int ADMIN_ID = START_SEQ + 1;
    public static final int NOT_FOUND = 10;

    public static final String USER_MAIL = "user@yandex.ru";
    public static final String ADMIN_MAIL = "admin@gmail.com";

    public static final User user = new User(USER_ID, "User", "user@yandex.ru", "password", true, Set.of(DEPARTMENT_HEAD), Set.of(department1, department2));
    public static final User admin = new User(ADMIN_ID, "Admin", "admin@gmail.com", "admin", true, Set.of(ADMIN, DEPARTMENT_HEAD, ECONOMIST), Set.of());

    public static User getNew() {
        return new User(null, "NewName", "new@gmail.com", "newPass", false, Set.of(DEPARTMENT_HEAD), new HashSet<>());
    }

    public static NewUserTo getNewTo() {
        return new NewUserTo(null, "NewName", "new@gmail.com", "newPass", false, Set.of(DEPARTMENT_HEAD), Set.of());
    }

    public static User getNewWithManagedDepartments() {
        User user = getNew();
        user.addManagedDepartment(department1);
        user.addManagedDepartment(department2);
        return user;
    }

    public static NewUserTo getNewToWithManagedDepartmentsId() {
        NewUserTo newUserTo = getNewTo();
        newUserTo.setManagedDepartmentsId(Set.of(DEPARTMENT_1_ID, DEPARTMENT_2_ID));
        return newUserTo;
    }

    public static User getUpdated() {
        return new User(USER_ID, "UpdatedName", "update@gmail.com", false, Set.of(ADMIN, ECONOMIST), Set.of(department3));
    }

    public static UserTo getUpdatedTo() {
        return new UserTo(USER_ID, "UpdatedName", "update@gmail.com", false, Set.of(ADMIN, ECONOMIST), Set.of(DEPARTMENT_3_ID));
    }

    public static String jsonWithPassword(NewUserTo newUserTo, String password) {
        return JsonUtil.writeAdditionProps(newUserTo, "password", password);
    }
}