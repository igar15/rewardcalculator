package ru.javaprojects.rewardcalculator.service;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import ru.javaprojects.rewardcalculator.model.Employee;
import ru.javaprojects.rewardcalculator.repository.EmployeeRepository;
import ru.javaprojects.rewardcalculator.util.exception.NotFoundException;

import java.util.List;

@Service
public class EmployeeService {
    private final EmployeeRepository repository;
    private final DepartmentService departmentService;
    private final PositionService positionService;

    public EmployeeService(EmployeeRepository repository, DepartmentService departmentService, PositionService positionService) {
        this.repository = repository;
        this.departmentService = departmentService;
        this.positionService = positionService;
    }

    public Employee create(Employee employee) {
        Assert.notNull(employee, "employee must not be null");
        departmentService.get(employee.getDepartment().id());
        positionService.get(employee.getPosition().id());
        return repository.save(employee);
    }

    public Employee get(int id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Not found employee with id=" + id));
    }

    public List<Employee> getAllByDepartmentId(int departmentId) {
        departmentService.get(departmentId);
        return repository.findAllByDepartmentIdOrderByName(departmentId);
    }

    public void delete(int id) {
        Employee employee = get(id);
        repository.delete(employee);
    }

    public void update(Employee employee) {
        Assert.notNull(employee, "employee must not be null");
        get(employee.id());
        departmentService.get(employee.getDepartment().id());
        positionService.get(employee.getPosition().id());
        repository.save(employee);
    }
}