package ru.javaprojects.rewardcalculator.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.javaprojects.rewardcalculator.model.EmployeeReward;
import ru.javaprojects.rewardcalculator.testdata.PositionTestData;
import ru.javaprojects.rewardcalculator.to.EmployeeRewardTo;
import ru.javaprojects.rewardcalculator.util.exception.EmployeeRewardBadDataException;
import ru.javaprojects.rewardcalculator.util.exception.NotFoundException;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.javaprojects.rewardcalculator.TestUtil.checkPdf;
import static ru.javaprojects.rewardcalculator.testdata.DepartmentRewardTestData.*;
import static ru.javaprojects.rewardcalculator.testdata.DepartmentTestData.DEPARTMENT_MATCHER;
import static ru.javaprojects.rewardcalculator.testdata.DepartmentTestData.department1;
import static ru.javaprojects.rewardcalculator.testdata.EmployeeRewardTestData.getUpdated;
import static ru.javaprojects.rewardcalculator.testdata.EmployeeRewardTestData.getUpdatedTo;
import static ru.javaprojects.rewardcalculator.testdata.EmployeeRewardTestData.*;
import static ru.javaprojects.rewardcalculator.testdata.EmployeeTestData.NOT_FOUND;

class EmployeeRewardServiceTest extends AbstractServiceTest {

    @Autowired
    private EmployeeRewardService service;

    @Autowired
    private DepartmentRewardService departmentRewardService;

    @Autowired
    private PositionService positionService;

    @Test
    void get() {
        EmployeeReward employeeReward = service.get(EMPLOYEE_REWARD_1_ID);
        EMPLOYEE_REWARD_MATCHER.assertMatch(employeeReward, employeeReward1);
    }

    @Test
    void getWithDepartment() {
        EmployeeReward employeeReward = service.getWithDepartment(EMPLOYEE_REWARD_1_ID);
        EMPLOYEE_REWARD_MATCHER.assertMatch(employeeReward, employeeReward1);
        DEPARTMENT_MATCHER.assertMatch(employeeReward.getDepartmentReward().getDepartment(), department1);
    }

    @Test
    void getNotFound() {
        assertThrows(NotFoundException.class, () -> service.get(NOT_FOUND));
    }

    @Test
    void getAllByDepartmentRewardId() {
        List<EmployeeReward> employeeRewards = service.getAllByDepartmentRewardId(DEPARTMENT_REWARD_2_ID);
        EMPLOYEE_REWARD_MATCHER.assertMatch(employeeRewards, employeeReward3, employeeReward2, employeeReward1);
    }

    @Test
    void getAllByDepartmentReward() {
        List<EmployeeReward> employeeRewards = service.getAllByDepartmentReward(departmentReward2);
        EMPLOYEE_REWARD_MATCHER.assertMatch(employeeRewards, employeeReward3, employeeReward2, employeeReward1);
    }

    // test on safety old rewards data, when employee position data changes in the future
    @Test
    void getAllByDepartmentRewardWhenEmployeePositionChanged() {
        positionService.update(PositionTestData.getUpdatedTo());
        List<EmployeeReward> employeeRewards = service.getAllByDepartmentReward(departmentReward2);
        EMPLOYEE_REWARD_MATCHER.assertMatch(employeeRewards, employeeReward3, employeeReward2, employeeReward1);
    }

    @Test
    void getAllByDepartmentRewardInPdfWithApprovingSignature() throws IOException {
        byte[] pdfBytes = service.getAllByDepartmentRewardInPdf(departmentReward2, APPROVING_SIGNATURE);
        checkPdf(pdfBytes, EMPLOYEE_REWARDS_PDF_FORM_WITH_CHIEF_AND_APPROVING_SIGNATURES_FILE_NAME);
    }

