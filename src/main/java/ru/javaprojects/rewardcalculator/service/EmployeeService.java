package ru.javaprojects.rewardcalculator.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import ru.javaprojects.rewardcalculator.model.Employee;
import ru.javaprojects.rewardcalculator.model.Position;
import ru.javaprojects.rewardcalculator.repository.EmployeeRepository;
import ru.javaprojects.rewardcalculator.to.EmployeeTo;
import ru.javaprojects.rewardcalculator.util.exception.NotFoundException;

import java.util.List;

import static ru.javaprojects.rewardcalculator.util.EmployeeUtil.*;

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

    @CacheEvict(value = "employees", allEntries = true)
    @Transactional
    public Employee create(EmployeeTo employeeTo) {
        Assert.notNull(employeeTo, "employeeTo must not be null");
        Position position = positionService.get(employeeTo.getPositionId());
        Employee employee = createFromTo(employeeTo);
        employee.setPosition(position);
        return repository.save(employee);
    }

    public Employee get(int id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Not found employee with id=" + id));
    }

    @Cacheable(value = "employees", key = "#departmentId")
    public List<Employee> getAllByDepartmentId(int departmentId) {
        departmentService.get(departmentId);
        return repository.findAllByPositionDepartmentIdWithPosition(departmentId);
    }

    @CacheEvict(value = "employees", allEntries = true)
    public void delete(int id) {
        Employee employee = get(id);
        repository.delete(employee);
    }

    @CacheEvict(value = "employees", allEntries = true)
    @Transactional
    public void update(EmployeeTo employeeTo) {
        Assert.notNull(employeeTo, "employeeTo must not be null");
        Employee employee = get(employeeTo.getId());
        Position position = positionService.get(employeeTo.getPositionId());
        updateFromTo(employee, employeeTo);
        employee.setPosition(position);
    }
}