package ru.javaprojects.rewardcalculator.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import ru.javaprojects.rewardcalculator.model.DepartmentReward;
import ru.javaprojects.rewardcalculator.model.Employee;
import ru.javaprojects.rewardcalculator.model.EmployeeReward;
import ru.javaprojects.rewardcalculator.to.DepartmentRewardTo;
import ru.javaprojects.rewardcalculator.util.exception.DepartmentRewardBadDataException;
import ru.javaprojects.rewardcalculator.util.exception.NotFoundException;

import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.javaprojects.rewardcalculator.testdata.DepartmentRewardTestData.NOT_FOUND;
import static ru.javaprojects.rewardcalculator.testdata.DepartmentRewardTestData.PAGE;
import static ru.javaprojects.rewardcalculator.testdata.DepartmentRewardTestData.PAGEABLE;
import static ru.javaprojects.rewardcalculator.testdata.DepartmentRewardTestData.getNew;
import static ru.javaprojects.rewardcalculator.testdata.DepartmentRewardTestData.getNewTo;
import static ru.javaprojects.rewardcalculator.testdata.DepartmentRewardTestData.getUpdated;
import static ru.javaprojects.rewardcalculator.testdata.DepartmentRewardTestData.getUpdatedTo;
import static ru.javaprojects.rewardcalculator.testdata.DepartmentRewardTestData.*;
import static ru.javaprojects.rewardcalculator.testdata.DepartmentTestData.*;
import static ru.javaprojects.rewardcalculator.testdata.EmployeeTestData.*;
import static ru.javaprojects.rewardcalculator.testdata.PaymentPeriodTestData.*;

class DepartmentRewardServiceTest extends AbstractServiceTest {

    @Autowired
    private DepartmentRewardService service;

    @Autowired
    private EmployeeRewardService employeeRewardService;

    @Test
    void create() {
        DepartmentReward created = service.create(getNewTo());
        int newId = created.id();
        DepartmentReward newDepartmentReward = getNew();
        newDepartmentReward.setId(newId);
        DEPARTMENT_REWARD_MATCHER.assertMatch(created, newDepartmentReward);
        DEPARTMENT_REWARD_MATCHER.assertMatch(service.get(newId), newDepartmentReward);

        List<EmployeeReward> employeeRewards = employeeRewardService.getAllByDepartmentRewardId(newId);
        employeeRewards.forEach(employeeReward -> {
            assertEquals(0, employeeReward.getFullReward());
        });
        List<Employee> employees = employeeRewards.stream()
                .map(EmployeeReward::getEmployee)
                .collect(Collectors.toList());
        EMPLOYEE_MATCHER.assertMatch(employees, employee1, employee2, employee3);
    }

    @Test
    void createWithNotExistedDepartment() {
        DepartmentRewardTo newDepartmentRewardTo = getNewTo();
        newDepartmentRewardTo.setDepartmentId(NOT_FOUND);
        assertThrows(NotFoundException.class, () -> service.create(newDepartmentRewardTo));
    }

    @Test
    void createWithNotExistedPaymentPeriod() {
        DepartmentRewardTo newDepartmentRewardTo = getNewTo();
        newDepartmentRewardTo.setPaymentPeriodId(NOT_FOUND);
        assertThrows(NotFoundException.class, () -> service.create(newDepartmentRewardTo));
    }

    @Test
    void duplicateDepartmentAndPaymentPeriodCreate() {
        assertThrows(DataAccessException.class, () -> service.create(new DepartmentRewardTo(null, DEPARTMENT_1_ID, PAYMENT_PERIOD_1_ID, 200000)));
    }

    @Test
    void get() {
        DepartmentReward departmentReward = service.get(DEPARTMENT_REWARD_1_ID);
        DEPARTMENT_REWARD_MATCHER.assertMatch(departmentReward, departmentReward1);
        PAYMENT_PERIOD_MATCHER.assertMatch(departmentReward.getPaymentPeriod(), paymentPeriod1);
    }

