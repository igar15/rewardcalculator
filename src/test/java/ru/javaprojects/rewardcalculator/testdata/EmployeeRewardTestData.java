package ru.javaprojects.rewardcalculator.testdata;

import ru.javaprojects.rewardcalculator.TestMatcher;
import ru.javaprojects.rewardcalculator.model.EmployeeReward;
import ru.javaprojects.rewardcalculator.to.EmployeeRewardTo;

import static ru.javaprojects.rewardcalculator.model.AbstractBaseEntity.START_SEQ;
import static ru.javaprojects.rewardcalculator.testdata.DepartmentRewardTestData.departmentReward2;
import static ru.javaprojects.rewardcalculator.testdata.EmployeeTestData.*;
import static ru.javaprojects.rewardcalculator.testdata.PositionTestData.position2;
import static ru.javaprojects.rewardcalculator.util.EmployeeUtil.EmployeeSignature;

public class EmployeeRewardTestData {
    public static final TestMatcher<EmployeeReward> EMPLOYEE_REWARD_MATCHER = TestMatcher.usingIgnoringFieldsComparator(EmployeeReward.class, "employee", "departmentReward");

    public static final int EMPLOYEE_REWARD_1_ID = START_SEQ + 22;
    public static final int EMPLOYEE_REWARD_2_ID = START_SEQ + 23;
    public static final int EMPLOYEE_REWARD_3_ID = START_SEQ + 24;
    public static final int EMPLOYEE_REWARD_ANOTHER_DEPARTMENT_ID = START_SEQ + 26;
    public static final int NOT_FOUND = 10;

    public static final EmployeeReward employeeReward1 = new EmployeeReward(EMPLOYEE_REWARD_1_ID, 150.75, 12060, 0, 0, employee1.getPosition().getName(), employee1.getPosition().getSalary(), employee1.getRate(), employee1, departmentReward2);
    public static final EmployeeReward employeeReward2 = new EmployeeReward(EMPLOYEE_REWARD_2_ID, 150.75, 12060, 0, 0, employee2.getPosition().getName(), employee2.getPosition().getSalary(), employee2.getRate(), employee2, departmentReward2);
    public static final EmployeeReward employeeReward3 = new EmployeeReward(EMPLOYEE_REWARD_3_ID, 150.75, 10710, 0, 0, employee3.getPosition().getName(), employee3.getPosition().getSalary(), employee3.getRate(), employee3, departmentReward2);

    public static final String EMPLOYEE_REWARDS_PDF_FORM_WITH_CHIEF_AND_APPROVING_SIGNATURES_FILE_NAME = "./src/test/resources/rewardsWithChiefAndApprovingSignatures.pdf";
    public static final String EMPLOYEE_REWARDS_PDF_FORM_WITH_CHIEF_SIGNATURE_ONLY_FILE_NAME = "./src/test/resources/rewardsWithChiefSignatureOnly.pdf";
    public static final String EMPLOYEE_REWARDS_PDF_FORM_WITH_APPROVING_SIGNATURE_ONLY_FILE_NAME = "./src/test/resources/rewardsWithApprovingSignatureOnly.pdf";
    public static final EmployeeSignature CHIEF_SIGNATURE = new EmployeeSignature(position2.getName(), "Bob Smith");
    public static final EmployeeSignature APPROVING_SIGNATURE = new EmployeeSignature("Deputy Head of Scientific and Technical Center \"NIEMI\" No. 49 for General Technical Issues", "S.K. Raevsky");
    public static final EmployeeSignature EMPTY_SIGNATURE = new EmployeeSignature();

    public static EmployeeReward getUpdated() {
        return new EmployeeReward(EMPLOYEE_REWARD_1_ID, 100d, 8000, 2000, 0, employee1.getPosition().getName(), employee1.getPosition().getSalary(), employee1.getRate(), employee1, departmentReward2);
    }

    public static EmployeeRewardTo getUpdatedTo() {
        return new EmployeeRewardTo(EMPLOYEE_REWARD_1_ID, 100d, 2000, 0);
    }
}