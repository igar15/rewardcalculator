package ru.javaprojects.rewardcalculator.testdata;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.javaprojects.rewardcalculator.TestMatcher;
import ru.javaprojects.rewardcalculator.model.DepartmentReward;
import ru.javaprojects.rewardcalculator.to.DepartmentRewardTo;

import java.util.List;

import static ru.javaprojects.rewardcalculator.model.AbstractBaseEntity.START_SEQ;
import static ru.javaprojects.rewardcalculator.testdata.DepartmentTestData.DEPARTMENT_1_ID;
import static ru.javaprojects.rewardcalculator.testdata.DepartmentTestData.department1;
import static ru.javaprojects.rewardcalculator.testdata.PaymentPeriodTestData.*;

public class DepartmentRewardTestData {
    public static final TestMatcher<DepartmentReward> DEPARTMENT_REWARD_MATCHER = TestMatcher.usingIgnoringFieldsComparator(DepartmentReward.class, "department", "paymentPeriod");

    public static final int DEPARTMENT_REWARD_1_ID = START_SEQ + 18;
    public static final int DEPARTMENT_REWARD_2_ID = START_SEQ + 19;
    public static final int DEPARTMENT_REWARD_ANOTHER_DEPARTMENT_ID = START_SEQ + 20;
    public static final int NOT_FOUND = 10;

    public static final DepartmentReward departmentReward1 = new DepartmentReward(DEPARTMENT_REWARD_1_ID, 40800, 12060);
    public static final DepartmentReward departmentReward2 = new DepartmentReward(DEPARTMENT_REWARD_2_ID, 40800, 34830, department1, paymentPeriod2);

    public static final Pageable PAGEABLE = PageRequest.of(0, 2);
    public static final Page<DepartmentReward> PAGE = new PageImpl<>(List.of(departmentReward2, departmentReward1), PAGEABLE, 2);

    public static final DepartmentReward departmentReward2Updated = new DepartmentReward(DEPARTMENT_REWARD_2_ID, 40800, 32770);

    public static DepartmentReward getNew() {
        return new DepartmentReward(null, 100000, 0, department1, paymentPeriod3);
    }

    public static DepartmentRewardTo getNewTo() {
        return new DepartmentRewardTo(null, DEPARTMENT_1_ID, PAYMENT_PERIOD_3_ID, 100000);
    }

    public static DepartmentReward getUpdated() {
        return new DepartmentReward(DEPARTMENT_REWARD_1_ID, 55000, 12060, department1, paymentPeriod3);
    }

    public static DepartmentRewardTo getUpdatedTo() {
        return new DepartmentRewardTo(DEPARTMENT_REWARD_1_ID, DEPARTMENT_1_ID, PAYMENT_PERIOD_3_ID, 55000);
    }
}