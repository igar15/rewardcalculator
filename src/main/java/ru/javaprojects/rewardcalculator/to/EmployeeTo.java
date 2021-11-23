package ru.javaprojects.rewardcalculator.to;

import ru.javaprojects.rewardcalculator.model.Rate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class EmployeeTo extends BaseTo {

    @NotBlank
    @Size(min = 4, max = 70)
    private String name;

    @NotNull
    private Rate rate;

    @NotNull
    private Integer positionId;

    public EmployeeTo() {
    }

    public EmployeeTo(Integer id, String name, Rate rate, Integer positionId) {
        super(id);
        this.name = name;
        this.rate = rate;
        this.positionId = positionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Rate getRate() {
        return rate;
    }

    public void setRate(Rate rate) {
        this.rate = rate;
    }

    public Integer getPositionId() {
        return positionId;
    }

    public void setPositionId(Integer positionId) {
        this.positionId = positionId;
    }

    @Override
    public String toString() {
        return "EmployeeTo{" +
                "id=" + id +
                ", name=" + name +
                ", rate=" + rate +
                ", positionId=" + positionId +
                '}';
    }
}