package org.example.finalsocialnetwork.service;

import org.example.finalsocialnetwork.domain.Message;
import org.example.finalsocialnetwork.repository.database.MessageDBRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public class MessageService extends AbstractService<Long, Message> {

    private final MessageDBRepository messageRepository;

    public MessageService(MessageDBRepository repository) {
        super(repository);
        this.messageRepository = repository;
    }

    public Message saveMessage(String text, LocalDateTime timestamp, Long senderID, Long receiverID) {
        Message message = new Message(text, timestamp, senderID, receiverID);

        Optional<Message> existingMessage = repository.save(message);
        if (existingMessage.isPresent())
            throw new RuntimeException("Message already sent");
        return message;
    }

    public Iterable<Message> getMessagesBetweenUsers(Long userId1, Long userId2) {
        return messageRepository.getMessagesBetweenUsers(userId1, userId2);
    }
}