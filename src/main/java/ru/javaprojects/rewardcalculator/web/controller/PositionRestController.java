package ru.javaprojects.rewardcalculator.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.javaprojects.rewardcalculator.model.Position;
import ru.javaprojects.rewardcalculator.service.PositionService;
import ru.javaprojects.rewardcalculator.to.PositionTo;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

import static ru.javaprojects.rewardcalculator.util.ValidationUtil.assureIdConsistent;
import static ru.javaprojects.rewardcalculator.util.ValidationUtil.checkNew;

@RestController
@RequestMapping(value = PositionRestController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class PositionRestController {
    private final Logger log = LoggerFactory.getLogger(getClass());
    static final String REST_URL = "/api";
    private final PositionService service;

    public PositionRestController(PositionService service) {
        this.service = service;
    }

    @GetMapping("/departments/{departmentId}/positions")
    public List<Position> getAll(@PathVariable int departmentId) {
        log.info("getAll for department {}", departmentId);
        return service.getAllByDepartmentId(departmentId);
    }

    @GetMapping("/positions/{id}")
    public Position get(@PathVariable int id) {
        log.info("get {}", id);
        return service.get(id);
    }

    @DeleteMapping("/positions/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        log.info("delete {}", id);
        service.delete(id);
    }

    @PostMapping(value = "/positions", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Position> createWithLocation(@Valid @RequestBody PositionTo positionTo) {
        log.info("create {}", positionTo);
        checkNew(positionTo);
        Position created = service.create(positionTo);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/positions/{id}")
                .buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @PutMapping(value = "/positions/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@Valid @RequestBody PositionTo positionTo, @PathVariable int id) {
        log.info("update {} with id={}", positionTo, id);
        assureIdConsistent(positionTo, id);
        service.update(positionTo);
    }
}