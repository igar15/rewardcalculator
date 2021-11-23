package ru.javaprojects.rewardcalculator.model;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Entity
@Table(name = "employees")
public class Employee extends AbstractNamedEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_id", nullable = false)
    private Position position;

    @NotNull
    @Column(name = "rate")
    @Enumerated(EnumType.STRING)
    private Rate rate;

    public Employee() {
    }

    public Employee(Integer id, String name, Rate rate) {
        super(id, name);
        this.rate = rate;
    }

    public Employee(Integer id, String name, Rate rate, Position position) {
        this(id, name, rate);
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Rate getRate() {
        return rate;
    }

    public void setRate(Rate rate) {
        this.rate = rate;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", name=" + name +
                ", rate=" + rate +
                '}';
    }
}