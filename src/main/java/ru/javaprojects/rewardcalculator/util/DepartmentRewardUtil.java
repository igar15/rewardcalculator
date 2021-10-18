package ru.javaprojects.rewardcalculator.util;

import ru.javaprojects.rewardcalculator.model.DepartmentReward;
import ru.javaprojects.rewardcalculator.to.DepartmentRewardTo;

public class DepartmentRewardUtil {
    private DepartmentRewardUtil() {
    }

    public static DepartmentReward createFromTo(DepartmentRewardTo departmentRewardTo) {
        return new DepartmentReward(null, departmentRewardTo.getAllocatedAmount());
    }

    public static DepartmentReward updateFromTo(DepartmentReward departmentReward, DepartmentRewardTo departmentRewardTo) {
        departmentReward.setAllocatedAmount(departmentRewardTo.getAllocatedAmount());
        return departmentReward;
    }
}