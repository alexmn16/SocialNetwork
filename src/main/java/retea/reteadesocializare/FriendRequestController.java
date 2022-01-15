package retea.reteadesocializare;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import retea.reteadesocializare.domain.Friendship;
import retea.reteadesocializare.domain.Tuple;
import retea.reteadesocializare.domain.User;
import retea.reteadesocializare.domain.validators.ValidationException;
import retea.reteadesocializare.service.Service;
import retea.reteadesocializare.service.ServiceException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Scanner;

public class FriendRequestController implements Initializable {

    Long ID;

    @FXML
    private ListView<User> FriendRequests;


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
    private Button reportsButton;

    @FXML
    private Button LogOutButton;

    @FXML
    private Button SearchButton;

    @FXML
    private TextField SearchBar;

    @FXML
    private GridPane GridPaneListFriends;

    @FXML
    private Button acceptFriendRequestButton;

    @FXML
    private Button rejectFriendRequestButton;

    @FXML
    private ImageView logoBackToMainMenu;

    @FXML
    private ListView<String> FriendRequestListDate;


    private Service service;

    private Parent root;


    public void setService(Service service, Long id) {
        this.service = service;
        this.ID=id;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources){
       // List<Friendship> friendshipList = service.pendingFriendships(ID);
        List<User> userList =  service.pendingFriendships(ID);
        /*List<String> dateList = new ArrayList<>();
        for (Friendship friendship : friendshipList){
            userList.add(service.findOneUser(friendship.getSender()));
            dateList.add(friendship.getDate());
        } */

        ObservableList<User> items = FXCollections.observableArrayList(userList);
        //ObservableList<String> items1 = FXCollections.observableArrayList(dateList);
        //FriendRequestListDate.setMouseTransparent( true );
        //FriendRequestListDate.setFocusTraversable( false );
        FriendRequests.setItems(items);
        //FriendRequestListDate.setItems(items1);

        FriendRequests.setCellFactory(param -> new ListCell<User>() {
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
                    imageView.setFitHeight(60);
                    imageView.setFitHeight(60);
                    Long id_friend = user.getId();
                    Long id_me = ID;
                    if(id_me > id_friend){
                        Long swap = id_me;
                        id_me = id_friend;
                        id_friend = swap;
                    }
                    Friendship friendship = service.findOneFriendship(new Tuple<Long, Long> (id_me, id_friend));
                    String date = friendship.getDate();
                    setText(user.getFirstName()+" "+user.getLastName()+ "           "+ date);
                    setGraphic(imageView);
                }
            }
        });

      /*  FriendRequestListDate.setCellFactory(param -> new ListCell<String>() {
            private ImageView imageView = new ImageView();
            @Override
            public void updateItem(String date, boolean empty) {
                super.updateItem(date, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    imageView.setPreserveRatio(true);
                    imageView.setFitHeight(60);
                    imageView.setFitHeight(60);
                    setText(date);
                    setGraphic(imageView);
                }
            }
        });
*/
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
    void acceptFriendRequestButtonClicked(MouseEvent event) {
        Long idFrom;
        User selectedUser = FriendRequests.getSelectionModel().getSelectedItem();
        idFrom = selectedUser.getId();
        String status = "approved";
        service.responseToFriendRequest( idFrom, ID, status);
        reloadItems();
    }

    @FXML
    void rejectFriendRequestButtonClicked(MouseEvent event) {
        Long idFrom;
        User selectedUser = FriendRequests.getSelectionModel().getSelectedItem();
        idFrom = selectedUser.getId();
        String status = "rejected";
        service.responseToFriendRequest( idFrom, ID, status);
        reloadItems();

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

    void reloadItems(){
        //List<Friendship> friendshipList = service.pendingFriendships(ID);
        List<User> userList =  service.pendingFriendships(ID);
        /*List<String> dateList = new ArrayList<>();
        for (Friendship friendship : friendshipList){
            userList.add(service.findOneUser(friendship.getSender()));
            dateList.add(friendship.getDate());
        }*/
        ObservableList<User> items = FXCollections.observableArrayList(userList);
        //ObservableList<String> items1 = FXCollections.observableArrayList(dateList);
        FriendRequestListDate.setMouseTransparent( true );
        FriendRequestListDate.setFocusTraversable( false );
        FriendRequests.setItems(items);
        //FriendRequestListDate.setItems(items1);

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
