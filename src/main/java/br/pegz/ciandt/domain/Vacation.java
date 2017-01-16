package br.pegz.ciandt.domain;

import br.pegz.ciandt.domain.enumeration.Status;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A Vacation.
 */
@Entity
@Table(name = "vacation")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Vacation implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "period")
    private Integer period;

    @Column(name = "start_date")
    private ZonedDateTime startDate;

    @Column(name = "end_date")
    private ZonedDateTime endDate;

    @Column(name = "sold_days")
    private Integer soldDays;

    @Column(name = "took_days")
    private Integer tookDays;

    @Enumerated(EnumType.STRING)
    @Column(name = "took")
    private Status took;


    @ManyToOne
    private Teammate teammate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getPeriod() {
        return period;
    }

    public Vacation period(Integer period) {
        this.period = period;
        return this;
    }

    public void setPeriod(Integer period) {
        this.period = period;
    }

    public ZonedDateTime getStartDate() {
        return startDate;
    }

    public Vacation startDate(ZonedDateTime startDate) {
        this.startDate = startDate;
        return this;
    }

    public void setStartDate(ZonedDateTime startDate) {
        this.startDate = startDate;
    }

    public ZonedDateTime getEndDate() {
        return endDate;
    }

    public Vacation endDate(ZonedDateTime endDate) {
        this.endDate = endDate;
        return this;
    }

    public void setEndDate(ZonedDateTime endDate) {
        this.endDate = endDate;
    }

    public Integer getSoldDays() {
        return soldDays;
    }

    public Vacation soldDays(Integer soldDays) {
        this.soldDays = soldDays;
        return this;
    }

    public void setSoldDays(Integer soldDays) {
        this.soldDays = soldDays;
    }

    public Integer getTookDays() {
        return tookDays;
    }

    public Vacation tookDays(Integer tookDays) {
        this.tookDays = tookDays;
        return this;
    }

    public void setTookDays(Integer tookDays) {
        this.tookDays = tookDays;
    }

    public Status getTook() {
        return took;
    }

    public Vacation took(Status took) {
        this.took = took;
        return this;
    }

    public void setTook(Status took) {
        this.took = took;
    }

    public Teammate getTeammate() {
        return teammate;
    }

    public Vacation teammate(Teammate teammate) {
        this.teammate = teammate;
        return this;
    }

    public void setTeammate(Teammate teammate) {
        this.teammate = teammate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Vacation vacation = (Vacation) o;
        if (vacation.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, vacation.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Vacation{" +
            "id=" + id +
            ", period='" + period + "'" +
            ", startDate='" + startDate + "'" +
            ", endDate='" + endDate + "'" +
            ", soldDays='" + soldDays + "'" +
            ", tookDays='" + tookDays + "'" +
            ", took='" + took + "'" +
            '}';
    }
}
