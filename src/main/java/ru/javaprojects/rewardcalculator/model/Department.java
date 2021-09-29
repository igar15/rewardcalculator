package ru.javaprojects.rewardcalculator.model;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "departments", uniqueConstraints = {@UniqueConstraint(columnNames = "name", name = "departments_unique_name_idx")})
public class Department extends AbstractNamedEntity {

    public Department() {
    }

    public Department(Integer id, String name) {
        super(id, name);
    }

    @Override
    public String toString() {
        return "Department{" +
                "id=" + id +
                ", name=" + name +
                '}';
    }
}