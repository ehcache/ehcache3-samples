package org.terracotta.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.terracotta.demo.repository.ActorRepository;

import javax.inject.Inject;

/**
 * Service Implementation for managing Actor.
 */
@Service
@Transactional
public class ActorService {

    private final Logger log = LoggerFactory.getLogger(ActorService.class);

    @Inject
    private ActorRepository actorRepository;

}
