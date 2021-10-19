package ru.javaprojects.rewardcalculator.testdata;

import ru.javaprojects.rewardcalculator.TestMatcher;
import ru.javaprojects.rewardcalculator.model.DepartmentReward;
import ru.javaprojects.rewardcalculator.to.DepartmentRewardTo;

import static ru.javaprojects.rewardcalculator.testdata.DepartmentTestData.DEPARTMENT_1_ID;
import static ru.javaprojects.rewardcalculator.testdata.DepartmentTestData.department1;
import static ru.javaprojects.rewardcalculator.testdata.PaymentPeriodTestData.*;
import static ru.javaprojects.rewardcalculator.model.AbstractBaseEntity.START_SEQ;

public class DepartmentRewardTestData {
    public static final TestMatcher<DepartmentReward> DEPARTMENT_REWARD_MATCHER = TestMatcher.usingIgnoringFieldsComparator(DepartmentReward.class, "department", "paymentPeriod");

    public static final int DEPARTMENT_REWARD_1_ID = START_SEQ + 14;
    public static final int DEPARTMENT_REWARD_2_ID = START_SEQ + 15;
    public static final int DEPARTMENT_REWARD_3_ID = START_SEQ + 16;
    public static final int NOT_FOUND = 10;

    public static final DepartmentReward departmentReward1 = new DepartmentReward(DEPARTMENT_REWARD_1_ID, 40800, 40800);
    public static final DepartmentReward departmentReward2 = new DepartmentReward(DEPARTMENT_REWARD_2_ID, 40800, 40800);
    public static final DepartmentReward departmentReward3 = new DepartmentReward(DEPARTMENT_REWARD_3_ID, 40800, 40800);

    public static final DepartmentReward departmentReward2Updated = new DepartmentReward(DEPARTMENT_REWARD_2_ID, 40800, 38740);

    public static DepartmentReward getNew() {
        return new DepartmentReward(null, 100000, 0, department1, paymentPeriod3);
    }

    public static DepartmentRewardTo getNewTo() {
        return new DepartmentRewardTo(null, DEPARTMENT_1_ID, PAYMENT_PERIOD_3_ID, 100000);
    }

    public static DepartmentReward getUpdated() {
        return new DepartmentReward(DEPARTMENT_REWARD_1_ID, 55000, 40800, department1, paymentPeriod3);
    }

    public static DepartmentRewardTo getUpdatedTo() {
        return new DepartmentRewardTo(DEPARTMENT_REWARD_1_ID, DEPARTMENT_1_ID, PAYMENT_PERIOD_3_ID, 55000);
    }
}