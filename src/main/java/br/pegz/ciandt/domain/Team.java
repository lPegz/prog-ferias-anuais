package br.pegz.ciandt.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A Team.
 */
@Entity
@Table(name = "team")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Team implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name")
    private String name;

    @ManyToOne
    private Department department;

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "team_list_teammates",
               joinColumns = @JoinColumn(name="teams_id", referencedColumnName="ID"),
               inverseJoinColumns = @JoinColumn(name="list_teammates_id", referencedColumnName="ID"))
    private Set<Teammate> listTeammates = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Team name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Department getDepartment() {
        return department;
    }

    public Team department(Department department) {
        this.department = department;
        return this;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Set<Teammate> getListTeammates() {
        return listTeammates;
    }

    public Team listTeammates(Set<Teammate> teammates) {
        this.listTeammates = teammates;
        return this;
    }

    public Team addListTeammates(Teammate teammate) {
        listTeammates.add(teammate);
        return this;
    }

    public Team removeListTeammates(Teammate teammate) {
        listTeammates.remove(teammate);
        return this;
    }

    public void setListTeammates(Set<Teammate> teammates) {
        this.listTeammates = teammates;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Team team = (Team) o;
        if (team.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, team.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Team{" +
            "id=" + id +
            ", name='" + name + "'" +
            '}';
    }
}
