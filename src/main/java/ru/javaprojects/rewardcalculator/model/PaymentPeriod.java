package ru.javaprojects.rewardcalculator.model;

import org.hibernate.validator.constraints.Range;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.Objects;

@Entity
@Table(name = "payment_periods", uniqueConstraints = {@UniqueConstraint(columnNames = "period", name = "payment_periods_unique_period_idx")})
public class PaymentPeriod extends AbstractBaseEntity {

    @NotNull
    @Column(name = "period", nullable = false, unique = true)
    @Convert(converter = YearMonthDateAttributeConverter.class)
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

    static class YearMonthDateAttributeConverter implements AttributeConverter<YearMonth, java.sql.Date> {
        @Override
        public java.sql.Date convertToDatabaseColumn(YearMonth yearMonth) {
            if (Objects.nonNull(yearMonth)) {
                return java.sql.Date.valueOf(yearMonth.atDay(1));
            }
            return null;
        }

        @Override
        public YearMonth convertToEntityAttribute(java.sql.Date date) {
            if (Objects.nonNull(date)) {
                return YearMonth.from(
                        Instant
                                .ofEpochMilli(date.getTime())
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate());
            }
            return null;
        }
    }
}