    @Test
    void getAllByDepartmentRewardInPdfWithoutApprovingSignature() throws IOException {
        byte[] pdfBytes = service.getAllByDepartmentRewardInPdf(departmentReward2, EMPTY_SIGNATURE);
        checkPdf(pdfBytes, EMPLOYEE_REWARDS_PDF_FORM_WITH_CHIEF_SIGNATURE_ONLY_FILE_NAME);
    }

    // test on safety old rewards data, when employee position data changes in the future
    @Test
    void getAllByDepartmentRewardInPdfWithoutApprovingSignatureWhenEmployeePositionChanged() throws IOException {
        positionService.update(PositionTestData.getUpdatedTo());
        byte[] pdfBytes = service.getAllByDepartmentRewardInPdf(departmentReward2, EMPTY_SIGNATURE);
        checkPdf(pdfBytes, EMPLOYEE_REWARDS_PDF_FORM_WITH_CHIEF_SIGNATURE_ONLY_FILE_NAME);
    }

    @Test
    void getAllByDepartmentRewardIdWithNotExistedDepartmentReward() {
        assertThrows(NotFoundException.class, () -> service.getAllByDepartmentRewardId(NOT_FOUND));
    }

    @Test
    void update() {
        service.update(getUpdatedTo());
        EMPLOYEE_REWARD_MATCHER.assertMatch(service.get(EMPLOYEE_REWARD_1_ID), getUpdated());
        DEPARTMENT_REWARD_MATCHER.assertMatch(departmentRewardService.get(DEPARTMENT_REWARD_2_ID), departmentReward2Updated);
    }

    @Test
    void updateWithNegativeFullReward() {
        EmployeeRewardTo updatedTo = getUpdatedTo();
        updatedTo.setPenalty(11000);
        assertThrows(EmployeeRewardBadDataException.class, () -> service.update(updatedTo));
    }

    @Test
    void updateWithFullRewardExceededMaxPercentageOfSalary() {
        EmployeeRewardTo updatedTo = getUpdatedTo();
        updatedTo.setAdditionalReward(25000);
        assertThrows(EmployeeRewardBadDataException.class, () -> service.update(updatedTo));
    }

    @Test
    void updateWithAllocatedAmountExceededTooMuchReward() {
        EmployeeRewardTo updatedTo = getUpdatedTo();
        updatedTo.setAdditionalReward(15000);
        assertThrows(EmployeeRewardBadDataException.class, () -> service.update(updatedTo));
    }

    @Test
    void updateWithAllocatedAmountExceededTooMuchHoursWorked() {
        EmployeeRewardTo updatedTo = getUpdatedTo();
        updatedTo.setHoursWorked(500d);
        assertThrows(EmployeeRewardBadDataException.class, () -> service.update(updatedTo));
    }

    @Test
    void updateNotFound() {
        EmployeeRewardTo updatedTo = getUpdatedTo();
        updatedTo.setId(NOT_FOUND);
        assertThrows(NotFoundException.class, () -> service.update(updatedTo));
    }
    @Test
    void updateWithException() {
        validateRootCause(IllegalArgumentException.class, () -> service.update(new EmployeeRewardTo(EMPLOYEE_REWARD_1_ID, null,  0, 0)));
        validateRootCause(IllegalArgumentException.class, () -> service.update(new EmployeeRewardTo(EMPLOYEE_REWARD_1_ID, 100d,  null, 0)));
        validateRootCause(IllegalArgumentException.class, () -> service.update(new EmployeeRewardTo(EMPLOYEE_REWARD_1_ID, 100d,  0, null)));
        validateRootCause(IllegalArgumentException.class, () -> service.update(new EmployeeRewardTo(EMPLOYEE_REWARD_1_ID, -5.d,  0, 0)));
        validateRootCause(IllegalArgumentException.class, () -> service.update(new EmployeeRewardTo(EMPLOYEE_REWARD_1_ID, 100d,  -2000, 0)));
        validateRootCause(IllegalArgumentException.class, () -> service.update(new EmployeeRewardTo(EMPLOYEE_REWARD_1_ID, 100d,  0, -2000)));
    }
}