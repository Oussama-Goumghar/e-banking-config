package com.ensa.web.rest.vm;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;


public class NotificationVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long idTransaction;

    private Integer pin;

    private String reference;

    private String status;
    private String message="";
    private String phone="";
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public NotificationVo id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdTransaction() {
        return this.idTransaction;
    }

    public NotificationVo idTransaction(Long idTransaction) {
        this.setIdTransaction(idTransaction);
        return this;
    }

    public void setIdTransaction(Long idTransaction) {
        this.idTransaction = idTransaction;
    }

    public Integer getPin() {
        return this.pin;
    }

    public NotificationVo pin(Integer pin) {
        this.setPin(pin);
        return this;
    }

    public void setPin(Integer pin) {
        this.pin = pin;
    }

    public String getReference() {
        return this.reference;
    }

    public NotificationVo reference(String reference) {
        this.setReference(reference);
        return this;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getStatus() {
        return this.status;
    }

    public NotificationVo status(String status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NotificationVo)) {
            return false;
        }
        return id != null && id.equals(((NotificationVo) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Notification{" +
            "id=" + getId() +
            ", idTransaction=" + getIdTransaction() +
            ", pin=" + getPin() +
            ", reference='" + getReference() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
