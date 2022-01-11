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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import retea.reteadesocializare.domain.Event;
import retea.reteadesocializare.domain.Group;
import retea.reteadesocializare.service.Service;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class EventController implements Initializable {

    Long ID;

    @FXML
    private ListView<Event> EventsList;


    @FXML
    private BorderPane BorderPane;

    @FXML
    private Label ErrorMessageLoginIn;

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
    private Button SearchButton;

    @FXML
    private Button LogOutButton;

    @FXML
    private TextField SearchBar;

    @FXML
    private GridPane GridPaneListFriends;

    @FXML
    private Button CreateEvent;

    @FXML
    private ImageView logoBackToMainMenu;

    private Service service;

    private Parent root;

    public void setService(Service service, Long id) {
        this.service = service;
        this.ID=id;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources){
        ObservableList<Event> items = FXCollections.observableArrayList (
                service.findEvents(ID));
        EventsList.setItems(items);

       EventsList.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {

                if (event.getClickCount() == 2) {
                    Event selectedItem = EventsList.getSelectionModel().getSelectedItem();
                    Long idEvent = selectedItem.getId();
                    EventDetailsController eventDetailsController = new EventDetailsController();
                    FXMLLoader loader= new FXMLLoader(getClass().getResource("eventDetails-view.fxml"));
                    eventDetailsController.setService(service,ID,idEvent);
                    loader.setController(eventDetailsController);
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

        EventsList.setCellFactory(param -> new ListCell<Event>() {
            private ImageView imageView = new ImageView();
            @Override
            public void updateItem(Event event, boolean empty) {
                super.updateItem(event, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Image image= service.loadEventAvatar(event.getId());
                    imageView.setImage(image);
                    imageView.setPreserveRatio(true);
                    imageView.setFitHeight(60);
                    imageView.setFitHeight(60);
                    setText(event.getName());
                    setGraphic(imageView);
                }
            }
        });
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

    @FXML
    void CreateEventButtonClicked(MouseEvent event) throws IOException{
        CreateEventController searchUsersController = new CreateEventController();
        FXMLLoader loader= new FXMLLoader(getClass().getResource("createEvent-view.fxml"));
        searchUsersController.setService(service,ID);
        loader.setController(searchUsersController);
        root=loader.load();

        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene=new Scene(root);
        stage.setTitle("CyberBear");
        stage.setScene(scene);

        stage.show();
    }


}

