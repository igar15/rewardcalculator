package ru.javaprojects.rewardcalculator.to;

import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

public class EmployeeRewardTo extends BaseTo {

    @NotNull
    @Range(min = 0, max = 300)
    private Double hoursWorked;

    @NotNull
    @Range(min = 0, max = 5_000_000)
    private Integer additionalReward;

    @NotNull
    @Range(min = 0, max = 5_000_000)
    private Integer penalty;

    public EmployeeRewardTo() {
    }

    public EmployeeRewardTo(Integer id, Double hoursWorked, Integer additionalReward, Integer penalty) {
        super(id);
        this.hoursWorked = hoursWorked;
        this.additionalReward = additionalReward;
        this.penalty = penalty;
    }

    public Double getHoursWorked() {
        return hoursWorked;
    }

    public void setHoursWorked(Double hoursWorked) {
        this.hoursWorked = hoursWorked;
    }

    public Integer getAdditionalReward() {
        return additionalReward;
    }

    public void setAdditionalReward(Integer additionalReward) {
        this.additionalReward = additionalReward;
    }

    public Integer getPenalty() {
        return penalty;
    }

    public void setPenalty(Integer penalty) {
        this.penalty = penalty;
    }

    @Override
    public String toString() {
        return "EmployeeRewardTo{" +
                "id=" + id +
                ", hoursWorked=" + hoursWorked +
                ", additionalReward=" + additionalReward +
                ", penalty=" + penalty +
                '}';
    }
}