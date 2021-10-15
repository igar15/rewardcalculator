package ru.javaprojects.rewardcalculator.util;

import ru.javaprojects.rewardcalculator.model.Position;
import ru.javaprojects.rewardcalculator.to.PositionTo;

public class PositionUtil {
    private PositionUtil() {
    }

    public static Position createFromTo(PositionTo positionTo) {
        return new Position(positionTo.getId(), positionTo.getName(), positionTo.getSalary());
    }

    public static Position updateFromTo(Position position, PositionTo positionTo) {
        position.setName(positionTo.getName());
        position.setSalary(positionTo.getSalary());
        return position;
    }
}