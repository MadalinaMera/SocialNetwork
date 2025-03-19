package org.example.finalsocialnetwork.domain.validators;

import org.example.finalsocialnetwork.domain.Friendship;

import java.util.Objects;

public class FriendshipValidator implements Validator<Friendship> {
    @Override
    public void validate(Friendship entity) throws ValidationException {
        if(Objects.equals(entity.getIduser1(), entity.getIduser2())) {
            throw new ValidationException("You cannot befriend yourself");
        }
    }
}
