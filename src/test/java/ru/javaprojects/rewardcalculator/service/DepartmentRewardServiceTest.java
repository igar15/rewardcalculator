package ru.javaprojects.rewardcalculator.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import ru.javaprojects.rewardcalculator.model.Department;
import ru.javaprojects.rewardcalculator.model.DepartmentReward;
import ru.javaprojects.rewardcalculator.model.PaymentPeriod;
import ru.javaprojects.rewardcalculator.util.exception.DepartmentRewardBadDataException;
import ru.javaprojects.rewardcalculator.util.exception.NotFoundException;

import javax.validation.ConstraintViolationException;
import java.time.YearMonth;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.javaprojects.rewardcalculator.DepartmentRewardTestData.*;
import static ru.javaprojects.rewardcalculator.DepartmentTestData.department1;
import static ru.javaprojects.rewardcalculator.DepartmentTestData.department3;
import static ru.javaprojects.rewardcalculator.DepartmentTestData.DEPARTMENT_1_ID;
import static ru.javaprojects.rewardcalculator.EmployeeTestData.NOT_FOUND;
import static ru.javaprojects.rewardcalculator.PaymentPeriodTestData.paymentPeriod1;
import static ru.javaprojects.rewardcalculator.PaymentPeriodTestData.paymentPeriod3;
import static ru.javaprojects.rewardcalculator.PaymentPeriodTestData.PAYMENT_PERIOD_1_ID;

class DepartmentRewardServiceTest extends AbstractServiceTest {

    @Autowired
    private DepartmentRewardService service;

    @Test
    void create() {
        DepartmentReward created = service.create(getNewWithDepartmentAndPaymentPeriod());
        int newId = created.id();
        DepartmentReward newDepartmentReward = getNew();
        newDepartmentReward.setId(newId);
        DEPARTMENT_REWARD_MATCHER.assertMatch(created, newDepartmentReward);
        DEPARTMENT_REWARD_MATCHER.assertMatch(service.get(newId), newDepartmentReward);
    }

    @Test
    void createWithDistributedAmountNotZero() {
        DepartmentReward newDepartmentReward = getNewWithDepartmentAndPaymentPeriod();
        newDepartmentReward.setDistributedAmount(100000);
        assertThrows(DepartmentRewardBadDataException.class, () -> service.create(newDepartmentReward));
    }

    @Test
    void createWithNotExistedDepartment() {
        DepartmentReward newDepartmentReward = getNewWithDepartmentAndPaymentPeriod();
        newDepartmentReward.setDepartment(new Department(NOT_FOUND, "Department"));
        assertThrows(NotFoundException.class, () -> service.create(newDepartmentReward));
    }

    @Test
    void createWithNotExistedPaymentPeriod() {
        DepartmentReward newDepartmentReward = getNewWithDepartmentAndPaymentPeriod();
        newDepartmentReward.setPaymentPeriod(new PaymentPeriod(NOT_FOUND, YearMonth.now(), 150d));
        assertThrows(NotFoundException.class, () -> service.create(newDepartmentReward));
    }

    @Test
    void duplicateDepartmentAndPaymentPeriodCreate() {
        assertThrows(DataAccessException.class, () -> service.create(new DepartmentReward(null, 200000, 0, department1, paymentPeriod1)));
    }

    @Test
    void get() {
        DepartmentReward departmentReward = service.get(DEPARTMENT_REWARD_1_ID);
        DEPARTMENT_REWARD_MATCHER.assertMatch(departmentReward, departmentReward1);
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
        service.update(getUpdatedWithDepartmentAndPaymentPeriod());
        DEPARTMENT_REWARD_MATCHER.assertMatch(service.get(DEPARTMENT_REWARD_1_ID), getUpdated());
    }

    @Test
    void updateNotFound() {
        DepartmentReward updated = getUpdatedWithDepartmentAndPaymentPeriod();
        updated.setId(NOT_FOUND);
        assertThrows(NotFoundException.class, () -> service.update(updated));
    }

    @Test
    void updateWithNotExistedDepartment() {
        DepartmentReward updated = getUpdatedWithDepartmentAndPaymentPeriod();
        updated.setDepartment(new Department(NOT_FOUND, "Department"));
        assertThrows(NotFoundException.class, () -> service.update(updated));
    }

    @Test
    void updateWithNotExistedPaymentPeriod() {
        DepartmentReward updated = getUpdatedWithDepartmentAndPaymentPeriod();
        updated.setPaymentPeriod(new PaymentPeriod(NOT_FOUND, YearMonth.now(), 150d));
        assertThrows(NotFoundException.class, () -> service.update(updated));
    }

    @Test
    void updateWithAllocatedAMountLessThanExistedDistributedAmount() {
        DepartmentReward updated = getUpdatedWithDepartmentAndPaymentPeriod();
        updated.setAllocatedAmount(100000);
        assertThrows(DepartmentRewardBadDataException.class, () -> service.update(updated));
    }

    @Test
    void createWithException() {
        validateRootCause(ConstraintViolationException.class, () -> service.create(new DepartmentReward(null, null, 0, department3, paymentPeriod3)));
        validateRootCause(ConstraintViolationException.class, () -> service.create(new DepartmentReward(null, 5_500_000, 0, department3, paymentPeriod3)));
        validateRootCause(ConstraintViolationException.class, () -> service.create(new DepartmentReward(null, -200000, 0, department3, paymentPeriod3)));
    }
}