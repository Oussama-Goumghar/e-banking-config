package com.ensa.web.rest;

import com.ensa.domain.Notification;
import com.ensa.domain.TokenVerify;
import com.ensa.repository.NotificationRepository;
import com.ensa.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.ensa.web.rest.vm.NotificationVo;
import com.ensa.web.rest.vm.ResponseClient;
import com.messagebird.MessageBirdClient;
import com.messagebird.MessageBirdService;
import com.messagebird.MessageBirdServiceImpl;
import com.messagebird.exceptions.GeneralException;
import com.messagebird.exceptions.NotFoundException;
import com.messagebird.exceptions.UnauthorizedException;
import com.messagebird.objects.ErrorReport;
import com.messagebird.objects.Verify;
import com.messagebird.objects.VerifyRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.ensa.domain.Notification}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class NotificationResource {

    private final Logger log = LoggerFactory.getLogger(NotificationResource.class);

    private static final String ENTITY_NAME = "configApiNotification";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    // First create your service object
   final MessageBirdService wsr = new MessageBirdServiceImpl("ptgmyINoejD4ZKLmPXsjug7ji");

    // Add the service to the client
    final MessageBirdClient messageBirdClient = new MessageBirdClient(wsr);

    private final NotificationRepository notificationRepository;

    public NotificationResource(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    /**
     * {@code POST  /notifications} : Create a new notification.
     *
     * @param notification the notification to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new notification, or with status {@code 400 (Bad Request)} if the notification has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
     @PostMapping("/notifications")
    public ResponseEntity<Notification> createNotification(@Valid @RequestBody Notification notification) throws URISyntaxException {
        log.debug("REST request to save Notification : {}", notification);
        if (notification.getId() != null) {
            throw new BadRequestAlertException("A new notification cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Notification result = notificationRepository.save(notification);
        return ResponseEntity
            .created(new URI("/api/notifications/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }
    @PostMapping("/notifications/sms")
    public ResponseEntity<?> createNotificationSms(@Valid @RequestBody NotificationVo notifVo) throws URISyntaxException {
        Notification notification = new Notification(notifVo.getIdTransaction(),notifVo.getPin(),notifVo.getReference(),notifVo.getStatus());
         log.debug("REST request to save Notification : {}", notification);
        if (notification.getId() != null) {
            throw new BadRequestAlertException("A new notification cannot already have an ID", ENTITY_NAME, "idexists");
        }
        if (notifVo.getPhone().isEmpty()) {
            throw new BadRequestAlertException("", ENTITY_NAME, "phoneNotExist");
        }
         try {
            // Send verify token
            log.debug("REST request to save Notification : {}", notification);
             VerifyRequest verifyRequest = new VerifyRequest(String.format("+212%s",notifVo.getPhone()));
             verifyRequest.setOriginator("+12025550170");
             verifyRequest.setTimeout(3600);
             verifyRequest.setType("sms");
             if(notifVo.getMessage().isEmpty()) notifVo.setMessage("ENSAS-BANK SYSTEM \nYOUR CODE: %token");
             verifyRequest.setTemplate(notifVo.getMessage());
            // sent
            // ffc45e895a8f46768933b503cc61859f
            final Verify verify = messageBirdClient.sendVerifyToken(verifyRequest);
             // final Verify verify = messageBirdClient.verifyToken("759ca957538b4b3694ccfa72d492224e","108475");
            // System.out.println(verify.toString());
             notification.setStatus(verify.getId());
             Notification result = notificationRepository.save(notification);
            return ResponseEntity
                .created(new URI("/api/notifications/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                .body(result);
         } catch (com.messagebird.exceptions.UnauthorizedException | GeneralException exception) {
            if (exception.getErrors() != null) {
                System.out.println(exception.getErrors().toString());
                ErrorReport err = exception.getErrors().get(0);
                return ResponseEntity.status(400).body(new BadRequestAlertException(err.getMessage(),err.getDescription(),"ERROR IN MESSAGEBIRD"));
            }
            exception.printStackTrace();
            return ResponseEntity.status(400).body(exception.getMessage());
         }

    }

    @PostMapping("/notifications/verify")
    public ResponseEntity<?> createNotificationVerify(@Valid @RequestBody TokenVerify tokenver) throws URISyntaxException {

        try {
            final Verify verify = messageBirdClient.verifyToken(tokenver.getTokenId(),tokenver.getToken());
            return ResponseEntity.status(200)
                .body(new ResponseClient("VERIFY DONE",verify.getStatus(),"N/A"));
        } catch (GeneralException | UnauthorizedException exception) {
            if (exception.getErrors() != null) {
                System.out.println(exception.getErrors().toString());
                ErrorReport err = exception.getErrors().get(0);
                return ResponseEntity.status(400).body(new BadRequestAlertException(err.getMessage(),err.getDescription(),"MESSAGE"));
            }
            exception.printStackTrace();
            return ResponseEntity.status(400).body(exception.getMessage());
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * {@code PUT  /notifications/:id} : Updates an existing notification.
     *
     * @param id the id of the notification to save.
     * @param notification the notification to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated notification,
     * or with status {@code 400 (Bad Request)} if the notification is not valid,
     * or with status {@code 500 (Internal Server Error)} if the notification couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/notifications/{id}")
    public ResponseEntity<Notification> updateNotification(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Notification notification
    ) throws URISyntaxException {
        log.debug("REST request to update Notification : {}, {}", id, notification);
        if (notification.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, notification.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!notificationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Notification result = notificationRepository.save(notification);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, notification.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /notifications/:id} : Partial updates given fields of an existing notification, field will ignore if it is null
     *
     * @param id the id of the notification to save.
     * @param notification the notification to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated notification,
     * or with status {@code 400 (Bad Request)} if the notification is not valid,
     * or with status {@code 404 (Not Found)} if the notification is not found,
     * or with status {@code 500 (Internal Server Error)} if the notification couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/notifications/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Notification> partialUpdateNotification(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Notification notification
    ) throws URISyntaxException {
        log.debug("REST request to partial update Notification partially : {}, {}", id, notification);
        if (notification.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, notification.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!notificationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Notification> result = notificationRepository
            .findById(notification.getId())
            .map(existingNotification -> {
                if (notification.getIdTransaction() != null) {
                    existingNotification.setIdTransaction(notification.getIdTransaction());
                }
                if (notification.getPin() != null) {
                    existingNotification.setPin(notification.getPin());
                }
                if (notification.getReference() != null) {
                    existingNotification.setReference(notification.getReference());
                }
                if (notification.getStatus() != null) {
                    existingNotification.setStatus(notification.getStatus());
                }

                return existingNotification;
            })
            .map(notificationRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, notification.getId().toString())
        );
    }

    /**
     * {@code GET  /notifications} : get all the notifications.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of notifications in body.
     */
    @GetMapping("/notifications")
    public List<Notification> getAllNotifications() {
        log.debug("REST request to get all Notifications");
        return notificationRepository.findAll();
    }

    /**
     * {@code GET  /notifications/:id} : get the "id" notification.
     *
     * @param id the id of the notification to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the notification, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/notifications/{id}")
    public ResponseEntity<Notification> getNotification(@PathVariable Long id) {
        log.debug("REST request to get Notification : {}", id);
        Optional<Notification> notification = notificationRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(notification);
    }

    /**
     * {@code DELETE  /notifications/:id} : delete the "id" notification.
     *
     * @param id the id of the notification to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/notifications/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        log.debug("REST request to delete Notification : {}", id);
        notificationRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
