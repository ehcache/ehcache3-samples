package org.ehcache.sample.repository;

import org.ehcache.sample.domain.Actor;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;

import java.util.List;


/**
 * Spring Data JPA repository for the Actor entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ActorRepository extends JpaRepository<Actor, Long> {
    List<Actor> findByLastNameIgnoreCase(String lastName);
}
