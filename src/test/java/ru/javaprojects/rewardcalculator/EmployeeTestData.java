package ru.javaprojects.rewardcalculator;

import ru.javaprojects.rewardcalculator.model.Employee;

import static ru.javaprojects.rewardcalculator.PositionTestData.position1;
import static ru.javaprojects.rewardcalculator.model.AbstractBaseEntity.START_SEQ;

public class EmployeeTestData {
    public static final TestMatcher<Employee> EMPLOYEE_MATCHER = TestMatcher.usingIgnoringFieldsComparator(Employee.class, "position");

    public static final int EMPLOYEE_1_ID = START_SEQ + 8;
    public static final int EMPLOYEE_2_ID = START_SEQ + 9;
    public static final int EMPLOYEE_3_ID = START_SEQ + 10;
    public static final int NOT_FOUND = 10;

    public static final Employee employee1 = new Employee(EMPLOYEE_1_ID, "employee 1 name");
    public static final Employee employee2 = new Employee(EMPLOYEE_2_ID, "employee 2 name");
    public static final Employee employee3 = new Employee(EMPLOYEE_3_ID, "employee 3 name");

    public static Employee getNew() {
        return new Employee(null, "NewName");
    }

    public static Employee getNewWithPosition() {
        return new Employee(null, "NewName", position1);
    }

    public static Employee getUpdated() {
        return new Employee(EMPLOYEE_1_ID, "UpdatedName");
    }

    public static Employee getUpdatedWithPosition() {
        return new Employee(EMPLOYEE_1_ID, "UpdatedName", position1);
    }
}