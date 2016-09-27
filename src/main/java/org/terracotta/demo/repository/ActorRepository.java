package org.terracotta.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.terracotta.demo.domain.Actor;

import java.util.List;

/**
 * Spring Data JPA repository for the Actor entity.
 */
@SuppressWarnings("unused")
public interface ActorRepository extends JpaRepository<Actor,Long> {
    List<Actor> findByLastNameIgnoreCase(String lastName);
}
