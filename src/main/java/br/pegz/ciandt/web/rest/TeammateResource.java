package br.pegz.ciandt.web.rest;

import com.codahale.metrics.annotation.Timed;
import br.pegz.ciandt.domain.Teammate;

import br.pegz.ciandt.repository.TeammateRepository;
import br.pegz.ciandt.web.rest.util.HeaderUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Teammate.
 */
@RestController
@RequestMapping("/api")
public class TeammateResource {

    private final Logger log = LoggerFactory.getLogger(TeammateResource.class);
        
    @Inject
    private TeammateRepository teammateRepository;

    /**
     * POST  /teammates : Create a new teammate.
     *
     * @param teammate the teammate to create
     * @return the ResponseEntity with status 201 (Created) and with body the new teammate, or with status 400 (Bad Request) if the teammate has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/teammates")
    @Timed
    public ResponseEntity<Teammate> createTeammate(@RequestBody Teammate teammate) throws URISyntaxException {
        log.debug("REST request to save Teammate : {}", teammate);
        if (teammate.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("teammate", "idexists", "A new teammate cannot already have an ID")).body(null);
        }
        Teammate result = teammateRepository.save(teammate);
        return ResponseEntity.created(new URI("/api/teammates/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("teammate", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /teammates : Updates an existing teammate.
     *
     * @param teammate the teammate to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated teammate,
     * or with status 400 (Bad Request) if the teammate is not valid,
     * or with status 500 (Internal Server Error) if the teammate couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/teammates")
    @Timed
    public ResponseEntity<Teammate> updateTeammate(@RequestBody Teammate teammate) throws URISyntaxException {
        log.debug("REST request to update Teammate : {}", teammate);
        if (teammate.getId() == null) {
            return createTeammate(teammate);
        }
        Teammate result = teammateRepository.save(teammate);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("teammate", teammate.getId().toString()))
            .body(result);
    }

    /**
     * GET  /teammates : get all the teammates.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of teammates in body
     */
    @GetMapping("/teammates")
    @Timed
    public List<Teammate> getAllTeammates() {
        log.debug("REST request to get all Teammates");
        List<Teammate> teammates = teammateRepository.findAll();
        return teammates;
    }

    /**
     * GET  /teammates/:id : get the "id" teammate.
     *
     * @param id the id of the teammate to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the teammate, or with status 404 (Not Found)
     */
    @GetMapping("/teammates/{id}")
    @Timed
    public ResponseEntity<Teammate> getTeammate(@PathVariable Long id) {
        log.debug("REST request to get Teammate : {}", id);
        Teammate teammate = teammateRepository.findOne(id);
        return Optional.ofNullable(teammate)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /teammates/:id : delete the "id" teammate.
     *
     * @param id the id of the teammate to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/teammates/{id}")
    @Timed
    public ResponseEntity<Void> deleteTeammate(@PathVariable Long id) {
        log.debug("REST request to delete Teammate : {}", id);
        teammateRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("teammate", id.toString())).build();
    }

}
