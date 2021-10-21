package ru.javaprojects.rewardcalculator.util;

import ru.javaprojects.rewardcalculator.model.Employee;
import ru.javaprojects.rewardcalculator.to.EmployeeTo;

public class EmployeeUtil {
    private EmployeeUtil() {
    }

    public static Employee createFromTo(EmployeeTo employeeTo) {
        return new Employee(employeeTo.getId(), employeeTo.getName());
    }

    public static Employee updateFromTo(Employee employee, EmployeeTo employeeTo) {
        employee.setName(employeeTo.getName());
        return employee;
    }
}