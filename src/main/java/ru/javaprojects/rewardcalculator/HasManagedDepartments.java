package ru.javaprojects.rewardcalculator;

import ru.javaprojects.rewardcalculator.model.Department;

import java.util.Set;

public interface HasManagedDepartments {
    Set<Department> getManagedDepartments();

    void setManagedDepartments(Set<Department> managedDepartments);
}