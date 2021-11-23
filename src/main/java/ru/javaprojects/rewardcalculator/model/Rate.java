package ru.javaprojects.rewardcalculator.model;

public enum Rate {
    FULL_RATE(1),
    HALF_RATE(0.5),
    QUARTER_RATE(0.25);

    private final double coefficient;

    Rate(double coefficient) {
        this.coefficient = coefficient;
    }

    public double getCoefficient() {
        return coefficient;
    }
}
