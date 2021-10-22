package ru.javaprojects.rewardcalculator.web.json;

import org.junit.jupiter.api.Test;
import ru.javaprojects.rewardcalculator.model.Department;
import ru.javaprojects.rewardcalculator.model.PaymentPeriod;
import ru.javaprojects.rewardcalculator.to.NewUserTo;

import java.util.List;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.javaprojects.rewardcalculator.testdata.DepartmentTestData.*;
import static ru.javaprojects.rewardcalculator.testdata.PaymentPeriodTestData.*;
import static ru.javaprojects.rewardcalculator.testdata.UserTestData.*;

class JsonUtilTest {

    @Test
    void readWriteValue() {
        String json = JsonUtil.writeValue(department1);
        System.out.println(json);
        Department department = JsonUtil.readValue(json, Department.class);
        DEPARTMENT_MATCHER.assertMatch(department, department1);
    }

    @Test
    void readWriteValues() {
        String json = JsonUtil.writeValue(List.of(department1, department2, department3));
        System.out.println(json);
        List<Department> departments = JsonUtil.readValues(json, Department.class);
        DEPARTMENT_MATCHER.assertMatch(departments, List.of(department1, department2, department3));
    }

    @Test
    void writeOnlyAccess() {
        String json = JsonUtil.writeValue(departmentHead);
        System.out.println(json);
        assertThat(json, not(containsString("password")));
        String jsonWithPass = jsonWithPassword(getNewTo(), "newPass");
        System.out.println(jsonWithPass);
        NewUserTo newUserTo = JsonUtil.readValue(jsonWithPass, NewUserTo.class);
        assertEquals(newUserTo.getPassword(), "newPass");
    }

    @Test
    void readContentFromPage() {
        String jsonPage = "{\"content\":[{\"id\":100013,\"period\":\"2021-03\",\"requiredHoursWorked\":176.5}," +
                "{\"id\":100012,\"period\":\"2021-02\",\"requiredHoursWorked\":150.75}]," +
                "\"pageable\":{\"page\":0,\"size\":2,\"sort\":{\"orders\":[]}},\"total\":3}";
        List<PaymentPeriod> paymentPeriods = JsonUtil.readContentFromPage(jsonPage, PaymentPeriod.class);
        PAYMENT_PERIOD_MATCHER.assertMatch(paymentPeriods, paymentPeriod3, paymentPeriod2);
    }
}