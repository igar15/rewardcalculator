package ru.javaprojects.rewardcalculator.util;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import ru.javaprojects.rewardcalculator.model.Department;
import ru.javaprojects.rewardcalculator.model.Role;
import ru.javaprojects.rewardcalculator.web.security.AuthorizedUser;

import java.util.Collection;

public class SecureUtil {
    private SecureUtil() {
    }

    public static void checkDepartmentHeadManagesTheDepartment(AuthorizedUser authUser, int departmentId) {
        if (hasDepartmentHeadRoleOnly(authUser) && !authUser.getManagedDepartments().contains(new Department(departmentId))) {
            throw new AccessDeniedException("User does not manages the department with id=" + departmentId);
        }
    }

    public static void checkDepartmentHeadManagesTheDepartment(AuthorizedUser authUser, Department department) {
        checkDepartmentHeadManagesTheDepartment(authUser, department.getId());
    }

    private static boolean hasDepartmentHeadRoleOnly(AuthorizedUser authUser) {
        Collection<GrantedAuthority> authorities = authUser.getAuthorities();
        return (authorities.size() == 1 && authorities.contains(Role.DEPARTMENT_HEAD));
    }
}