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

    public PositionService(PositionRepository repository) {
        this.repository = repository;
    }

    public Position create(Position position) {
        Assert.notNull(position, "position must not be null");
        return repository.save(position);
    }

    public Position get(int id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Not found position with id=" + id));
    }

    public List<Position> getAll() {
        return repository.findAllByOrderByName();
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