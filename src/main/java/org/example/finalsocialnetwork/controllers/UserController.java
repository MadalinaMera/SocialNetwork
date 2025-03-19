package org.example.finalsocialnetwork.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import org.example.finalsocialnetwork.Constants;
import org.example.finalsocialnetwork.domain.Friendship;
import org.example.finalsocialnetwork.domain.Message;
import org.example.finalsocialnetwork.domain.Pair;
import org.example.finalsocialnetwork.domain.User;
import org.example.finalsocialnetwork.domain.validators.MessageValidator;
import org.example.finalsocialnetwork.domain.validators.ValidationException;
import org.example.finalsocialnetwork.service.MessageService;
import org.example.finalsocialnetwork.service.Network;
import org.example.finalsocialnetwork.util.Page;
import org.example.finalsocialnetwork.util.Pageable;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class UserController implements Initializable {

    @FXML
    private AnchorPane mainpane;

    @FXML
    private Label PageTitle;

    @FXML
    private Label username;

    @FXML
    private TextField txt_username;

    @FXML
    private Button addbutton;

    @FXML
    private Button deletebutton;

    @FXML
    private ComboBox<String> users;

    @FXML
    private ListView<Pair<User, LocalDateTime>> friendRequests;

    @FXML
    private TableView<Map<String, String>> friends_table;
    @FXML
    private TableColumn<Map<String, String>, String> username_column;
    @FXML
    private TableColumn<Map<String, String>, String>  firstname_column;
    @FXML
    private TableColumn<Map<String, String>, String> lastname_column;
    @FXML
    private TableColumn<Map<String, String>, String> friendsSince;



    @FXML
    private TabPane mainTabPane;

    @FXML
    private Button prevButton;
    @FXML
    private Button nextButton;

    @FXML
    private Label pageNumber;

    @FXML
    private TextField newMessage;

    private final ObservableList<Map<String, String>> friendsObs = FXCollections.observableArrayList();
    private final ObservableList<String> userObs = FXCollections.observableArrayList();
    private User user;
    private Network service;
    private MessageService messageService;
    private MessagesController messagesController;

    public void setService(Network service, MessageService messageService) {
        this.service = service;
        this.messageService = messageService;
    }

    public Network getService() {
        return service;
    }

    private int currentPage = 0;
    private int pageSize = 2;
    private int numberOfElements = 0;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        username_column.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get("username")));
        firstname_column.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get("firstName")));
        lastname_column.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get("lastName")));
        friendsSince.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get("friendshipDate")));
        friends_table.setItems(friendsObs);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/finalsocialnetwork/MessagesView.fxml"));
            Parent messagesView = loader.load();
            this.messagesController = loader.getController();
            Tab messagesTab = mainTabPane.getTabs().stream()
                    .filter(tab -> "Messages".equals(tab.getText()))
                    .findFirst()
                    .orElse(null);
            if (messagesTab != null) {
                messagesTab.setContent(messagesView);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initApp(User user) {
        this.user = user;
        currentPage = 0;
        initModel();
    }

    private void initModel() {
        Page<User> page = service.findAllOnPage(new Pageable(currentPage, pageSize),user.getId());

        List<Map<String, String>> friends = new ArrayList<>();
        for (User u : page.getElementsOnPage()) {
            Map<String, String> friend = new HashMap<>();
            Optional<Friendship> f = service.getOneFriendship(user.getId(), u.getId());
            if (!f.isPresent()) {
                f = service.getOneFriendship(u.getId(), user.getId());
            }
            if (f.isPresent()) {
                friend.put("username", u.getUsername());
                friend.put("firstName", u.getFirstName());
                friend.put("lastName", u.getLastName());
                friend.put("friendshipDate", f.get().getDate().format(Constants.DATE_TIME_FORMATTER));
            }
            friends.add(friend);
        }

        friendsObs.setAll(friends);
        service.getAllUsers().forEach(u->{
            if(!u.equals(user) && service.getOneFriendship(user.getId(), u.getId()).isEmpty() && service.getOneFriendship(u.getId(), user.getId()).isEmpty()) {
                String result = u.getUsername() + ":" + u.getFirstName() + " " + u.getLastName();
                userObs.add(result);
            }
        });
        users.setItems(userObs);

        // Gestionam butoanele de navigare
        prevButton.setDisable(currentPage == 0);
        int noOfPages = (int) Math.ceil((double) page.getTotalNumberElements() / pageSize);
        nextButton.setDisable(currentPage + 1 == noOfPages);
        pageNumber.setText((currentPage + 1) + " / " + noOfPages);
    }


    @FXML
    void removeFriend(){
        Map<String,String> selectedItem = friends_table.getSelectionModel().getSelectedItem();
        if(selectedItem == null){
            return;
        }
        String username = selectedItem.get("username");
        service.getUserByUsername(username).ifPresent(u->{
            Long id1 = Math.min(user.getId(), u.getId());
            Long id2 = Math.max(user.getId(), u.getId());
            service.removeFriendship(id1, id2);
            friendsObs.remove(selectedItem);
            userObs.add(u.getUsername() + ":" + u.getFirstName() + " " + u.getLastName());
        });
        friends_table.refresh();

    }

    @FXML
    void addFriend(){
        String selectedItem = users.getSelectionModel().getSelectedItem();
        if(selectedItem == null){
            return;
        }
        String username = selectedItem.split(":")[0];
        service.getUserByUsername(username).ifPresent(u->{
            Long idFriend = u.getId();
            try {
                Friendship f = service.addFriendship(user.getId(),idFriend);

                ///sterg din lista de useri cu care se poate imprieteni
                userObs.remove(selectedItem);

                ///adaug in lista de prieteni observabili
                Map<String, String> friend = new HashMap<>();
                friend.put("username", u.getUsername());
                friend.put("firstName", u.getFirstName());
                friend.put("lastName", u.getLastName());
                friend.put("friendshipDate", f.getDate().format(Constants.DATE_TIME_FORMATTER));
                friendsObs.add(friend);
                friends_table.refresh();
            }
            catch(ValidationException e){
                System.out.println(e.getMessage());
            }
        });

    }

    public void onPreviousPage(ActionEvent actionEvent) {
        currentPage--;
        initModel();
    }


    public void onNextPage(ActionEvent actionEvent) {
        currentPage++;
        initModel();
    }


    @FXML
    private void onMessagesTabSelected() {
        if (messagesController != null) {
            messagesController.setServices(service,messageService, user);
        }
    }

}