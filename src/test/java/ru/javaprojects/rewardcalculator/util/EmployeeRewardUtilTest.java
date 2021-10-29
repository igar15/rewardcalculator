package ru.javaprojects.rewardcalculator.util;

import com.itextpdf.text.pdf.PdfReader;
import org.junit.jupiter.api.Test;
import ru.javaprojects.rewardcalculator.TestUtil;
import ru.javaprojects.rewardcalculator.util.exception.EmployeeRewardBadDataException;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.javaprojects.rewardcalculator.TestUtil.*;
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
    void createEmployeeRewardsPdfFormTest() throws IOException {
        byte[] pdfBytes = createEmployeeRewardsPdfForm(List.of(employeeReward1, employeeReward2, employeeReward3), departmentReward2);
        checkPdf(pdfBytes);
    }
}