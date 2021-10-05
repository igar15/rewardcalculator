package ru.javaprojects.rewardcalculator.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import ru.javaprojects.rewardcalculator.model.User;
import ru.javaprojects.rewardcalculator.repository.UserRepository;
import ru.javaprojects.rewardcalculator.util.exception.NotFoundException;

import java.util.List;

@Service
public class UserService {
    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public User create(User user) {
        Assert.notNull(user, "user must not be null");
        return repository.save(user);
    }

    public User get(int id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Not found user with id=" + id));
    }

    public User getByEmail(String email) {
        Assert.notNull(email, "email must not be null");
        return repository.findByEmail(email).orElseThrow(() -> new NotFoundException("Not found user with email=" + email));
    }

    public List<User> getAll() {
        return repository.findAllByOrderByNameAscEmailAsc();
    }

    public void delete(int id) {
        User user = get(id);
        repository.delete(user);
    }

    public void update(User user) {
        Assert.notNull(user, "user must not be null");
        get(user.id());
        repository.save(user);
    }

    @Transactional
    public void enable(int id, boolean enabled) {
        User user = get(id);
        user.setEnabled(enabled);
    }

    @Transactional
    public void changePassword(int id, String password) {
        Assert.notNull(password, "password must not be null");
        User user = get(id);
        user.setPassword(password);
    }
}