package com.ensa.web.rest;

import com.ensa.domain.ClientBlackList;
import com.ensa.repository.ClientBlackListRepository;
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
 * REST controller for managing {@link com.ensa.domain.ClientBlackList}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class ClientBlackListResource {

    private final Logger log = LoggerFactory.getLogger(ClientBlackListResource.class);

    private static final String ENTITY_NAME = "configApiClientBlackList";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ClientBlackListRepository clientBlackListRepository;

    public ClientBlackListResource(ClientBlackListRepository clientBlackListRepository) {
        this.clientBlackListRepository = clientBlackListRepository;
    }

    /**
     * {@code POST  /client-black-lists} : Create a new clientBlackList.
     *
     * @param clientBlackList the clientBlackList to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new clientBlackList, or with status {@code 400 (Bad Request)} if the clientBlackList has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/client-black-lists")
    public ResponseEntity<ClientBlackList> createClientBlackList(@Valid @RequestBody ClientBlackList clientBlackList)
        throws URISyntaxException {
        log.debug("REST request to save ClientBlackList : {}", clientBlackList);
        if (clientBlackList.getId() != null) {
            throw new BadRequestAlertException("A new clientBlackList cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ClientBlackList result = clientBlackListRepository.save(clientBlackList);
        return ResponseEntity
            .created(new URI("/api/client-black-lists/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /client-black-lists/:id} : Updates an existing clientBlackList.
     *
     * @param id the id of the clientBlackList to save.
     * @param clientBlackList the clientBlackList to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated clientBlackList,
     * or with status {@code 400 (Bad Request)} if the clientBlackList is not valid,
     * or with status {@code 500 (Internal Server Error)} if the clientBlackList couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/client-black-lists/{id}")
    public ResponseEntity<ClientBlackList> updateClientBlackList(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ClientBlackList clientBlackList
    ) throws URISyntaxException {
        log.debug("REST request to update ClientBlackList : {}, {}", id, clientBlackList);
        if (clientBlackList.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, clientBlackList.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!clientBlackListRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ClientBlackList result = clientBlackListRepository.save(clientBlackList);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, clientBlackList.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /client-black-lists/:id} : Partial updates given fields of an existing clientBlackList, field will ignore if it is null
     *
     * @param id the id of the clientBlackList to save.
     * @param clientBlackList the clientBlackList to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated clientBlackList,
     * or with status {@code 400 (Bad Request)} if the clientBlackList is not valid,
     * or with status {@code 404 (Not Found)} if the clientBlackList is not found,
     * or with status {@code 500 (Internal Server Error)} if the clientBlackList couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/client-black-lists/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ClientBlackList> partialUpdateClientBlackList(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ClientBlackList clientBlackList
    ) throws URISyntaxException {
        log.debug("REST request to partial update ClientBlackList partially : {}, {}", id, clientBlackList);
        if (clientBlackList.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, clientBlackList.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!clientBlackListRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ClientBlackList> result = clientBlackListRepository
            .findById(clientBlackList.getId())
            .map(existingClientBlackList -> {
                if (clientBlackList.getIdkyc() != null) {
                    existingClientBlackList.setIdkyc(clientBlackList.getIdkyc());
                }
                if (clientBlackList.getRaison() != null) {
                    existingClientBlackList.setRaison(clientBlackList.getRaison());
                }

                return existingClientBlackList;
            })
            .map(clientBlackListRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, clientBlackList.getId().toString())
        );
    }

    /**
     * {@code GET  /client-black-lists} : get all the clientBlackLists.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of clientBlackLists in body.
     */
    @GetMapping("/client-black-lists")
    public List<ClientBlackList> getAllClientBlackLists() {
        log.debug("REST request to get all ClientBlackLists");
        return clientBlackListRepository.findAll();
    }

    /**
     * {@code GET  /client-black-lists/:id} : get the "id" clientBlackList.
     *
     * @param id the id of the clientBlackList to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the clientBlackList, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/client-black-lists/{id}")
    public ResponseEntity<ClientBlackList> getClientBlackList(@PathVariable Long id) {
        log.debug("REST request to get ClientBlackList : {}", id);
        Optional<ClientBlackList> clientBlackList = clientBlackListRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(clientBlackList);
    }

    /**
     * {@code DELETE  /client-black-lists/:id} : delete the "id" clientBlackList.
     *
     * @param id the id of the clientBlackList to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/client-black-lists/{id}")
    public ResponseEntity<Void> deleteClientBlackList(@PathVariable Long id) {
        log.debug("REST request to delete ClientBlackList : {}", id);
        clientBlackListRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
