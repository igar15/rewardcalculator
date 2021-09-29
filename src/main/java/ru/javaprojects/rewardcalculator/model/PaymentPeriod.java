package ru.javaprojects.rewardcalculator.model;

import org.hibernate.validator.constraints.Range;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import java.time.YearMonth;

@Entity
@Table(name = "payment_periods", uniqueConstraints = {@UniqueConstraint(columnNames = "period", name = "payment_periods_unique_period_idx")})
public class PaymentPeriod extends AbstractBaseEntity {

    @NotNull
    @Column(name = "period", nullable = false, unique = true)
    private YearMonth period;

    @NotNull
    @Column(name = "required_hours_worked", nullable = false)
    @Range(min = 0, max = 200)
    private Double requiredHoursWorked;

    public PaymentPeriod() {
    }

    public PaymentPeriod(Integer id, YearMonth period, Double requiredHoursWorked) {
        super(id);
        this.period = period;
        this.requiredHoursWorked = requiredHoursWorked;
    }

    public YearMonth getPeriod() {
        return period;
    }

    public void setPeriod(YearMonth period) {
        this.period = period;
    }

    public Double getRequiredHoursWorked() {
        return requiredHoursWorked;
    }

    public void setRequiredHoursWorked(Double requiredHoursWorked) {
        this.requiredHoursWorked = requiredHoursWorked;
    }

    @Override
    public String toString() {
        return "PaymentPeriod{" +
                "id=" + id +
                ", period=" + period +
                ", requiredHoursWorked=" + requiredHoursWorked +
                '}';
    }
}