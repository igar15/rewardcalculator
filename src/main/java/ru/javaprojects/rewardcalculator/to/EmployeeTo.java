package ru.javaprojects.rewardcalculator.to;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class EmployeeTo extends BaseTo {

    @NotBlank
    @Size(min = 4, max = 50)
    private String name;

    @NotNull
    private Integer positionId;

    public EmployeeTo() {
    }

    public EmployeeTo(Integer id, String name, Integer positionId) {
        super(id);
        this.name = name;
        this.positionId = positionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
                ", positionId=" + positionId +
                '}';
    }
}