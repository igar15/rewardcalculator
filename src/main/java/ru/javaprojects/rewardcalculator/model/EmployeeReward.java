package ru.javaprojects.rewardcalculator.model;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.validator.constraints.Range;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "employee_rewards", uniqueConstraints = {@UniqueConstraint(columnNames = {"employee_id", "department_reward_id"}, name = "employee_rewards_unique_employee_id_department_reward_id_idx")})
public class EmployeeReward extends AbstractBaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Employee employee;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_reward_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private DepartmentReward departmentReward;

    @NotNull
    @Range(min = 0, max = 300)
    @Column(name = "hours_worked", nullable = false)
    private Double hoursWorked;

    @NotNull
    @Range(min = 0, max = 5_000_000)
    @Column(name = "hours_worked_reward", nullable = false)
    private Integer hoursWorkedReward;

    @NotNull
    @Range(min = 0, max = 5_000_000)
    @Column(name = "additional_reward", nullable = false)
    private Integer additionalReward;

    @NotNull
    @Range(min = 0, max = 5_000_000)
    @Column(name = "penalty", nullable = false)
    private Integer penalty;

    @NotBlank
    @Size(min = 4, max = 70)
    @Column(name = "current_position_name", nullable = false)
    private String currentPositionName;

    @NotNull
    @Min(10000)
    @Column(name = "current_position_salary", nullable = false)
    private Integer currentPositionSalary;

    @NotNull
    @Column(name = "current_employee_rate")
    @Enumerated(EnumType.STRING)
    private Rate currentEmployeeRate;

    public EmployeeReward() {
    }

    public EmployeeReward(Integer id, Double hoursWorked, Integer hoursWorkedReward, Integer additionalReward,
                          Integer penalty, String currentPositionName, Integer currentPositionSalary, Rate currentEmployeeRate ) {
        super(id);
        this.hoursWorked = hoursWorked;
        this.hoursWorkedReward = hoursWorkedReward;
        this.additionalReward = additionalReward;
        this.penalty = penalty;
        this.currentPositionName = currentPositionName;
        this.currentPositionSalary = currentPositionSalary;
        this.currentEmployeeRate = currentEmployeeRate;
    }

    public EmployeeReward(Integer id, Double hoursWorked, Integer hoursWorkedReward, Integer additionalReward,
                          Integer penalty, String currentPositionName, Integer currentPositionSalary, Rate currentEmployeeRate,
                          Employee employee, DepartmentReward departmentReward) {
        this(id, hoursWorked, hoursWorkedReward, additionalReward, penalty, currentPositionName, currentPositionSalary, currentEmployeeRate);
        this.employee = employee;
        this.departmentReward = departmentReward;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public DepartmentReward getDepartmentReward() {
        return departmentReward;
    }

    public void setDepartmentReward(DepartmentReward departmentReward) {
        this.departmentReward = departmentReward;
    }

    public Double getHoursWorked() {
        return hoursWorked;
    }

    public void setHoursWorked(Double hoursWorked) {
        this.hoursWorked = hoursWorked;
    }

    public Integer getHoursWorkedReward() {
        return hoursWorkedReward;
    }

    public void setHoursWorkedReward(Integer hoursWorkedReward) {
        this.hoursWorkedReward = hoursWorkedReward;
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

    public Integer getFullReward() {
        return hoursWorkedReward + additionalReward - penalty;
    }

    public String getCurrentPositionName() {
        return currentPositionName;
    }

    public void setCurrentPositionName(String currentPositionName) {
        this.currentPositionName = currentPositionName;
    }

    public Integer getCurrentPositionSalary() {
        return currentPositionSalary;
    }

    public void setCurrentPositionSalary(Integer currentPositionSalary) {
        this.currentPositionSalary = currentPositionSalary;
    }

    public Rate getCurrentEmployeeRate() {
        return currentEmployeeRate;
    }

    public void setCurrentEmployeeRate(Rate currentEmployeeRate) {
        this.currentEmployeeRate = currentEmployeeRate;
    }

    @Override
    public String toString() {
        return "EmployeeReward{" +
                "id=" + id +
                ", hoursWorked=" + hoursWorked +
                ", hoursWorkedReward=" + hoursWorkedReward +
                ", additionalReward=" + additionalReward +
                ", penalty=" + penalty +
                ", currentPositionName=" + currentPositionName +
                ", currentPositionSalary=" + currentPositionSalary +
                ", currentEmployeeRate=" + currentEmployeeRate +
                '}';
    }
}