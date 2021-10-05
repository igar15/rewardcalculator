package ru.javaprojects.rewardcalculator.service;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import ru.javaprojects.rewardcalculator.model.Department;
import ru.javaprojects.rewardcalculator.repository.DepartmentRepository;
import ru.javaprojects.rewardcalculator.util.exception.NotFoundException;

import java.util.List;

@Service
public class DepartmentService {
    private final DepartmentRepository repository;

    public DepartmentService(DepartmentRepository repository) {
        this.repository = repository;
    }

    public Department create(Department department) {
        Assert.notNull(department, "department must not be null");
        return repository.save(department);
    }

    public Department get(int id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Not found department with id=" + id));
    }

    public List<Department> getAll() {
        return repository.findAllByOrderByName();
    }

    public void delete(int id) {
        Department department = get(id);
        repository.delete(department);
    }

    public void update(Department department) {
        Assert.notNull(department, "department must not be null");
        get(department.id());
        repository.save(department);
    }
}