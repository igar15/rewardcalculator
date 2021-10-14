package ru.javaprojects.rewardcalculator.util;

import ru.javaprojects.rewardcalculator.model.User;
import ru.javaprojects.rewardcalculator.to.NewUserTo;
import ru.javaprojects.rewardcalculator.to.UserTo;

public class UserUtil {

    private UserUtil() {
    }

    public static User createNewFromTo(NewUserTo newUserTo) {
        return new User(null, newUserTo.getName(), newUserTo.getEmail().toLowerCase(), newUserTo.getPassword(),
                newUserTo.getEnabled(), newUserTo.getRoles());
    }

    public static User updateFromTo(User user, UserTo userTo) {
        user.setName(userTo.getName());
        user.setEmail(userTo.getEmail().toLowerCase());
        user.setEnabled(userTo.getEnabled());
        user.setRoles(userTo.getRoles());
        return user;
    }
}