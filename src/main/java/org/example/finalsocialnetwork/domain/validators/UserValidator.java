package org.example.finalsocialnetwork.domain.validators;


import org.example.finalsocialnetwork.domain.User;

public class UserValidator implements Validator<User> {
    @Override
    public void validate(User entity) throws ValidationException {
        if(entity.getFirstName().isEmpty() || entity.getLastName().isEmpty())
            throw new ValidationException("Utilizatorul nu este valid");
    }
}
