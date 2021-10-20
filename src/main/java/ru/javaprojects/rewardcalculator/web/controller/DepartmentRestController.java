package ru.javaprojects.rewardcalculator.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.javaprojects.rewardcalculator.model.Department;
import ru.javaprojects.rewardcalculator.service.DepartmentService;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

import static ru.javaprojects.rewardcalculator.util.ValidationUtil.assureIdConsistent;
import static ru.javaprojects.rewardcalculator.util.ValidationUtil.checkNew;

@RestController
@RequestMapping(value = DepartmentRestController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class DepartmentRestController {
    private final Logger log = LoggerFactory.getLogger(getClass());
    static final String REST_URL = "/api/departments";
    private final DepartmentService service;

    public DepartmentRestController(DepartmentService service) {
        this.service = service;
    }

    @GetMapping
    public List<Department> getAll() {
        log.info("getAll");
        return service.getAll();
    }

    @GetMapping("/byPage")
    public Page<Department> getAll(Pageable pageable) {
        log.info("getAll (pageNumber={}, pageSize={})", pageable.getPageNumber(), pageable.getPageSize());
        return service.getAll(pageable);
    }

    @GetMapping("/{id}")
    public Department get(@PathVariable int id) {
        log.info("get {}", id);
        return service.get(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        log.info("delete {}", id);
        service.delete(id);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Department> createWithLocation(@Valid @RequestBody Department department) {
        log.info("create {}", department);
        checkNew(department);
        Department created = service.create(department);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@Valid @RequestBody Department department, @PathVariable int id) {
        log.info("update {} with id={}", department, id);
        assureIdConsistent(department, id);
        service.update(department);
    }
}