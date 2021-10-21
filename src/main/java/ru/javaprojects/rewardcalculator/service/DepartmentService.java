package ru.javaprojects.rewardcalculator.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @CacheEvict(value = "departments", allEntries = true)
    public Department create(Department department) {
        Assert.notNull(department, "department must not be null");
        return repository.save(department);
    }

    public Department get(int id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Not found department with id=" + id));
    }

    @Cacheable("departments")
    public List<Department> getAll() {
        return repository.findAllByOrderByName();
    }

    public Page<Department> getAll(Pageable pageable) {
        Assert.notNull(pageable, "pageable must not be null");
        return repository.findAllByOrderByName(pageable);
    }

    @Caching(evict = {
            @CacheEvict(value = "departments", allEntries = true),
            @CacheEvict(value = "positions", key = "#id"),
            @CacheEvict(value = "employees", key = "#id")
    })
    public void delete(int id) {
        Department department = get(id);
        repository.delete(department);
    }

    @CacheEvict(value = "departments", allEntries = true)
    public void update(Department department) {
        Assert.notNull(department, "department must not be null");
        get(department.id());
        repository.save(department);
    }
}