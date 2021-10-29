package ru.javaprojects.rewardcalculator.model;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Entity
@Table(name = "positions", uniqueConstraints = {@UniqueConstraint(columnNames = {"department_id", "name"}, name = "positions_unique_department_id_name_idx")})
public class Position extends AbstractNamedEntity {

    @NotNull
    @Min(10000)
    @Column(name = "salary", nullable = false)
    private Integer salary;

    @Column(name = "chief_position", nullable = false, columnDefinition = "bool default false")
    private boolean chiefPosition = false;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Department department;

    public Position() {
    }

    public Position(Integer id, String name, Integer salary, boolean chiefPosition) {
        super(id, name);
        this.salary = salary;
        this.chiefPosition = chiefPosition;
    }

    public Position(Integer id, String name, Integer salary, boolean chiefPosition, Department department) {
        this(id, name, salary, chiefPosition);
        this.department = department;
    }

    public Integer getSalary() {
        return salary;
    }

    public void setSalary(Integer salary) {
        this.salary = salary;
    }

    public boolean isChiefPosition() {
        return chiefPosition;
    }

    public void setChiefPosition(boolean chiefPosition) {
        this.chiefPosition = chiefPosition;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    @Override
    public String toString() {
        return "Position{" +
                "id=" + id +
                ", name=" + name +
                ", salary=" + salary +
                ", chiefPosition=" + chiefPosition +
                '}';
    }
}