package retea.reteadesocializare;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import retea.reteadesocializare.domain.*;
import retea.reteadesocializare.domain.validators.ValidationException;
import retea.reteadesocializare.service.Service;
import retea.reteadesocializare.service.ServiceException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Scanner;

public class EventDetailsController implements Initializable {

    Long ID;
    Long idEvent;

    @FXML
    private Button reportsButton;

    @FXML
    private BorderPane BorderPane;

    @FXML
    private Label ErrorMessageLoginIn;

    @FXML
    private Label ErrorMessageAddFriend;

    @FXML
    private Label welcomeText;

    @FXML
    private Button LogInButton;

    @FXML
    private TextField LoginTextField;

    @FXML
    private TextField EditProfileTextField;

    @FXML
    private Button FriendRequestsButton;

    @FXML
    private Button FriendsListButton;

    @FXML
    private Button LogOutButton;

    @FXML
    private Button SearchButton;

    @FXML
    private TextField SearchBar;

    @FXML
    private GridPane GridPaneListFriends;

    @FXML
    private ImageView logoBackToMainMenu;

    @FXML
    private Button AddFriendButton;

    @FXML
    private Button seeDetailsButton;

    @FXML
    private Button unsubscribeButton;

    @FXML
    private Label eventCreator;

    @FXML
    private Label eventDate;

    @FXML
    private Label eventDescription;

    @FXML
    private ImageView eventImage;

    @FXML
    private Label eventLocation;

    @FXML
    private Label eventName;

    @FXML
    private ListView<User> eventParticipants;

    @FXML
    private Button subscribeButton;

    @FXML
    private Button notificationsOffButton;

    @FXML
    private Button notificationsOnButton;



    private Service service;

    private Parent root;

    public void setService(Service service, Long idUser, Long idEvent) {
        this.service = service;
        this.ID = idUser;
        this.idEvent = idEvent;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        reloadList();
        Event event = service.findEvent(idEvent);
        User user = service.findOneUser(event.getCreator());
        eventName.setText(event.getName());
        eventCreator.setText("Organised by: " + user.getFirstName() + " " + user.getLastName());
        eventImage.setImage(service.loadEventAvatar(idEvent));
        eventDate.setText("Date: \n" + event.getDate());
        eventDescription.setText(event.getDescription());
        eventLocation.setText("Location: \n" + event.getLocation());
        if(service.checkUserEvent(ID, idEvent) == false) {
            unsubscribeButton.setVisible(false);
            notificationsOffButton.setVisible(false);
            notificationsOnButton.setVisible(false);
            subscribeButton.setVisible(true);
        }else {
            if(service.checkUserEventNotifications(ID, idEvent)) {
                unsubscribeButton.setVisible(true);
                notificationsOffButton.setVisible(true);
                notificationsOnButton.setVisible(false);
                subscribeButton.setVisible(false);
            }
            else{
                unsubscribeButton.setVisible(true);
                notificationsOffButton.setVisible(false);
                notificationsOnButton.setVisible(true);
                subscribeButton.setVisible(false);
            }
        }



    }


    @FXML
    void FriendRequestsButtonClicked(MouseEvent event) throws IOException {
        FriendRequestController friendRequestController = new FriendRequestController();
        FXMLLoader loader= new FXMLLoader(getClass().getResource("friendRequest-view.fxml"));
        friendRequestController.setService(service,ID);
        loader.setController(friendRequestController);
        root=loader.load();

        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene=new Scene(root);
        stage.setTitle("CyberBear");
        stage.setScene(scene);

        stage.show();
    }

    @FXML
    void FriendsListButtonClicked(MouseEvent event) throws IOException {
        FriendsListController friendsListController = new FriendsListController();
        FXMLLoader loader= new FXMLLoader(getClass().getResource("friendsList-view.fxml"));
        friendsListController.setService(service,ID);
        loader.setController(friendsListController);
        root=loader.load();

        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene=new Scene(root);
        stage.setTitle("CyberBear");
        stage.setScene(scene);

        stage.show();

    }

    @FXML
    void backToMainMenu(MouseEvent event) throws IOException{
        MainMenuController mainMenuController = new MainMenuController();
        FXMLLoader loader= new FXMLLoader(getClass().getResource("mainMenu-view.fxml"));
        mainMenuController.setService(service,ID);
        loader.setController(mainMenuController);
        root=loader.load();

        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene=new Scene(root);
        stage.setTitle("CyberBear");
        stage.setScene(scene);

        stage.show();
    }

