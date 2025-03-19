package org.example.finalsocialnetwork.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.example.finalsocialnetwork.domain.User;
import org.example.finalsocialnetwork.service.MessageService;
import org.example.finalsocialnetwork.service.Network;

import java.io.IOException;
import java.util.Optional;

public class LogIn {

    @FXML
    public Text usernameError;

    @FXML
    private TextField username;

    private Network service;
    private MessageService messageService;

    public void setService(Network service, MessageService messageService) {
        this.service = service;
        this.messageService = messageService;
    }
    @FXML
    void loginButtonClicked(ActionEvent event) throws IOException {
        Optional<User> user = service.getUserByUsername(username.getText());
        if (user.isEmpty()) {
            usernameError.setVisible(true);
        }
        else {
            usernameError.setVisible(false);
            FXMLLoader stageLoader = new FXMLLoader();
            stageLoader.setLocation(getClass().getResource("/org/example/finalsocialnetwork/MainApplication.fxml"));
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            AnchorPane appLayout = stageLoader.load();
            Scene scene = new Scene(appLayout);
            stage.setScene(scene);

            UserController appController = stageLoader.getController();

            appController.setService(this.service, this.messageService);
            appController.initApp(user.get());

            stage.show();
        }

    }

}
