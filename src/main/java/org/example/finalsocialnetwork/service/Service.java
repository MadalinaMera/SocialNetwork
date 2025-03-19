package org.example.finalsocialnetwork.service;

import org.example.finalsocialnetwork.domain.Entity;

import java.util.Optional;

public interface Service<ID, E extends Entity<ID>> {
    Iterable<E> getAll();
    void delete(ID id);
    Optional<E> find(ID id);
}
