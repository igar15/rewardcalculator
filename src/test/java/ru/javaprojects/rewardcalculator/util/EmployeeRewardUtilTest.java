package ru.javaprojects.rewardcalculator.util;

import org.junit.jupiter.api.Test;
import ru.javaprojects.rewardcalculator.util.exception.EmployeeRewardBadDataException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.javaprojects.rewardcalculator.testdata.DepartmentRewardTestData.departmentReward1;
import static ru.javaprojects.rewardcalculator.util.EmployeeRewardUtil.*;

class EmployeeRewardUtilTest {

    @Test
    void calculateHoursWorkedRewardTest() {
        assertEquals(10891, calculateHoursWorkedReward(120d, 53400, 176.5));
        assertEquals(7283, calculateHoursWorkedReward(80.25, 53400, 176.5));
        assertEquals(12629, calculateHoursWorkedReward(118.25, 53400, 150d));
    }

    @Test
    void calculateFullRewardTest() {
        assertEquals(21020, calculateFullReward(16020, 5000, 0));
        assertEquals(10000, calculateFullReward(16020, 0, 6020));
        assertThrows(EmployeeRewardBadDataException.class, () -> calculateFullReward(10000, 0, 12000));
    }

    @Test
    void calculateNewDistributedAmountTest() {
        int newDistributedAmount = calculateNewDistributedAmount(departmentReward1, 16020, 10020);
        assertEquals(34800, newDistributedAmount);
        assertThrows(EmployeeRewardBadDataException.class, () -> calculateNewDistributedAmount(departmentReward1, 16020, 20000));
    }
}