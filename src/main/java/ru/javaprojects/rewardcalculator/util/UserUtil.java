package ru.javaprojects.rewardcalculator.util;

import ru.javaprojects.rewardcalculator.model.User;
import ru.javaprojects.rewardcalculator.to.UserTo;

public class UserUtil {

    private UserUtil() {
    }

    public static User updateFromTo(User user, UserTo userTo) {
        user.setName(userTo.getName());
        user.setEmail(userTo.getEmail().toLowerCase());
        user.setEnabled(userTo.getEnabled());
        user.setRoles(userTo.getRoles());
        user.setManagedDepartments(userTo.getManagedDepartments());
        return user;
    }
}