package ru.javaprojects.rewardcalculator.util;

import org.junit.jupiter.api.Test;
import ru.javaprojects.rewardcalculator.util.exception.EmployeeRewardBadDataException;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.javaprojects.rewardcalculator.TestUtil.checkPdf;
import static ru.javaprojects.rewardcalculator.model.Rate.FULL_RATE;
import static ru.javaprojects.rewardcalculator.model.Rate.HALF_RATE;
import static ru.javaprojects.rewardcalculator.testdata.DepartmentRewardTestData.departmentReward1;
import static ru.javaprojects.rewardcalculator.testdata.DepartmentRewardTestData.departmentReward2;
import static ru.javaprojects.rewardcalculator.testdata.EmployeeRewardTestData.*;
import static ru.javaprojects.rewardcalculator.util.EmployeeRewardUtil.*;

class EmployeeRewardUtilTest {

    @Test
    void calculateHoursWorkedRewardTest() {
        assertEquals(10891, calculateHoursWorkedReward(120d, 53400, FULL_RATE, 176.5));
        assertEquals(7283, calculateHoursWorkedReward(80.25, 53400, FULL_RATE, 176.5));
        assertEquals(12629, calculateHoursWorkedReward(118.25, 53400, FULL_RATE, 150d));
        assertEquals(4890, calculateHoursWorkedReward(87, 32600, HALF_RATE, 174.25));
        assertEquals(4890, calculateHoursWorkedReward(88, 32600, HALF_RATE, 174.25));
        assertEquals(2806, calculateHoursWorkedReward(50, 32600, HALF_RATE, 174.25));
    }

    @Test
    void calculateFullRewardTest() {
        assertEquals(21020, calculateFullReward(16020, 5000, 0, 44000, FULL_RATE));
        assertEquals(10000, calculateFullReward(16020, 0, 6020, 44000, FULL_RATE));
        assertEquals(35200, calculateFullReward(10000, 25200, 0, 44000, FULL_RATE));
        assertEquals(4890, calculateFullReward(4890, 0, 0, 32600, HALF_RATE));
        assertThrows(EmployeeRewardBadDataException.class, () -> calculateFullReward(10000, 0, 12000, 44000, FULL_RATE));
        assertThrows(EmployeeRewardBadDataException.class, () -> calculateFullReward(10000, 26000, 0, 44000, FULL_RATE));
        assertThrows(EmployeeRewardBadDataException.class, () -> calculateFullReward(4890, 10000, 0, 32600, HALF_RATE));
    }

    @Test
    void calculateNewDistributedAmountTest() {
        int newDistributedAmount = calculateNewDistributedAmount(departmentReward1, 12060, 10000);
        assertEquals(10000, newDistributedAmount);
        assertThrows(EmployeeRewardBadDataException.class, () -> calculateNewDistributedAmount(departmentReward1, 12060, 50000));
    }

    @Test
    void createEmployeeRewardsPdfFormTestWithChiefAndApprovingSignatures() throws IOException {
        byte[] pdfBytes = createEmployeeRewardsPdfForm(List.of(employeeReward3, employeeReward2, employeeReward1), departmentReward2, CHIEF_SIGNATURE, APPROVING_SIGNATURE);
        checkPdf(pdfBytes, EMPLOYEE_REWARDS_PDF_FORM_WITH_CHIEF_AND_APPROVING_SIGNATURES_FILE_NAME);
    }

    @Test
    void createEmployeeRewardsPdfFormTestWithChiefSignatureOnly() throws IOException {
        byte[] pdfBytes = createEmployeeRewardsPdfForm(List.of(employeeReward3, employeeReward2, employeeReward1), departmentReward2, CHIEF_SIGNATURE, EMPTY_SIGNATURE);
        checkPdf(pdfBytes, EMPLOYEE_REWARDS_PDF_FORM_WITH_CHIEF_SIGNATURE_ONLY_FILE_NAME);
    }

    @Test
    void createEmployeeRewardsPdfFormTestWithApprovingSignatureOnly() throws IOException {
        byte[] pdfBytes = createEmployeeRewardsPdfForm(List.of(employeeReward2, employeeReward1), departmentReward2, EMPTY_SIGNATURE, APPROVING_SIGNATURE);
        checkPdf(pdfBytes, EMPLOYEE_REWARDS_PDF_FORM_WITH_APPROVING_SIGNATURE_ONLY_FILE_NAME);
    }
}