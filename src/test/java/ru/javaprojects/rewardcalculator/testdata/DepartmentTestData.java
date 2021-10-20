package ru.javaprojects.rewardcalculator.testdata;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.javaprojects.rewardcalculator.TestMatcher;
import ru.javaprojects.rewardcalculator.model.Department;

import java.util.List;

import static ru.javaprojects.rewardcalculator.model.AbstractBaseEntity.START_SEQ;

public class DepartmentTestData {
    public static final TestMatcher<Department> DEPARTMENT_MATCHER = TestMatcher.usingIgnoringFieldsComparator(Department.class);

    public static final int DEPARTMENT_1_ID = START_SEQ + 2;
    public static final int DEPARTMENT_2_ID = START_SEQ + 3;
    public static final int DEPARTMENT_3_ID = START_SEQ + 4;
    public static final int NOT_FOUND = 10;

    public static final Department department1 = new Department(DEPARTMENT_1_ID, "Отдел № 1");
    public static final Department department2 = new Department(DEPARTMENT_2_ID, "Отдел № 3");
    public static final Department department3 = new Department(DEPARTMENT_3_ID, "Отдел № 2");

    public static final Pageable PAGEABLE = PageRequest.of(0, 2);
    public static final Page<Department> PAGE = new PageImpl<>(List.of(department1, department3), PAGEABLE, 3);

    public static Department getNew() {
        return new Department(null, "NewDepartment");
    }

    public static Department getUpdated() {
        return new Department(DEPARTMENT_1_ID, "UpdatedName");
    }
}