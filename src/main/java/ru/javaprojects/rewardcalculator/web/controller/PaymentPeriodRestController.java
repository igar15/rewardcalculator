package ru.javaprojects.rewardcalculator.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.javaprojects.rewardcalculator.model.PaymentPeriod;
import ru.javaprojects.rewardcalculator.service.PaymentPeriodService;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

import static ru.javaprojects.rewardcalculator.config.OpenApiConfig.ALLOWED_ADMIN_ECONOMIST;
import static ru.javaprojects.rewardcalculator.util.ValidationUtil.assureIdConsistent;
import static ru.javaprojects.rewardcalculator.util.ValidationUtil.checkNew;

@RestController
@RequestMapping(value = PaymentPeriodRestController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Payment Period Controller")
public class PaymentPeriodRestController {
    private final Logger log = LoggerFactory.getLogger(getClass());
    static final String REST_URL = "/api/paymentperiods";
    private final PaymentPeriodService service;

    public PaymentPeriodRestController(PaymentPeriodService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(description = "Get all payment periods")
    public List<PaymentPeriod> getAll() {
        log.info("getAll");
        return service.getAll();
    }

    @GetMapping("/byPage")
    @Operation(description = "Get payment periods page")
    public Page<PaymentPeriod> getAll(Pageable pageable) {
        log.info("getAll (pageNumber={}, pageSize={})", pageable.getPageNumber(), pageable.getPageSize());
        return service.getAll(pageable);
    }

    @GetMapping("/{id}")
    @Operation(description = "Get payment period")
    public PaymentPeriod get(@PathVariable int id) {
        log.info("get {}", id);
        return service.get(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({"ROLE_ADMIN", "ROLE_ECONOMIST"})
    @Operation(description = "Delete payment period" + ALLOWED_ADMIN_ECONOMIST)
    public void delete(@PathVariable int id) {
        log.info("delete {}", id);
        service.delete(id);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Secured({"ROLE_ADMIN", "ROLE_ECONOMIST"})
    @Operation(description = "Create new payment period" + ALLOWED_ADMIN_ECONOMIST)
    public ResponseEntity<PaymentPeriod> createWithLocation(@Valid @RequestBody PaymentPeriod paymentPeriod) {
        log.info("create {}", paymentPeriod);
        checkNew(paymentPeriod);
        PaymentPeriod created = service.create(paymentPeriod);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({"ROLE_ADMIN", "ROLE_ECONOMIST"})
    @Operation(description = "Update payment period" + ALLOWED_ADMIN_ECONOMIST)
    public void update(@Valid @RequestBody PaymentPeriod paymentPeriod, @PathVariable int id) {
        log.info("update {} with id={}", paymentPeriod, id);
        assureIdConsistent(paymentPeriod, id);
        service.update(paymentPeriod);
    }
}