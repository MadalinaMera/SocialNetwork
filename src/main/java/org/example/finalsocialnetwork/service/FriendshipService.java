package org.example.finalsocialnetwork.service;

import org.example.finalsocialnetwork.domain.Friendship;
import org.example.finalsocialnetwork.domain.Pair;
import org.example.finalsocialnetwork.domain.User;
import org.example.finalsocialnetwork.repository.Repository;
import org.example.finalsocialnetwork.repository.database.AbstractDBRepository;
import org.example.finalsocialnetwork.repository.database.FriendshipDBRepository;
import org.example.finalsocialnetwork.util.Page;
import org.example.finalsocialnetwork.util.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class FriendshipService extends AbstractService<Pair<Long,Long>,Friendship> {
    public FriendshipService(FriendshipDBRepository repository) {
        super(repository);
        this.repository = repository;
    }
    public Friendship addFriendship(Long iduser1, Long iduser2, LocalDateTime date) {
        Friendship friendship = new Friendship(iduser1, iduser2, date);
        if(iduser1<iduser2) {
            friendship.setId(new Pair<>(iduser1, iduser2));
        }
        else
            friendship.setId(new Pair<>(iduser2, iduser1));
        Optional<Friendship> existingfriendship = repository.save(friendship);
        if(existingfriendship.isPresent())
            throw new RuntimeException("The friendship already exists");
        return friendship;
    }
    public Page<User> findAllOnPage(Pageable pageable, Long iduser) {
        return repository.findAllOnPage(pageable,iduser);
    }
}
