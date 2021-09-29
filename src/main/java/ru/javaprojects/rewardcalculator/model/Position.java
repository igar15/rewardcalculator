package ru.javaprojects.rewardcalculator.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "positions", uniqueConstraints = {@UniqueConstraint(columnNames = "name", name = "positions_unique_name_idx")})
public class Position extends AbstractNamedEntity {

    @NotNull
    @Min(10000)
    @Column(name = "salary", nullable = false)
    private Integer salary;

    public Position() {
    }

    public Position(Integer id, String name, Integer salary) {
        super(id, name);
        this.salary = salary;
    }

    public Integer getSalary() {
        return salary;
    }

    public void setSalary(Integer salary) {
        this.salary = salary;
    }

    @Override
    public String toString() {
        return "Position{" +
                "id=" + id +
                ", name=" + name +
                ", salary=" + salary +
                '}';
    }
}