package org.example.finalsocialnetwork.repository;

import org.example.finalsocialnetwork.domain.Entity;
import org.example.finalsocialnetwork.domain.Friendship;
import org.example.finalsocialnetwork.domain.Pair;
import org.example.finalsocialnetwork.domain.User;
import org.example.finalsocialnetwork.util.Page;
import org.example.finalsocialnetwork.util.Pageable;

public interface FriendshipRepository<P, F extends Entity<Pair<Long, Long>>> extends PagingRepository<Pair<Long,Long>, Friendship> {

    Page<User> findAllOnPage(Pageable pageable, Long iduser);

}
