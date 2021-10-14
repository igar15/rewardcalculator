package ru.javaprojects.rewardcalculator.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "employees")
public class Employee extends AbstractNamedEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_id", nullable = false)
    private Position position;

    public Employee() {
    }

    public Employee(Integer id, String name) {
        super(id, name);
    }

    public Employee(Integer id, String name, Position position) {
        this(id, name);
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", name=" + name +
                '}';
    }
}