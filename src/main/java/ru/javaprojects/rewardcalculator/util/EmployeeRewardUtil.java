package ru.javaprojects.rewardcalculator.util;

import org.springframework.util.Assert;
import ru.javaprojects.rewardcalculator.model.DepartmentReward;
import ru.javaprojects.rewardcalculator.model.EmployeeReward;
import ru.javaprojects.rewardcalculator.to.EmployeeRewardTo;
import ru.javaprojects.rewardcalculator.util.exception.EmployeeRewardBadDataException;

public class EmployeeRewardUtil {
    private static final double PREMIUM_RATE = 0.3;
    private EmployeeRewardUtil() {
    }

    public static EmployeeReward updateFromTo(EmployeeReward employeeReward, EmployeeRewardTo employeeRewardTo, int hoursWorkedReward) {
        employeeReward.setHoursWorked(employeeRewardTo.getHoursWorked());
        employeeReward.setHoursWorkedReward(hoursWorkedReward);
        employeeReward.setAdditionalReward(employeeRewardTo.getAdditionalReward());
        employeeReward.setPenalty(employeeRewardTo.getPenalty());
        return employeeReward;
    }

    public static void checkToState(EmployeeRewardTo employeeRewardTo) {
        Assert.notNull(employeeRewardTo, "employeeRewardTo must not be null");
        Assert.notNull(employeeRewardTo.getHoursWorked(), "hours worked must not be null");
        Assert.notNull(employeeRewardTo.getAdditionalReward(), "additional reward must not be null");
        Assert.notNull(employeeRewardTo.getPenalty(), "penalty must not be null");
        Assert.isTrue(employeeRewardTo.getHoursWorked() >= 0, "hours worked must be greater than or equals to zero");
        Assert.isTrue(employeeRewardTo.getAdditionalReward() >= 0, "additional reward must be greater than or equals to zero");
        Assert.isTrue(employeeRewardTo.getPenalty() >= 0, "penalty must be greater than or equals to zero");
    }

    public static int calculateHoursWorkedReward(double hoursWorked, int salary, double requiredHoursWorked) {
        return (int) (hoursWorked * salary / requiredHoursWorked * PREMIUM_RATE);
    }

    public static int calculateFullReward(int hoursWorkedReward, int additionalReward, int penalty) {
        int fullReward = hoursWorkedReward + additionalReward - penalty;
        if (fullReward < 0) {
            throw new EmployeeRewardBadDataException("Employee reward must be greater than or equal zero");
        }
        return fullReward;
    }

    public static int calculateNewDistributedAmount(DepartmentReward departmentReward, int currentEmployeeFullReward, int newEmployeeFullReward) {
        int newDistributedAmount =  departmentReward.getDistributedAmount() - currentEmployeeFullReward + newEmployeeFullReward;
        if (newDistributedAmount > departmentReward.getAllocatedAmount()) {
            throw new EmployeeRewardBadDataException("Department reward allocated amount exceeded");
        }
        return newDistributedAmount;
    }
}