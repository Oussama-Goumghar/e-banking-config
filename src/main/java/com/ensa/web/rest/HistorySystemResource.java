package com.ensa.web.rest;

import com.ensa.domain.HistorySystem;
import com.ensa.repository.HistorySystemRepository;
import com.ensa.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.ensa.domain.HistorySystem}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class HistorySystemResource {

    private final Logger log = LoggerFactory.getLogger(HistorySystemResource.class);

    private static final String ENTITY_NAME = "configApiHistorySystem";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final HistorySystemRepository historySystemRepository;

    public HistorySystemResource(HistorySystemRepository historySystemRepository) {
        this.historySystemRepository = historySystemRepository;
    }

    /**
     * {@code POST  /history-systems} : Create a new historySystem.
     *
     * @param historySystem the historySystem to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new historySystem, or with status {@code 400 (Bad Request)} if the historySystem has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/history-systems")
    public ResponseEntity<HistorySystem> createHistorySystem(@Valid @RequestBody HistorySystem historySystem) throws URISyntaxException {
        log.debug("REST request to save HistorySystem : {}", historySystem);
        if (historySystem.getId() != null) {
            throw new BadRequestAlertException("A new historySystem cannot already have an ID", ENTITY_NAME, "idexists");
        }
        HistorySystem result = historySystemRepository.save(historySystem);
        return ResponseEntity
            .created(new URI("/api/history-systems/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /history-systems/:id} : Updates an existing historySystem.
     *
     * @param id the id of the historySystem to save.
     * @param historySystem the historySystem to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated historySystem,
     * or with status {@code 400 (Bad Request)} if the historySystem is not valid,
     * or with status {@code 500 (Internal Server Error)} if the historySystem couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/history-systems/{id}")
    public ResponseEntity<HistorySystem> updateHistorySystem(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody HistorySystem historySystem
    ) throws URISyntaxException {
        log.debug("REST request to update HistorySystem : {}, {}", id, historySystem);
        if (historySystem.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, historySystem.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!historySystemRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        HistorySystem result = historySystemRepository.save(historySystem);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, historySystem.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /history-systems/:id} : Partial updates given fields of an existing historySystem, field will ignore if it is null
     *
     * @param id the id of the historySystem to save.
     * @param historySystem the historySystem to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated historySystem,
     * or with status {@code 400 (Bad Request)} if the historySystem is not valid,
     * or with status {@code 404 (Not Found)} if the historySystem is not found,
     * or with status {@code 500 (Internal Server Error)} if the historySystem couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/history-systems/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<HistorySystem> partialUpdateHistorySystem(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody HistorySystem historySystem
    ) throws URISyntaxException {
        log.debug("REST request to partial update HistorySystem partially : {}, {}", id, historySystem);
        if (historySystem.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, historySystem.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!historySystemRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<HistorySystem> result = historySystemRepository
            .findById(historySystem.getId())
            .map(existingHistorySystem -> {
                if (historySystem.getIdAgent() != null) {
                    existingHistorySystem.setIdAgent(historySystem.getIdAgent());
                }
                if (historySystem.getAction() != null) {
                    existingHistorySystem.setAction(historySystem.getAction());
                }
                if (historySystem.getModule() != null) {
                    existingHistorySystem.setModule(historySystem.getModule());
                }
                if (historySystem.getDate() != null) {
                    existingHistorySystem.setDate(historySystem.getDate());
                }
                if (historySystem.getExtra() != null) {
                    existingHistorySystem.setExtra(historySystem.getExtra());
                }

                return existingHistorySystem;
            })
            .map(historySystemRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, historySystem.getId().toString())
        );
    }

    /**
     * {@code GET  /history-systems} : get all the historySystems.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of historySystems in body.
     */
    @GetMapping("/history-systems")
    public List<HistorySystem> getAllHistorySystems() {
        log.debug("REST request to get all HistorySystems");
        return historySystemRepository.findAll();
    }

    /**
     * {@code GET  /history-systems/:id} : get the "id" historySystem.
     *
     * @param id the id of the historySystem to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the historySystem, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/history-systems/{id}")
    public ResponseEntity<HistorySystem> getHistorySystem(@PathVariable Long id) {
        log.debug("REST request to get HistorySystem : {}", id);
        Optional<HistorySystem> historySystem = historySystemRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(historySystem);
    }

    /**
     * {@code DELETE  /history-systems/:id} : delete the "id" historySystem.
     *
     * @param id the id of the historySystem to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/history-systems/{id}")
    public ResponseEntity<Void> deleteHistorySystem(@PathVariable Long id) {
        log.debug("REST request to delete HistorySystem : {}", id);
        historySystemRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
