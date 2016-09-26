package org.terracotta.demo.service.impl;

import org.terracotta.demo.service.ActorService;
import org.terracotta.demo.domain.Actor;
import org.terracotta.demo.repository.ActorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

/**
 * Service Implementation for managing Actor.
 */
@Service
@Transactional
public class ActorServiceImpl implements ActorService{

    private final Logger log = LoggerFactory.getLogger(ActorServiceImpl.class);
    
    @Inject
    private ActorRepository actorRepository;

    /**
     * Save a actor.
     *
     * @param actor the entity to save
     * @return the persisted entity
     */
    public Actor save(Actor actor) {
        log.debug("Request to save Actor : {}", actor);
        Actor result = actorRepository.save(actor);
        return result;
    }

    /**
     *  Get all the actors.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<Actor> findAll(Pageable pageable) {
        log.debug("Request to get all Actors");
        Page<Actor> result = actorRepository.findAll(pageable);
        return result;
    }

    /**
     *  Get one actor by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public Actor findOne(Long id) {
        log.debug("Request to get Actor : {}", id);
        Actor actor = actorRepository.findOne(id);
        return actor;
    }

    /**
     *  Delete the  actor by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Actor : {}", id);
        actorRepository.delete(id);
    }
}
