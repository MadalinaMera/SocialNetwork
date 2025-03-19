package org.example.finalsocialnetwork.service;

import org.example.finalsocialnetwork.domain.Entity;
import org.example.finalsocialnetwork.domain.Friendship;
import org.example.finalsocialnetwork.repository.Repository;
import org.example.finalsocialnetwork.util.Page;
import org.example.finalsocialnetwork.util.Pageable;

import java.util.Optional;

public abstract class AbstractService<ID,E extends Entity<ID>> implements Service<ID,E> {
    Repository<ID,E> repository;
    public AbstractService(Repository<ID,E> repository) {
        this.repository = repository;
    }

    @Override
    public Iterable<E> getAll() {
        return repository.findAll();
    }

    @Override
    public void delete(ID id) {
        repository.delete(id);
    }

    @Override
    public Optional<E> find(ID id) {
        return repository.findOne(id);
    }

}