    @Test
    void getWithDepartment() {
        DepartmentReward departmentReward = service.getWithDepartment(DEPARTMENT_REWARD_1_ID);
        DEPARTMENT_REWARD_MATCHER.assertMatch(departmentReward, departmentReward1);
        DEPARTMENT_MATCHER.assertMatch(departmentReward.getDepartment(), department1);
        PAYMENT_PERIOD_MATCHER.assertMatch(departmentReward.getPaymentPeriod(), paymentPeriod1);
    }

    @Test
    void getNotFound() {
        assertThrows(NotFoundException.class, () -> service.get(NOT_FOUND));
    }

    @Test
    void getByDepartmentIdAndPaymentPeriodId() {
        DepartmentReward departmentReward = service.getByDepartmentIdAndPaymentPeriodId(DEPARTMENT_1_ID, PAYMENT_PERIOD_1_ID);
        DEPARTMENT_REWARD_MATCHER.assertMatch(departmentReward, departmentReward1);
    }

    @Test
    void getByDepartmentIdAndPaymentPeriodIdWithNotExistedDepartment() {
        assertThrows(NotFoundException.class, () -> service.getByDepartmentIdAndPaymentPeriodId(NOT_FOUND, PAYMENT_PERIOD_1_ID));
    }

    @Test
    void getByDepartmentIdAndPaymentPeriodIdWithNotExistedPaymentPeriod() {
        assertThrows(NotFoundException.class, () -> service.getByDepartmentIdAndPaymentPeriodId(DEPARTMENT_1_ID, NOT_FOUND));
    }

    @Test
    void getAllByDepartmentId() {
        List<DepartmentReward> departmentRewards = service.getAllByDepartmentId(DEPARTMENT_1_ID);
        DEPARTMENT_REWARD_MATCHER.assertMatch(departmentRewards, departmentReward2, departmentReward1);
    }

    @Test
    void getAllByDepartmentIdByPage() {
        Page<DepartmentReward> departmentRewards = service.getAllByDepartmentId(DEPARTMENT_1_ID, PAGEABLE);
        assertEquals(PAGE, departmentRewards);
        DEPARTMENT_REWARD_MATCHER.assertMatch(departmentRewards.getContent(), departmentReward2, departmentReward1);
    }

    @Test
    void getAllByDepartmentIdWithNotExistedDepartment() {
        assertThrows(NotFoundException.class, () -> service.getAllByDepartmentId(NOT_FOUND));
    }

    @Test
    void delete() {
        service.delete(DEPARTMENT_REWARD_1_ID);
        assertThrows(NotFoundException.class, () -> service.get(DEPARTMENT_REWARD_1_ID));
    }

    @Test
    void deleteNotFound() {
        assertThrows(NotFoundException.class, () -> service.delete(NOT_FOUND));
    }

    @Test
    void update() {
        service.update(getUpdatedTo());
        DEPARTMENT_REWARD_MATCHER.assertMatch(service.get(DEPARTMENT_REWARD_1_ID), getUpdated());
    }

    @Test
    void updateNotFound() {
        DepartmentRewardTo updatedTo = getUpdatedTo();
        updatedTo.setId(NOT_FOUND);
        assertThrows(NotFoundException.class, () -> service.update(updatedTo));
    }

    @Test
    void updateWithAllocatedAMountLessThanExistedDistributedAmount() {
        DepartmentRewardTo updatedTo = getUpdatedTo();
        updatedTo.setAllocatedAmount(10000);
        assertThrows(DepartmentRewardBadDataException.class, () -> service.update(updatedTo));
    }

    @Test
    void createWithException() {
        validateRootCause(ConstraintViolationException.class, () -> service.create(new DepartmentRewardTo(null, DEPARTMENT_3_ID, PAYMENT_PERIOD_3_ID, null)));
        validateRootCause(ConstraintViolationException.class, () -> service.create(new DepartmentRewardTo(null, DEPARTMENT_3_ID, PAYMENT_PERIOD_3_ID, 5_500_000)));
        validateRootCause(ConstraintViolationException.class, () -> service.create(new DepartmentRewardTo(null, DEPARTMENT_3_ID, PAYMENT_PERIOD_3_ID, -200000)));
    }
}