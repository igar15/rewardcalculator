package ru.javaprojects.rewardcalculator.testdata;

import ru.javaprojects.rewardcalculator.TestMatcher;
import ru.javaprojects.rewardcalculator.model.Position;
import ru.javaprojects.rewardcalculator.to.PositionTo;

import static ru.javaprojects.rewardcalculator.testdata.DepartmentTestData.*;
import static ru.javaprojects.rewardcalculator.model.AbstractBaseEntity.START_SEQ;

public class PositionTestData {
    public static final TestMatcher<Position> POSITION_MATCHER = TestMatcher.usingIgnoringFieldsComparator(Position.class, "department");

    public static final int POSITION_1_ID = START_SEQ + 5;
    public static final int POSITION_2_ID = START_SEQ + 6;
    public static final int POSITION_3_ID = START_SEQ + 7;
    public static final int NOT_FOUND = 10;

    public static final Position position1 = new Position(POSITION_1_ID, "position 1 name", 40200);
    public static final Position position2 = new Position(POSITION_2_ID, "position 2 name", 35700);
    public static final Position position3 = new Position(POSITION_3_ID, "position 3 name", 60100);

    public static Position getNew() {
        return new Position(null, "newPositionName", 50000, department1);
    }

    public static PositionTo getNewTo() {
        return new PositionTo(null, "newPositionName", 50000, DEPARTMENT_1_ID);
    }

    public static Position getUpdated() {
        return new Position(POSITION_1_ID, "UpdatedPositionName", 54200, department1);
    }

    public static PositionTo getUpdatedTo() {
        return new PositionTo(POSITION_1_ID, "UpdatedPositionName", 54200, DEPARTMENT_1_ID);
    }
}