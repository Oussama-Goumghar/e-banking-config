package com.ensa.domain;

import java.io.Serializable;
import java.time.LocalDate;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A HistorySystem.
 */
@Entity
@Table(name = "history_system")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class HistorySystem implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "id_agent")
    private Long idAgent;

    @Column(name = "action")
    private String action;

    @Column(name = "module")
    private String module;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "extra")
    private String extra;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public HistorySystem id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdAgent() {
        return this.idAgent;
    }

    public HistorySystem idAgent(Long idAgent) {
        this.setIdAgent(idAgent);
        return this;
    }

    public void setIdAgent(Long idAgent) {
        this.idAgent = idAgent;
    }

    public String getAction() {
        return this.action;
    }

    public HistorySystem action(String action) {
        this.setAction(action);
        return this;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getModule() {
        return this.module;
    }

    public HistorySystem module(String module) {
        this.setModule(module);
        return this;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public LocalDate getDate() {
        return this.date;
    }

    public HistorySystem date(LocalDate date) {
        this.setDate(date);
        return this;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getExtra() {
        return this.extra;
    }

    public HistorySystem extra(String extra) {
        this.setExtra(extra);
        return this;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof HistorySystem)) {
            return false;
        }
        return id != null && id.equals(((HistorySystem) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "HistorySystem{" +
            "id=" + getId() +
            ", idAgent=" + getIdAgent() +
            ", action='" + getAction() + "'" +
            ", module='" + getModule() + "'" +
            ", date='" + getDate() + "'" +
            ", extra='" + getExtra() + "'" +
            "}";
    }
}
