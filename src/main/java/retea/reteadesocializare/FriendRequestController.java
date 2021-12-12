package retea.reteadesocializare;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import retea.reteadesocializare.domain.Friendship;
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
    private ListView<String> FriendRequestListDate;


    private Service service;

    private Parent root;


    public void setService(Service service, Long id) {
        this.service = service;
        this.ID=id;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources){
        List<Friendship> friendshipList = service.pendingFriendships(ID);
        List<User> userList =  new ArrayList<>();
        List<String> dateList = new ArrayList<>();
        for (Friendship friendship : friendshipList){
            userList.add(service.findOneUser(friendship.getSender()));
            dateList.add(friendship.getDate());
        }
        ObservableList<User> items = FXCollections.observableArrayList(userList);
        ObservableList<String> items1 = FXCollections.observableArrayList(dateList);
        FriendRequestListDate.setMouseTransparent( true );
        FriendRequestListDate.setFocusTraversable( false );
        FriendRequests.setItems(items);
        FriendRequestListDate.setItems(items1);


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
    void LogOutButtonClicked(MouseEvent event) throws IOException {
        ((Node)(event.getSource())).getScene().getWindow().hide();
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);

        stage.setTitle("Log In");
        stage.setScene(scene);
        stage.show();
    }

    void reloadItems(){
        List<Friendship> friendshipList = service.pendingFriendships(ID);
        List<User> userList =  new ArrayList<>();
        List<String> dateList = new ArrayList<>();
        for (Friendship friendship : friendshipList){
            userList.add(service.findOneUser(friendship.getSender()));
            dateList.add(friendship.getDate());
        }
        ObservableList<User> items = FXCollections.observableArrayList(userList);
        ObservableList<String> items1 = FXCollections.observableArrayList(dateList);
        FriendRequestListDate.setMouseTransparent( true );
        FriendRequestListDate.setFocusTraversable( false );
        FriendRequests.setItems(items);
        FriendRequestListDate.setItems(items1);

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
}
