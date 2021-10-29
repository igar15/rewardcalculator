package ru.javaprojects.rewardcalculator.testdata;

import ru.javaprojects.rewardcalculator.TestMatcher;
import ru.javaprojects.rewardcalculator.model.Employee;
import ru.javaprojects.rewardcalculator.to.EmployeeTo;

import static ru.javaprojects.rewardcalculator.model.AbstractBaseEntity.START_SEQ;
import static ru.javaprojects.rewardcalculator.testdata.PositionTestData.*;

public class EmployeeTestData {
    public static final TestMatcher<Employee> EMPLOYEE_MATCHER = TestMatcher.usingIgnoringFieldsComparator(Employee.class, "position");

    public static final int EMPLOYEE_1_ID = START_SEQ + 11;
    public static final int EMPLOYEE_2_ID = START_SEQ + 12;
    public static final int EMPLOYEE_3_ID = START_SEQ + 13;
    public static final int EMPLOYEE_ANOTHER_DEPARTMENT_ID = START_SEQ + 14;
    public static final int NOT_FOUND = 10;

    public static final Employee employee1 = new Employee(EMPLOYEE_1_ID, "employee 1 name", position1);
    public static final Employee employee2 = new Employee(EMPLOYEE_2_ID, "employee 2 name", position1);
    public static final Employee employee3 = new Employee(EMPLOYEE_3_ID, "employee 3 name", position2);

    public static Employee getNew() {
        return new Employee(null, "NewName", position1);
    }

    public static EmployeeTo getNewTo() {
        return new EmployeeTo(null, "NewName", POSITION_1_ID);
    }

    public static Employee getUpdated() {
        return new Employee(EMPLOYEE_1_ID, "UpdatedName", position1);
    }

    public static EmployeeTo getUpdatedTo() {
        return new EmployeeTo(EMPLOYEE_1_ID, "UpdatedName", POSITION_1_ID);
    }
}