package ru.javaprojects.rewardcalculator.web.security;

import ru.javaprojects.rewardcalculator.model.Department;
import ru.javaprojects.rewardcalculator.model.User;

import java.io.Serial;
import java.util.Set;

public class AuthorizedUser extends org.springframework.security.core.userdetails.User {
    @Serial
    private static final long serialVersionUID = 1L;

    private final int id;
    private final Set<Department> managedDepartments;

    public AuthorizedUser(User user) {
        super(user.getEmail(), user.getPassword(), user.isEnabled(), true, true, true, user.getRoles());
        id = user.getId();
        managedDepartments = user.getManagedDepartments();
    }

    public int getId() {
        return id;
    }

    public Set<Department> getManagedDepartments() {
        return managedDepartments;
    }
}