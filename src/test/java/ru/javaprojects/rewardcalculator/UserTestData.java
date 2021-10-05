package ru.javaprojects.rewardcalculator;

import ru.javaprojects.rewardcalculator.model.User;

import java.util.Collections;
import java.util.Date;
import java.util.Set;

import static ru.javaprojects.rewardcalculator.model.AbstractBaseEntity.START_SEQ;
import static ru.javaprojects.rewardcalculator.model.Role.*;

public class UserTestData {
    public static final TestMatcher<User> USER_MATCHER = TestMatcher.usingIgnoringFieldsComparator(User.class, "registered");

    public static final int USER_ID = START_SEQ;
    public static final int ADMIN_ID = START_SEQ + 1;
    public static final int NOT_FOUND = 10;

    public static final String USER_MAIL = "user@yandex.ru";
    public static final String ADMIN_MAIL = "admin@gmail.com";

    public static final User user = new User(USER_ID, "User", "user@yandex.ru", "password", DEPARTMENT_HEAD);
    public static final User admin = new User(ADMIN_ID, "Admin", "admin@gmail.com", "admin", ADMIN, DEPARTMENT_HEAD, ECONOMIST);

    public static User getNew() {
        return new User(null, "NewName", "new@gmail.com", "newPass", false, new Date(), Collections.singleton(DEPARTMENT_HEAD));
    }

    public static User getUpdated() {
        return new User(USER_ID, "UpdatedName", "update@gmail.com", "updatedPass", false, new Date(), Set.of(ADMIN, ECONOMIST));
    }
}