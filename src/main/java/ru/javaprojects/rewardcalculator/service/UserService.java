package ru.javaprojects.rewardcalculator.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import ru.javaprojects.rewardcalculator.model.Department;
import ru.javaprojects.rewardcalculator.model.User;
import ru.javaprojects.rewardcalculator.repository.UserRepository;
import ru.javaprojects.rewardcalculator.to.UserTo;
import ru.javaprojects.rewardcalculator.util.exception.NotFoundException;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import static ru.javaprojects.rewardcalculator.util.UserUtil.updateFromTo;

@Service
public class UserService {
    private final UserRepository repository;
    private final DepartmentService departmentService;

    public UserService(UserRepository repository, DepartmentService departmentService) {
        this.repository = repository;
        this.departmentService = departmentService;
    }

    public User create(User user) {
        Assert.notNull(user, "user must not be null");
        checkManagedDepartments(user.getManagedDepartments());
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
        checkManagedDepartments(user.getManagedDepartments());
        repository.save(user);
    }

    @Transactional
    public void update(UserTo userTo) {
        Assert.notNull(userTo, "userTo must not be null");
        User user = get(userTo.id());
        checkManagedDepartments(userTo.getManagedDepartments());
        updateFromTo(user, userTo);
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

    private void checkManagedDepartments(Set<Department> managedDepartments) {
        if (Objects.nonNull(managedDepartments)) {
            managedDepartments.forEach(department -> {
                Department dbDepartment = departmentService.get(department.id());
                managedDepartments.remove(department);
                managedDepartments.add(dbDepartment);
            });
        }
    }
}