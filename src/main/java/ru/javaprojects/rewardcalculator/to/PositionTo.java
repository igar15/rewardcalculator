package ru.javaprojects.rewardcalculator.to;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class PositionTo extends BaseTo {

    @NotBlank
    @Size(min = 4, max = 70)
    private String name;

    @NotNull
    @Min(10000)
    private Integer salary;

    @NotNull
    private Boolean chiefPosition;

    @NotNull
    private Integer departmentId;

    public PositionTo() {
    }

    public PositionTo(Integer id, String name, Integer salary, Boolean chiefPosition, Integer departmentId) {
        super(id);
        this.name = name;
        this.salary = salary;
        this.chiefPosition = chiefPosition;
        this.departmentId = departmentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSalary() {
        return salary;
    }

    public void setSalary(Integer salary) {
        this.salary = salary;
    }

    public Boolean getChiefPosition() {
        return chiefPosition;
    }

    public void setChiefPosition(Boolean chiefPosition) {
        this.chiefPosition = chiefPosition;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    @Override
    public String toString() {
        return "PositionTo{" +
                "id=" + id +
                ", name=" + name +
                ", salary=" + salary +
                ", chiefPosition=" + chiefPosition +
                ", departmentId=" + departmentId +
                '}';
    }
}