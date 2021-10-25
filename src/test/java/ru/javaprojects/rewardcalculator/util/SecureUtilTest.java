package ru.javaprojects.rewardcalculator.util;

import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import ru.javaprojects.rewardcalculator.web.security.AuthorizedUser;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.javaprojects.rewardcalculator.testdata.DepartmentTestData.DEPARTMENT_1_ID;
import static ru.javaprojects.rewardcalculator.testdata.DepartmentTestData.DEPARTMENT_3_ID;
import static ru.javaprojects.rewardcalculator.testdata.UserTestData.*;

class SecureUtilTest {

    @Test
    void checkHasNotDepartmentHeadRoleOnly() {
        AuthorizedUser authUserAdmin = new AuthorizedUser(admin);
        assertDoesNotThrow(() -> SecureUtil.checkDepartmentHeadManagesTheDepartment(authUserAdmin, DEPARTMENT_1_ID));
        AuthorizedUser authUserEconomist = new AuthorizedUser(economist);
        assertDoesNotThrow(() -> SecureUtil.checkDepartmentHeadManagesTheDepartment(authUserEconomist, DEPARTMENT_1_ID));
        AuthorizedUser authUserPersonnelOfficer = new AuthorizedUser(personnelOfficer);
        assertDoesNotThrow(() -> SecureUtil.checkDepartmentHeadManagesTheDepartment(authUserPersonnelOfficer, DEPARTMENT_1_ID));
    }

    @Test
    void checkHasDepartmentHeadRoleOnly() {
        AuthorizedUser authUserDepartmentHead = new AuthorizedUser(departmentHead);
        assertDoesNotThrow(() -> SecureUtil.checkDepartmentHeadManagesTheDepartment(authUserDepartmentHead, DEPARTMENT_1_ID));
    }

    @Test
    void checkHasDepartmentHeadRoleOnlyNotManagedDepartment() {
        AuthorizedUser authUserDepartmentHead = new AuthorizedUser(departmentHead);
        assertThrows(AccessDeniedException.class, () -> SecureUtil.checkDepartmentHeadManagesTheDepartment(authUserDepartmentHead, DEPARTMENT_3_ID));
    }
}