package com.ensa.domain;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A ClientBlackList.
 */
@Entity
@Table(name = "client_black_list")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ClientBlackList implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "idkyc")
    private Long idkyc;

    @Column(name = "raison")
    private String raison;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ClientBlackList id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdkyc() {
        return this.idkyc;
    }

    public ClientBlackList idkyc(Long idkyc) {
        this.setIdkyc(idkyc);
        return this;
    }

    public void setIdkyc(Long idkyc) {
        this.idkyc = idkyc;
    }

    public String getRaison() {
        return this.raison;
    }

    public ClientBlackList raison(String raison) {
        this.setRaison(raison);
        return this;
    }

    public void setRaison(String raison) {
        this.raison = raison;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ClientBlackList)) {
            return false;
        }
        return id != null && id.equals(((ClientBlackList) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ClientBlackList{" +
            "id=" + getId() +
            ", idkyc=" + getIdkyc() +
            ", raison='" + getRaison() + "'" +
            "}";
    }
}
