package ru.javaprojects.rewardcalculator.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.javaprojects.rewardcalculator.config.OpenApiConfig;
import ru.javaprojects.rewardcalculator.model.User;
import ru.javaprojects.rewardcalculator.service.UserService;
import ru.javaprojects.rewardcalculator.to.NewUserTo;
import ru.javaprojects.rewardcalculator.to.UserTo;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.net.URI;
import java.util.List;

import static ru.javaprojects.rewardcalculator.config.OpenApiConfig.*;
import static ru.javaprojects.rewardcalculator.util.ValidationUtil.assureIdConsistent;
import static ru.javaprojects.rewardcalculator.util.ValidationUtil.checkNew;

@RestController
@RequestMapping(value = UserRestController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
@Secured("ROLE_ADMIN")
@Tag(name = "User Controller" + ALLOWED_ADMIN)
public class UserRestController {
    private final Logger log = LoggerFactory.getLogger(getClass());
    static final String REST_URL = "/api/users";
    private final UserService service;

    public UserRestController(UserService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(description = "Get all users" + ALLOWED_ADMIN)
    public List<User> getAll() {
        log.info("getAll");
        return service.getAll();
    }

    @GetMapping("/by")
    @Operation(description = "Get all users by key word" + ALLOWED_ADMIN)
    public List<User> getAllByKeyWord(@RequestParam String keyWord) {
        log.info("getAllByKeyWord {}", keyWord);
        return service.getAllByKeyWord(keyWord);
    }

    @GetMapping("/{id}")
    @Operation(description = "Get user" + ALLOWED_ADMIN)
    public User get(@PathVariable int id) {
        log.info("get {}", id);
        return service.get(id);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Create new user" + ALLOWED_ADMIN)
    public ResponseEntity<User> createWithLocation(@Valid @RequestBody NewUserTo newUserTo) {
        log.info("create {}", newUserTo);
        checkNew(newUserTo);
        User created = service.create(newUserTo);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(description = "Delete user" + ALLOWED_ADMIN)
    public void delete(@PathVariable int id) {
        log.info("delete {}", id);
        service.delete(id);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(description = "Update user" + ALLOWED_ADMIN)
    public void update(@Valid @RequestBody UserTo userTo, @PathVariable int id) {
        log.info("update {} with id={}", userTo, id);
        assureIdConsistent(userTo, id);
        service.update(userTo);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(description = "Enable/Disable user" + ALLOWED_ADMIN)
    public void enable(@PathVariable int id, @RequestParam boolean enabled) {
        log.info(enabled ? "enable {}" : "disable {}", id);
        service.enable(id, enabled);
    }

    @PatchMapping("/{id}/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(description = "Change user's password" + ALLOWED_ADMIN)
    public void changePassword(@PathVariable int id, @RequestParam @Size(min = 5, max = 32) String password) {
        log.info("change password for user {}", id);
        service.changePassword(id, password);
    }
}