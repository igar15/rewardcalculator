package ru.javaprojects.rewardcalculator.testdata;

import ru.javaprojects.rewardcalculator.TestMatcher;
import ru.javaprojects.rewardcalculator.model.Position;
import ru.javaprojects.rewardcalculator.to.PositionTo;

import static ru.javaprojects.rewardcalculator.testdata.DepartmentTestData.*;
import static ru.javaprojects.rewardcalculator.model.AbstractBaseEntity.START_SEQ;

public class PositionTestData {
    public static final TestMatcher<Position> POSITION_MATCHER = TestMatcher.usingIgnoringFieldsComparator(Position.class, "department");

    public static final int POSITION_1_ID = START_SEQ + 7;
    public static final int POSITION_2_ID = START_SEQ + 8;
    public static final int POSITION_3_ID = START_SEQ + 9;
    public static final int POSITION_ANOTHER_DEPARTMENT_ID = START_SEQ + 10;
    public static final int NOT_FOUND = 10;

    public static final Position position1 = new Position(POSITION_1_ID, "Web designer", 40200, false);
    public static final Position position2 = new Position(POSITION_2_ID, "Programmer", 35700, true);
    public static final Position position3 = new Position(POSITION_3_ID, "Business analyst", 60100, false);

    public static Position getNew() {
        return new Position(null, "newPositionName", 50000, false, department1);
    }

    public static PositionTo getNewTo() {
        return new PositionTo(null, "newPositionName", 50000, false, DEPARTMENT_1_ID);
    }

    public static Position getUpdated() {
        return new Position(POSITION_1_ID, "UpdatedPositionName", 54200, false, department1);
    }

    public static PositionTo getUpdatedTo() {
        return new PositionTo(POSITION_1_ID, "UpdatedPositionName", 54200, false, DEPARTMENT_1_ID);
    }
}