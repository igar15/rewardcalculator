package ru.javaprojects.rewardcalculator.testdata;

import ru.javaprojects.rewardcalculator.TestMatcher;
import ru.javaprojects.rewardcalculator.model.User;
import ru.javaprojects.rewardcalculator.to.NewUserTo;
import ru.javaprojects.rewardcalculator.to.UserTo;
import ru.javaprojects.rewardcalculator.web.json.JsonUtil;

import java.util.HashSet;
import java.util.Set;

import static ru.javaprojects.rewardcalculator.model.AbstractBaseEntity.START_SEQ;
import static ru.javaprojects.rewardcalculator.model.Role.*;
import static ru.javaprojects.rewardcalculator.testdata.DepartmentTestData.*;

public class UserTestData {
    public static final TestMatcher<User> USER_MATCHER = TestMatcher.usingIgnoringFieldsComparator(User.class, "registered", "password");

    public static final int DEPARTMENT_HEAD_ID = START_SEQ;
    public static final int ADMIN_ID = START_SEQ + 1;
    public static final int ECONOMIST_ID = START_SEQ + 2;
    public static final int PERSONNEL_OFFICER_ID = START_SEQ + 3;
    public static final int NOT_FOUND = 10;

    public static final String DEPARTMENT_HEAD_MAIL = "departmenthead@yandex.ru";
    public static final String ADMIN_MAIL = "admin@gmail.com";
    public static final String ECONOMIST_MAIL = "economist@yandex.ru";
    public static final String PERSONNEL_OFFICER_MAIL = "personnelofficer@yandex.ru";

    public static final User departmentHead = new User(DEPARTMENT_HEAD_ID, "Department head name", "departmenthead@yandex.ru", "password", true, Set.of(DEPARTMENT_HEAD), Set.of(department1, department2));
    public static final User admin = new User(ADMIN_ID, "Admin name", "admin@gmail.com", "admin", true, Set.of(ADMIN, DEPARTMENT_HEAD, ECONOMIST), Set.of());
    public static final User economist = new User(ECONOMIST_ID, "Economist name", "economist@yandex.ru", "password", true, Set.of(ECONOMIST), Set.of());
    public static final User personnelOfficer = new User(PERSONNEL_OFFICER_ID, "Personnel Officer name", "personnelofficer@yandex.ru", "password", true, Set.of(PERSONNEL_OFFICER), Set.of());


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
        return new User(DEPARTMENT_HEAD_ID, "UpdatedName", "update@gmail.com", false, Set.of(ADMIN, ECONOMIST), Set.of(department3));
    }

    public static UserTo getUpdatedTo() {
        return new UserTo(DEPARTMENT_HEAD_ID, "UpdatedName", "update@gmail.com", false, Set.of(ADMIN, ECONOMIST), Set.of(DEPARTMENT_3_ID));
    }

    public static String jsonWithPassword(NewUserTo newUserTo, String password) {
        return JsonUtil.writeAdditionProps(newUserTo, "password", password);
    }
}