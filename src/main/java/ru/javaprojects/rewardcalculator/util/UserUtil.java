package ru.javaprojects.rewardcalculator.util;

import org.springframework.security.crypto.password.PasswordEncoder;
import ru.javaprojects.rewardcalculator.model.User;
import ru.javaprojects.rewardcalculator.to.NewUserTo;
import ru.javaprojects.rewardcalculator.to.UserTo;

public class UserUtil {

    private UserUtil() {
    }

    public static User createNewFromTo(NewUserTo newUserTo, PasswordEncoder passwordEncoder) {
        return new User(null, newUserTo.getName(), newUserTo.getEmail().toLowerCase(),
                passwordEncoder.encode(newUserTo.getPassword()), newUserTo.getEnabled(), newUserTo.getRoles());
    }

    public static User updateFromTo(User user, UserTo userTo) {
        user.setName(userTo.getName());
        user.setEmail(userTo.getEmail().toLowerCase());
        user.setEnabled(userTo.getEnabled());
        user.setRoles(userTo.getRoles());
        return user;
    }
}