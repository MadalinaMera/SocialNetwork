package org.example.finalsocialnetwork.service;

import org.example.finalsocialnetwork.domain.User;
import org.example.finalsocialnetwork.repository.Repository;

import java.util.Random;
import java.util.random.*;
import java.util.Optional;

public class UserService extends AbstractService<Long, User> {

    public UserService(Repository<Long, User> repository) {
        super(repository);
    }


    public User addUser(String firstName, String lastName, String username){
        User user = new User(firstName, lastName, username);

        //user.setId(new Random().nextLong());
        ///???trebuie sa rezolv problema id-ului
        Optional<User> existingUser = repository.save(user);
        if(existingUser.isPresent())
            throw new RuntimeException("The user already exists");
        return user;
    }

}