package org.example.finalsocialnetwork.domain.validators;

public interface Validator<T> {
    void validate(T entity) throws ValidationException;
}