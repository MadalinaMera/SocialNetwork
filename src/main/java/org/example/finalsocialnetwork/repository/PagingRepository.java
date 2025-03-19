package org.example.finalsocialnetwork.repository;

import org.example.finalsocialnetwork.domain.Entity;
import org.example.finalsocialnetwork.domain.User;
import org.example.finalsocialnetwork.util.Page;
import org.example.finalsocialnetwork.util.Pageable;

public interface PagingRepository<ID, E extends Entity<ID>> extends Repository<ID, E> {

    Page<User> findAllOnPage(Pageable pageable, Long iduser);
}
