package ru.javaprojects.rewardcalculator.util;

import ru.javaprojects.rewardcalculator.model.Employee;
import ru.javaprojects.rewardcalculator.to.EmployeeTo;

import java.util.Objects;

public class EmployeeUtil {
    private EmployeeUtil() {
    }

    public static Employee createFromTo(EmployeeTo employeeTo) {
        return new Employee(employeeTo.getId(), employeeTo.getName(), employeeTo.getRate());
    }

    public static Employee updateFromTo(Employee employee, EmployeeTo employeeTo) {
        employee.setName(employeeTo.getName());
        employee.setRate(employeeTo.getRate());
        return employee;
    }

    public static EmployeeSignature getEmployeeSignature(Employee employee) {
        return new EmployeeSignature(employee.getPosition().getName(), formatEmployeeName(employee.getName()));
    }

    public static String formatEmployeeName(String name) {
        String[] nameParts = name.split(" ");
        if (nameParts.length == 3) {
            StringBuilder builder = new StringBuilder();
            builder
                    .append(nameParts[1].charAt(0))
                    .append(".")
                    .append(nameParts[2].charAt(0))
                    .append(".")
                    .append(" ")
                    .append(nameParts[0]);
            return builder.toString();
        } else {
            return name;
        }
    }

    public static class EmployeeSignature {
        private final String position;
        private final String name;

        public EmployeeSignature(String position, String name) {
            this.position = position;
            this.name = name;
        }

        public EmployeeSignature() {
            this.position = "";
            this.name = "";
        }

        public String getPosition() {
            return position;
        }

        public String getName() {
            return name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            EmployeeSignature that = (EmployeeSignature) o;
            return Objects.equals(position, that.position) &&
                    Objects.equals(name, that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(position, name);
        }
    }
}
