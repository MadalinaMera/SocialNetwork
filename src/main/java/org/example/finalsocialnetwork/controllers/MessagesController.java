package org.example.finalsocialnetwork.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import org.example.finalsocialnetwork.domain.Message;
import org.example.finalsocialnetwork.domain.User;
import org.example.finalsocialnetwork.service.MessageService;
import org.example.finalsocialnetwork.service.Network;

import java.time.LocalDateTime;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class MessagesController {

    @FXML
    public AnchorPane messagesPane;
    @FXML
    private ScrollPane conversation;
    @FXML
    private ListView<Message> messageListView;
    @FXML
    private ListView<User> chatsList;
    @FXML
    private TextField messageTextField;

    private MessageService messageService;
    private Network userService;
    private User currentUser;
    private User chatUser;

    public void setServices(Network userService, MessageService messageService, User currentUser) {
        this.userService = userService;
        this.messageService = messageService;
        this.currentUser = currentUser;
        loadFriends();
    }

    private void loadFriends() {
        chatsList.setItems(FXCollections.observableArrayList(
                userService.getFriends(currentUser.getId())
        ));
        chatsList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                chatUser = newValue;
                loadMessages();
            }
        });
    }

    private void loadMessages() {
        if (chatUser == null) {
            return;
        }
        Iterable<Message> messages = messageService.getMessagesBetweenUsers(currentUser.getId(), chatUser.getId());
        messageListView.setItems(FXCollections.observableArrayList(
                StreamSupport.stream(messages.spliterator(), false)
                        .collect(Collectors.toList())
        ));
        messageListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Message message, boolean empty) {
                super.updateItem(message, empty);
                if (empty || message == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    String senderName = message.getSenderId().equals(currentUser.getId()) ? "You" : chatUser.getFirstName();
                    Label contentLabel = new Label(senderName + ": " + message.getText());
                    contentLabel.setWrapText(true);
                    setGraphic(contentLabel);
                }
            }
        });
        conversation.setContent(messageListView);
    }

    @FXML
    private void sendMessageButtonClicked() {
        String text = messageTextField.getText();
        if (!text.isEmpty() && chatUser != null) {
            messageService.saveMessage(text, LocalDateTime.now(), currentUser.getId(), chatUser.getId());
            messageTextField.clear();
            loadMessages();
        }
    }
}