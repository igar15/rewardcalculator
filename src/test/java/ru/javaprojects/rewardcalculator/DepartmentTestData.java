package ru.javaprojects.rewardcalculator;

import ru.javaprojects.rewardcalculator.model.Department;

import static ru.javaprojects.rewardcalculator.model.AbstractBaseEntity.START_SEQ;

public class DepartmentTestData {
    public static final TestMatcher<Department> DEPARTMENT_MATCHER = TestMatcher.usingIgnoringFieldsComparator(Department.class);

    public static final int DEPARTMENT_1_ID = START_SEQ + 2;
    public static final int DEPARTMENT_2_ID = START_SEQ + 3;
    public static final int DEPARTMENT_3_ID = START_SEQ + 4;
    public static final int NOT_FOUND = 10;

    public static final Department department1 = new Department(DEPARTMENT_1_ID, "Отдел № 1");
    public static final Department department2 = new Department(DEPARTMENT_2_ID, "Отдел № 3");
    public static final Department department3 = new Department(DEPARTMENT_3_ID, "Отдел № 2");

    public static Department getNew() {
        return new Department(null, "NewDepartment");
    }

    public static Department getUpdated() {
        return new Department(DEPARTMENT_1_ID, "UpdatedName");
    }
}