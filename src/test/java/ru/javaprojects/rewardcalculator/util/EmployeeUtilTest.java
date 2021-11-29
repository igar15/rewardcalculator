package ru.javaprojects.rewardcalculator.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static ru.javaprojects.rewardcalculator.testdata.EmployeeRewardTestData.CHIEF_SIGNATURE;
import static ru.javaprojects.rewardcalculator.testdata.EmployeeTestData.employee3;
import static ru.javaprojects.rewardcalculator.util.EmployeeUtil.*;

class EmployeeUtilTest {

    @Test
    void getEmployeeSignatureTest() {
        assertEquals(CHIEF_SIGNATURE, getEmployeeSignature(employee3));
    }

    @Test
    void formatEmployeeNameWithThreeWords() {
        String name = "Charles Ray Robinson";
        assertEquals("R.R. Charles", formatEmployeeName(name));
    }

    @Test
    void formatEmployeeNameWithTwoWords() {
        String name = "Ray Charles";
        assertEquals("Ray Charles", formatEmployeeName(name));
    }
}