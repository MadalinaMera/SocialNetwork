package org.example.finalsocialnetwork.domain.validators;

import org.example.finalsocialnetwork.domain.Message;

public class MessageValidator implements Validator<Message> {
    @Override
    public void validate(Message entity) throws ValidationException {
        if(entity.getText().isEmpty())
            throw new ValidationException("Message is not valid");
    }
}
