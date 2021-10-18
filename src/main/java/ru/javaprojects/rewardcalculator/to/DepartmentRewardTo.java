package ru.javaprojects.rewardcalculator.to;

import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

public class DepartmentRewardTo extends BaseTo {

    @NotNull
    private Integer departmentId;

    @NotNull
    private Integer paymentPeriodId;

    @NotNull
    @Range(min = 0, max = 5_000_000)
    private Integer allocatedAmount;

    public DepartmentRewardTo() {
    }

    public DepartmentRewardTo(Integer id, Integer departmentId, Integer paymentPeriodId, Integer allocatedAmount) {
        super(id);
        this.departmentId = departmentId;
        this.paymentPeriodId = paymentPeriodId;
        this.allocatedAmount = allocatedAmount;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public Integer getPaymentPeriodId() {
        return paymentPeriodId;
    }

    public void setPaymentPeriodId(Integer paymentPeriodId) {
        this.paymentPeriodId = paymentPeriodId;
    }

    public Integer getAllocatedAmount() {
        return allocatedAmount;
    }

    public void setAllocatedAmount(Integer allocatedAmount) {
        this.allocatedAmount = allocatedAmount;
    }

    @Override
    public String toString() {
        return "DepartmentRewardTo{" +
                "id=" + id +
                ", departmentId=" + departmentId +
                ", paymentPeriodId=" + paymentPeriodId +
                ", allocatedAmount=" + allocatedAmount +
                '}';
    }
}