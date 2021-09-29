package ru.javaprojects.rewardcalculator.model;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.validator.constraints.Range;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "employee_rewards", uniqueConstraints = {@UniqueConstraint(columnNames = {"employee_id", "payment_period_id"}, name = "employee_rewards_unique_department_id_payment_period_id_idx")})
public class EmployeeReward extends AbstractBaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Employee employee;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_period_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private PaymentPeriod paymentPeriod;

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

    public EmployeeReward() {
    }

    public EmployeeReward(Integer id, Double hoursWorked, Integer hoursWorkedReward, Integer additionalReward, Integer penalty) {
        super(id);
        this.hoursWorked = hoursWorked;
        this.hoursWorkedReward = hoursWorkedReward;
        this.additionalReward = additionalReward;
        this.penalty = penalty;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public PaymentPeriod getPaymentPeriod() {
        return paymentPeriod;
    }

    public void setPaymentPeriod(PaymentPeriod paymentPeriod) {
        this.paymentPeriod = paymentPeriod;
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

    @Override
    public String toString() {
        return "EmployeeReward{" +
                "id=" + id +
                ", hoursWorked=" + hoursWorked +
                ", hoursWorkedReward=" + hoursWorkedReward +
                ", additionalReward=" + additionalReward +
                ", penalty=" + penalty +
                '}';
    }
}