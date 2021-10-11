package ru.javaprojects.rewardcalculator.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.javaprojects.rewardcalculator.model.DepartmentReward;
import ru.javaprojects.rewardcalculator.model.Employee;
import ru.javaprojects.rewardcalculator.model.EmployeeReward;
import ru.javaprojects.rewardcalculator.util.exception.EmployeeRewardBadDataException;
import ru.javaprojects.rewardcalculator.util.exception.NotFoundException;

import javax.validation.ConstraintViolationException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.javaprojects.rewardcalculator.DepartmentRewardTestData.DEPARTMENT_REWARD_2_ID;
import static ru.javaprojects.rewardcalculator.DepartmentRewardTestData.departmentReward2;
import static ru.javaprojects.rewardcalculator.EmployeeRewardTestData.*;
import static ru.javaprojects.rewardcalculator.EmployeeTestData.NOT_FOUND;
import static ru.javaprojects.rewardcalculator.EmployeeTestData.employee1;

class EmployeeRewardServiceTest extends AbstractServiceTest {

    @Autowired
    private EmployeeRewardService service;

    @Test
    void get() {
        EmployeeReward employeeReward = service.get(EMPLOYEE_REWARD_1_ID);
        EMPLOYEE_REWARD_MATCHER.assertMatch(employeeReward, employeeReward1);
    }

    @Test
    void getNotFound() {
        assertThrows(NotFoundException.class, () -> service.get(NOT_FOUND));
    }

    @Test
    void getAllByDepartmentRewardId() {
        List<EmployeeReward> employeeRewards = service.getAllByDepartmentRewardId(DEPARTMENT_REWARD_2_ID);
        EMPLOYEE_REWARD_MATCHER.assertMatch(employeeRewards, employeeReward1, employeeReward2, employeeReward3);
    }

    @Test
    void getAllByDepartmentRewardIdWithNotExistedDepartmentReward() {
        assertThrows(NotFoundException.class, () -> service.getAllByDepartmentRewardId(NOT_FOUND));
    }

    @Test
    void update() {
        service.update(getUpdatedWithEmployeeAndDepartmentReward());
        EMPLOYEE_REWARD_MATCHER.assertMatch(service.get(EMPLOYEE_REWARD_1_ID), getUpdated());
    }

    @Test
    void updateWithNegativeFullReward() {
        EmployeeReward updated = getUpdatedWithEmployeeAndDepartmentReward();
        updated.setPenalty(11000);
        assertThrows(EmployeeRewardBadDataException.class, () -> service.update(updated));
    }

    @Test
    void updateWithAllocatedAmountExceeded() {
        EmployeeReward updated = getUpdatedWithEmployeeAndDepartmentReward();
        updated.setAdditionalReward(2500);
        assertThrows(EmployeeRewardBadDataException.class, () -> service.update(updated));
    }

    @Test
    void updateNotFound() {
        EmployeeReward updated = getUpdatedWithEmployeeAndDepartmentReward();
        updated.setId(NOT_FOUND);
        assertThrows(NotFoundException.class, () -> service.update(updated));
    }

    @Test
    void updateWithNotExistedEmployee() {
        EmployeeReward updated = getUpdatedWithEmployeeAndDepartmentReward();
        updated.setEmployee(new Employee(NOT_FOUND, "employeeName"));
        assertThrows(NotFoundException.class, () -> service.update(updated));
    }

    @Test
    void updateWithNotExistedDepartmentReward() {
        EmployeeReward updated = getUpdatedWithEmployeeAndDepartmentReward();
        updated.setDepartmentReward(new DepartmentReward(NOT_FOUND, 40000, 0));
        assertThrows(NotFoundException.class, () -> service.update(updated));
    }

    @Test
    void updateWithException() {
        validateRootCause(ConstraintViolationException.class, () -> service.update(new EmployeeReward(EMPLOYEE_REWARD_1_ID, null, 12060, 0, 0, employee1, departmentReward2)));
        validateRootCause(ConstraintViolationException.class, () -> service.update(new EmployeeReward(EMPLOYEE_REWARD_1_ID, -5.d, 12060, 0, 0, employee1, departmentReward2)));
        validateRootCause(ConstraintViolationException.class, () -> service.update(new EmployeeReward(EMPLOYEE_REWARD_1_ID, 310d, 12060, 0, 0, employee1, departmentReward2)));
    }
}