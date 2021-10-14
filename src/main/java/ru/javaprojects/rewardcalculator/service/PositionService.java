package ru.javaprojects.rewardcalculator.service;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import ru.javaprojects.rewardcalculator.model.Position;
import ru.javaprojects.rewardcalculator.repository.PositionRepository;
import ru.javaprojects.rewardcalculator.util.exception.NotFoundException;

import java.util.List;

@Service
public class PositionService {
    private final PositionRepository repository;
    private final DepartmentService departmentService;

    public PositionService(PositionRepository repository, DepartmentService departmentService) {
        this.repository = repository;
        this.departmentService = departmentService;
    }

    public Position create(Position position) {
        Assert.notNull(position, "position must not be null");
        return repository.save(position);
    }

    public Position get(int id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Not found position with id=" + id));
    }

    public List<Position> getAllByDepartmentId(int departmentId) {
        departmentService.get(departmentId);
        return repository.findAllByDepartmentIdOrderByName(departmentId);
    }

    public void delete(int id) {
        Position position = get(id);
        repository.delete(position);
    }

    public void update(Position position) {
        Assert.notNull(position, "position must not be null");
        get(position.id());
        repository.save(position);
    }
}