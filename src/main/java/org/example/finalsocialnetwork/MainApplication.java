package org.example.finalsocialnetwork;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.example.finalsocialnetwork.controllers.LogIn;
import org.example.finalsocialnetwork.domain.User;
import org.example.finalsocialnetwork.domain.validators.FriendshipValidator;
import org.example.finalsocialnetwork.domain.validators.MessageValidator;
import org.example.finalsocialnetwork.domain.validators.UserValidator;
import org.example.finalsocialnetwork.repository.Repository;
import org.example.finalsocialnetwork.repository.database.FriendshipDBRepository;
import org.example.finalsocialnetwork.repository.database.MessageDBRepository;
import org.example.finalsocialnetwork.repository.database.UserDBRepository;
import org.example.finalsocialnetwork.service.FriendshipService;
import org.example.finalsocialnetwork.service.MessageService;
import org.example.finalsocialnetwork.service.Network;
import org.example.finalsocialnetwork.service.UserService;

import javax.sql.DataSource;
import java.io.IOException;

public class MainApplication extends Application {

    private Network network;
    private MessageService messageService;
    @Override
    public void start(Stage primaryStage) throws Exception{

        DataSource dataSource = DataSourceManager.getDataSource();
        Repository<Long, User> userDBRepository = new UserDBRepository(new UserValidator(),dataSource);
        FriendshipDBRepository friendshipDBRepository = new FriendshipDBRepository(new FriendshipValidator(), dataSource);
        MessageDBRepository messageDBRepository = new MessageDBRepository(new MessageValidator(), dataSource);

        UserService userService = new UserService(userDBRepository);
        FriendshipService friendshipService = new FriendshipService(friendshipDBRepository);
        this.messageService = new MessageService(messageDBRepository);
        this.network = new Network(userService, friendshipService);
        initView(primaryStage);
        primaryStage.show();

    }

    private void initView(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/org/example/finalsocialnetwork/LogIn.fxml"));
        AnchorPane LogInLayout = fxmlLoader.load();
        primaryStage.setScene(new Scene(LogInLayout));
        primaryStage.setTitle("Social Network");

        LogIn logInController = fxmlLoader.getController();
        logInController.setService(this.network, this.messageService);
    }

    public static void main(String[] args) {
        launch();
    }
}