    @FXML
    void LogOutButtonClicked(MouseEvent event) throws IOException {
        ((Node)(event.getSource())).getScene().getWindow().hide();
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        Image image = new Image(getClass().getResource("images/logoIcon.PNG").toExternalForm());
        stage.getIcons().add(image);
        stage.setTitle("Log In");
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    void searchButtonClicked(MouseEvent event) throws IOException{
        String text = SearchBar.getText();
        SearchUsersController searchUsersController = new SearchUsersController();
        FXMLLoader loader= new FXMLLoader(getClass().getResource("searchUser-view.fxml"));
        searchUsersController.setService(service,ID,text);
        loader.setController(searchUsersController);
        root=loader.load();

        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene=new Scene(root);
        stage.setTitle("CyberBear");
        stage.setScene(scene);

        stage.show();
    }

    public void reloadList(){
        ObservableList<User> items = FXCollections.observableArrayList (
                service.findUsersEvent(idEvent));
        eventParticipants.setItems(items);

        eventParticipants.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {

                if (event.getClickCount() == 2) {
                    ConversationController conversationController = new ConversationController();
                    FXMLLoader loader= new FXMLLoader(getClass().getResource("conversation-view.fxml"));
                    User selectedUser= eventParticipants.getSelectionModel().getSelectedItem();
                    conversationController.setService(service,ID,selectedUser.getId());
                    loader.setController(conversationController);
                    try {
                        root=loader.load();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
                    Scene scene=new Scene(root);
                    stage.setTitle("CyberBear");
                    stage.setScene(scene);

                    stage.show();
                }
            }
        });

        eventParticipants.setCellFactory(param -> new ListCell<User>() {
            private ImageView imageView = new ImageView();
            @Override
            public void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Image image= service.loadAvatar(user.getId());
                    imageView.setImage(image);
                    imageView.setPreserveRatio(true);
                    imageView.setFitHeight(30);
                    imageView.setFitWidth(30);
                    setText(user.getFirstName()+" "+user.getLastName());
                    setGraphic(imageView);
                }
            }
        });
    }


    @FXML
    void subscribeButtonClicked(MouseEvent event) {
        service.addUserToEvent(idEvent, ID);
        unsubscribeButton.setVisible(true);
        notificationsOnButton.setVisible(false);
        notificationsOffButton.setVisible(true);
        subscribeButton.setVisible(false);
        reloadList();
    }

    @FXML
    void unsubscribeButtonClicked(MouseEvent event) {
        service.deleteUserFromEvent(ID, idEvent);
        unsubscribeButton.setVisible(false);
        notificationsOnButton.setVisible(false);
        notificationsOffButton.setVisible(false);
        subscribeButton.setVisible(true);
        reloadList();
    }
    @FXML
    void notificationsOffButtonClicked(MouseEvent event) {
        notificationsOnButton.setVisible(true);
        notificationsOffButton.setVisible(false);
        service.setNotificationsOff(ID,idEvent);
    }

    @FXML
    void notificationsOnButtonClicked(MouseEvent event) {
        notificationsOnButton.setVisible(false);
        notificationsOffButton.setVisible(true);
        service.setNotificationsOn(ID,idEvent);
    }

    @FXML
    void reportsButtonClicked(MouseEvent event) throws IOException {
        ReportsController mainMenuController = new ReportsController();
        FXMLLoader loader= new FXMLLoader(getClass().getResource("reports-view.fxml"));
        mainMenuController.setService(service,ID);
        loader.setController(mainMenuController);
        root=loader.load();

        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene=new Scene(root);
        stage.setTitle("CyberBear");
        stage.setScene(scene);

        stage.show();
    }

    @FXML
    void GroupsButtonClicked(MouseEvent event) throws IOException{
        GroupsController groupsController = new GroupsController();
        FXMLLoader loader= new FXMLLoader(getClass().getResource("groups-view.fxml"));
        groupsController.setService(service,ID);
        loader.setController(groupsController);
        root=loader.load();

        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene=new Scene(root);
        stage.setTitle("CyberBear");
        stage.setScene(scene);

        stage.show();
    }


    @FXML
    void EventsButtonClicked(MouseEvent event) throws IOException{
        EventController eventController = new EventController();
        FXMLLoader loader= new FXMLLoader(getClass().getResource("events-view.fxml"));
        eventController.setService(service,ID);
        loader.setController(eventController);
        root=loader.load();

        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene=new Scene(root);
        stage.setTitle("CyberBear");
        stage.setScene(scene);

        stage.show();
    }

    @FXML
    void homeButtonClicked(MouseEvent event) throws IOException {
        MainMenuController mainMenuController = new MainMenuController();
        FXMLLoader loader= new FXMLLoader(getClass().getResource("mainMenu-view.fxml"));
        mainMenuController.setService(service,ID);
        loader.setController(mainMenuController);
        root=loader.load();

        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene=new Scene(root);
        stage.setTitle("CyberBear");
        stage.setScene(scene);

        stage.show();
    }

}
