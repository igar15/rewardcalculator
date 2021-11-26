package ru.javaprojects.rewardcalculator.testdata;

import ru.javaprojects.rewardcalculator.TestMatcher;
import ru.javaprojects.rewardcalculator.model.Employee;
import ru.javaprojects.rewardcalculator.to.EmployeeTo;

import static ru.javaprojects.rewardcalculator.model.AbstractBaseEntity.START_SEQ;
import static ru.javaprojects.rewardcalculator.model.Rate.FULL_RATE;
import static ru.javaprojects.rewardcalculator.testdata.PositionTestData.*;

public class EmployeeTestData {
    public static final TestMatcher<Employee> EMPLOYEE_MATCHER = TestMatcher.usingIgnoringFieldsComparator(Employee.class, "position");

    public static final int EMPLOYEE_1_ID = START_SEQ + 11;
    public static final int EMPLOYEE_2_ID = START_SEQ + 12;
    public static final int EMPLOYEE_3_ID = START_SEQ + 13;
    public static final int EMPLOYEE_ANOTHER_DEPARTMENT_ID = START_SEQ + 14;
    public static final int NOT_FOUND = 10;

    public static final int FIRED_EMPLOYEE_1_ID = START_SEQ + 30;
    public static final int FIRED_EMPLOYEE_2_ID = START_SEQ + 31;
    public static final int FIRED_EMPLOYEE_3_ID = START_SEQ + 32;

    public static final Employee employee1 = new Employee(EMPLOYEE_1_ID, "employee 1 name", FULL_RATE, position1);
    public static final Employee employee2 = new Employee(EMPLOYEE_2_ID, "employee 2 name", FULL_RATE, position1);
    public static final Employee employee3 = new Employee(EMPLOYEE_3_ID, "employee 3 name", FULL_RATE, position2);

    public static final Employee firedEmployee1 = new Employee(FIRED_EMPLOYEE_1_ID, "employee 1 name", FULL_RATE, position1);
    public static final Employee firedEmployee2 = new Employee(FIRED_EMPLOYEE_2_ID, "employee 2 name", FULL_RATE, position1);
    public static final Employee firedEmployee3 = new Employee(FIRED_EMPLOYEE_3_ID, "employee 3 name", FULL_RATE, position2);

    static {
        firedEmployee1.setFired(true);
        firedEmployee2.setFired(true);
        firedEmployee3.setFired(true);
    }

    public static Employee getNew() {
        return new Employee(null, "NewName", FULL_RATE, position1);
    }

    public static EmployeeTo getNewTo() {
        return new EmployeeTo(null, "NewName", FULL_RATE, POSITION_1_ID);
    }

    public static Employee getUpdated() {
        return new Employee(EMPLOYEE_1_ID, "UpdatedName", FULL_RATE, position1);
    }

    public static EmployeeTo getUpdatedTo() {
        return new EmployeeTo(EMPLOYEE_1_ID, "UpdatedName", FULL_RATE, POSITION_1_ID);
    }
}