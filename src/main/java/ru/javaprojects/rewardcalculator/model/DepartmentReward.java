package ru.javaprojects.rewardcalculator.model;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.validator.constraints.Range;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "department_rewards", uniqueConstraints = {@UniqueConstraint(columnNames = {"department_id", "payment_period_id"}, name = "department_rewards_unique_department_id_payment_period_id_idx")})
public class DepartmentReward extends AbstractBaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Department department;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_period_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private PaymentPeriod paymentPeriod;

    @NotNull
    @Range(min = 0, max = 5_000_000)
    @Column(name = "allocated_amount", nullable = false)
    private Integer allocatedAmount;

    @NotNull
    @Range(min = 0, max = 5_000_000)
    @Column(name = "distributed_amount", nullable = false)
    private Integer distributedAmount;

    public DepartmentReward() {
    }

    public DepartmentReward(Integer id, Integer allocatedAmount, Integer distributedAmount) {
        super(id);
        this.allocatedAmount = allocatedAmount;
        this.distributedAmount = distributedAmount;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public PaymentPeriod getPaymentPeriod() {
        return paymentPeriod;
    }

    public void setPaymentPeriod(PaymentPeriod paymentPeriod) {
        this.paymentPeriod = paymentPeriod;
    }

    public Integer getAllocatedAmount() {
        return allocatedAmount;
    }

    public void setAllocatedAmount(Integer allocatedAmount) {
        this.allocatedAmount = allocatedAmount;
    }

    public Integer getDistributedAmount() {
        return distributedAmount;
    }

    public void setDistributedAmount(Integer distributedAmount) {
        this.distributedAmount = distributedAmount;
    }

    @Override
    public String toString() {
        return "DepartmentReward{" +
                "id=" + id +
                ", allocatedAmount=" + allocatedAmount +
                ", distributedAmount=" + distributedAmount +
                '}';
    }
}