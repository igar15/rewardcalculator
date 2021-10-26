package ru.javaprojects.rewardcalculator.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import ru.javaprojects.rewardcalculator.model.User;
import ru.javaprojects.rewardcalculator.repository.UserRepository;
import ru.javaprojects.rewardcalculator.to.NewUserTo;
import ru.javaprojects.rewardcalculator.to.UserTo;
import ru.javaprojects.rewardcalculator.util.exception.NotFoundException;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import static ru.javaprojects.rewardcalculator.util.UserUtil.createNewFromTo;
import static ru.javaprojects.rewardcalculator.util.UserUtil.updateFromTo;

@Service
public class UserService {
    private final UserRepository repository;
    private final DepartmentService departmentService;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository repository, DepartmentService departmentService, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.departmentService = departmentService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User create(NewUserTo newUserTo) {
        Assert.notNull(newUserTo, "newUserTo must not be null");
        User user = createNewFromTo(newUserTo, passwordEncoder);
        addManagedDepartments(user, newUserTo);
        return repository.save(user);
    }

    public User get(int id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Not found user with id=" + id));
    }

    @Cacheable(value = "users", key = "#email")
    public User getByEmail(String email) {
        Assert.notNull(email, "email must not be null");
        return repository.findByEmail(email).orElseThrow(() -> new NotFoundException("Not found user with email=" + email));
    }

    public List<User> getAll() {
        return repository.findAllByOrderByNameAscEmailAsc();
    }

    @CacheEvict(value = "users", allEntries = true)
    public void delete(int id) {
        User user = get(id);
        repository.delete(user);
    }

    @CacheEvict(value = "users", allEntries = true)
    @Transactional
    public void update(UserTo userTo) {
        Assert.notNull(userTo, "userTo must not be null");
        User user = get(userTo.getId());
        updateFromTo(user, userTo);
        addManagedDepartments(user, userTo);
    }

    @CacheEvict(value = "users", allEntries = true)
    @Transactional
    public void enable(int id, boolean enabled) {
        User user = get(id);
        user.setEnabled(enabled);
    }

    @CacheEvict(value = "users", allEntries = true)
    @Transactional
    public void changePassword(int id, String password) {
        Assert.notNull(password, "password must not be null");
        User user = get(id);
        user.setPassword(passwordEncoder.encode(password));
    }

    private void addManagedDepartments(User user, UserTo userTo) {
        Set<Integer> managedDepartmentsId = userTo.getManagedDepartmentsId();
        user.getManagedDepartments().clear();
        if (Objects.nonNull(managedDepartmentsId)) {
            managedDepartmentsId.forEach(departmentId -> user.addManagedDepartment(departmentService.get(departmentId)));
        }
    }
}