package ru.javaprojects.rewardcalculator;

import ru.javaprojects.rewardcalculator.model.EmployeeReward;

import static ru.javaprojects.rewardcalculator.DepartmentRewardTestData.*;
import static ru.javaprojects.rewardcalculator.EmployeeTestData.*;
import static ru.javaprojects.rewardcalculator.model.AbstractBaseEntity.START_SEQ;

public class EmployeeRewardTestData {
    public static final TestMatcher<EmployeeReward> EMPLOYEE_REWARD_MATCHER = TestMatcher.usingIgnoringFieldsComparator(EmployeeReward.class, "employee", "departmentReward");

    public static final int EMPLOYEE_REWARD_1_ID = START_SEQ + 17;
    public static final int EMPLOYEE_REWARD_2_ID = START_SEQ + 18;
    public static final int EMPLOYEE_REWARD_3_ID = START_SEQ + 19;
    public static final int NOT_FOUND = 10;

    public static final EmployeeReward employeeReward1 = new EmployeeReward(EMPLOYEE_REWARD_1_ID, 150.75, 12060, 0, 0);
    public static final EmployeeReward employeeReward2 = new EmployeeReward(EMPLOYEE_REWARD_2_ID, 150.75, 10710, 0, 0);
    public static final EmployeeReward employeeReward3 = new EmployeeReward(EMPLOYEE_REWARD_3_ID, 150.75, 18030, 0, 0);

    public static EmployeeReward getNew() {
        return new EmployeeReward(null, 120d, 12060, 0, 0);
    }

    public static EmployeeReward getNewWithEmployeeAndDepartmentReward() {
        return new EmployeeReward(null, 120d, 12060, 0, 0, employee1, departmentReward1);
    }

    public static EmployeeReward getUpdated() {
        return new EmployeeReward(EMPLOYEE_REWARD_1_ID, 100d, 10050, 0, 0);
    }

    public static EmployeeReward getUpdatedWithEmployeeAndDepartmentReward() {
        return new EmployeeReward(EMPLOYEE_REWARD_1_ID, 100d, 10050, 0, 0, employee1, departmentReward2);
    }
}