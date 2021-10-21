package ru.javaprojects.rewardcalculator.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import ru.javaprojects.rewardcalculator.model.Department;
import ru.javaprojects.rewardcalculator.model.Position;
import ru.javaprojects.rewardcalculator.repository.PositionRepository;
import ru.javaprojects.rewardcalculator.to.PositionTo;
import ru.javaprojects.rewardcalculator.util.exception.NotFoundException;

import java.util.List;

import static ru.javaprojects.rewardcalculator.util.PositionUtil.createFromTo;
import static ru.javaprojects.rewardcalculator.util.PositionUtil.updateFromTo;

@Service
public class PositionService {
    private final PositionRepository repository;
    private final DepartmentService departmentService;

    public PositionService(PositionRepository repository, DepartmentService departmentService) {
        this.repository = repository;
        this.departmentService = departmentService;
    }

    @CacheEvict(value = "positions", key = "#positionTo.departmentId")
    @Transactional
    public Position create(PositionTo positionTo) {
        Assert.notNull(positionTo, "positionTo must not be null");
        Department department = departmentService.get(positionTo.getDepartmentId());
        Position position = createFromTo(positionTo);
        position.setDepartment(department);
        return repository.save(position);
    }

    public Position get(int id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Not found position with id=" + id));
    }

    @Cacheable(value = "positions", key = "#departmentId")
    public List<Position> getAllByDepartmentId(int departmentId) {
        departmentService.get(departmentId);
        return repository.findAllByDepartmentIdOrderByName(departmentId);
    }

//    @CacheEvict(value = "positions", key = "@positionRepository.findByIdWithDepartment(#id).department.id", beforeInvocation = true)
    @CacheEvict(value = "positions", allEntries = true)
    public void delete(int id) {
        Position position = get(id);
        repository.delete(position);
    }

//    @CacheEvict(value = "positions", key = "@positionRepository.findByIdWithDepartment(#positionTo.id).department.id")
    @CacheEvict(value = "positions", allEntries = true)
    @Transactional
    public void update(PositionTo positionTo) {
        Assert.notNull(positionTo, "position must not be null");
        Position position = get(positionTo.getId());
        updateFromTo(position, positionTo);
    }
}