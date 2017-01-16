package br.pegz.ciandt.repository;

import br.pegz.ciandt.domain.Teammate;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Teammate entity.
 */
@SuppressWarnings("unused")
public interface TeammateRepository extends JpaRepository<Teammate,Long> {

}
