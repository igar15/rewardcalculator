package ru.javaprojects.rewardcalculator.util;

import org.junit.jupiter.api.Test;
import ru.javaprojects.rewardcalculator.util.exception.EmployeeRewardBadDataException;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.javaprojects.rewardcalculator.TestUtil.checkPdf;
import static ru.javaprojects.rewardcalculator.testdata.DepartmentRewardTestData.departmentReward1;
import static ru.javaprojects.rewardcalculator.testdata.DepartmentRewardTestData.departmentReward2;
import static ru.javaprojects.rewardcalculator.testdata.EmployeeRewardTestData.*;
import static ru.javaprojects.rewardcalculator.util.EmployeeRewardUtil.*;

class EmployeeRewardUtilTest {

    @Test
    void calculateHoursWorkedRewardTest() {
        assertEquals(10891, calculateHoursWorkedReward(120d, 53400, 176.5));
        assertEquals(7283, calculateHoursWorkedReward(80.25, 53400, 176.5));
        assertEquals(12629, calculateHoursWorkedReward(118.25, 53400, 150d));
    }

    @Test
    void calculateFullRewardTest() {
        assertEquals(21020, calculateFullReward(16020, 5000, 0));
        assertEquals(10000, calculateFullReward(16020, 0, 6020));
        assertThrows(EmployeeRewardBadDataException.class, () -> calculateFullReward(10000, 0, 12000));
    }

    @Test
    void calculateNewDistributedAmountTest() {
        int newDistributedAmount = calculateNewDistributedAmount(departmentReward1, 16020, 10020);
        assertEquals(34800, newDistributedAmount);
        assertThrows(EmployeeRewardBadDataException.class, () -> calculateNewDistributedAmount(departmentReward1, 16020, 20000));
    }

    @Test
    void createEmployeeRewardsPdfFormTestWithChiefAndApprovingSignatures() throws IOException {
        byte[] pdfBytes = createEmployeeRewardsPdfForm(List.of(employeeReward1, employeeReward2, employeeReward3), departmentReward2, APPROVING_SIGNATURE);
        checkPdf(pdfBytes, EMPLOYEE_REWARDS_PDF_FORM_WITH_CHIEF_AND_APPROVING_SIGNATURES_FILE_NAME);
    }

    @Test
    void createEmployeeRewardsPdfFormTestWithChiefSignatureOnly() throws IOException {
        byte[] pdfBytes = createEmployeeRewardsPdfForm(List.of(employeeReward1, employeeReward2, employeeReward3), departmentReward2, EMPTY_SIGNATURE);
        checkPdf(pdfBytes, EMPLOYEE_REWARDS_PDF_FORM_WITH_CHIEF_SIGNATURE_ONLY_FILE_NAME);
    }

    @Test
    void createEmployeeRewardsPdfFormTestWithApprovingSignatureOnly() throws IOException {
        byte[] pdfBytes = createEmployeeRewardsPdfForm(List.of(employeeReward1, employeeReward2), departmentReward2, APPROVING_SIGNATURE);
        checkPdf(pdfBytes, EMPLOYEE_REWARDS_PDF_FORM_WITH_APPROVING_SIGNATURE_ONLY_FILE_NAME);
    }

    @Test
    void getDepartmentChiefSignatureTestWhenChiefExist() {
        EmployeeSignature chiefSignature = getDepartmentChiefSignature(List.of(employeeReward1, employeeReward2, employeeReward3));
        assertEquals(CHIEF_SIGNATURE, chiefSignature);
    }

    @Test
    void getDepartmentChiefSignatureTestWhenChiefNotExist() {
        EmployeeSignature chiefSignature = getDepartmentChiefSignature(List.of(employeeReward1, employeeReward2));
        assertEquals(EMPTY_SIGNATURE, chiefSignature);
    }
}