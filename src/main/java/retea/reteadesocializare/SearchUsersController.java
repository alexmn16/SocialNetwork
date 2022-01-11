package retea.reteadesocializare;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
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

public class SearchUsersController implements Initializable {

    Long ID;
    String text;

    @FXML
    private ListView<Entity> EntityList;


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

    private Service service;

    private Parent root;

    public void setService(Service service, Long id, String text) {
        this.service = service;
        this.ID=id;
        this.text=text;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources){
        seeDetailsButton.setVisible(false);
        AddFriendButton.setVisible(false);
        CancelRequestButton.setVisible(false);
        reloadList();
    }

    @FXML
    private Button CancelRequestButton;

    @FXML
    void AddFriendButtonClicked(MouseEvent event) {
        ErrorMessageAddFriend.setText("");

        User selectedUser=(User)EntityList.getSelectionModel().getSelectedItem();
        Long idTo = selectedUser.getId();
        try{
            service.sendFriendRequest(ID, idTo);

        }catch(ServiceException ex){
            ErrorMessageAddFriend.setText(ex.getMessage());
        }
        reloadList();

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

    public void reloadList(){
        Iterable<User> users = service.findAllUsersStartsWith(text);
        List<User> currentUserFriends=service.getUserFriends(ID);
        int numberOfFriends=currentUserFriends.size();
        Iterable<Event> events = service.findEventsStartsWith(text);
        List<Entity> theList = new ArrayList<>();


        for(User user : users) {
            if(user.getId() != ID )
                theList.add(user);
        }

        for(Event event : events)
            theList.add(event);

        ObservableList<Entity> items = FXCollections.observableArrayList (theList);
        EntityList.setItems(items);

        EntityList.setCellFactory(param -> new ListCell<Entity>() {
            private ImageView imageView = new ImageView();
            @Override
            public void updateItem(Entity entity, boolean empty) {
                super.updateItem(entity, empty);

                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {

                    if( entity instanceof User) {
                        User user = (User)(entity);
                        Image image = service.loadAvatar(user.getId());
                        imageView.setImage(image);
                        imageView.setPreserveRatio(true);
                        imageView.setFitHeight(60);
                        imageView.setFitHeight(60);
                        if(currentUserFriends.contains(user)) {
                            //setText(user.getFirstName() + " " + user.getLastName());
                            setText(null);
                            HBox hbox = new HBox(30);
                            Label nameLabel = new Label();
                            nameLabel.setText("\n" + user.getFirstName() + " " + user.getLastName());
                            Label typeLabel = new Label();
                            typeLabel.setText("Friend");
                            typeLabel.setFont(Font.font("System", FontPosture.ITALIC,
                                    Font.getDefault().getSize()));

                            typeLabel.setStyle("");
                            hbox.getChildren().add(imageView);
                            VBox userInfo=new VBox(3);
                            userInfo.getChildren().add(nameLabel);
                            userInfo.getChildren().add(typeLabel);
                            hbox.getChildren().add(userInfo);
                            setGraphic(hbox);
                        }
                        else{
                            setText(null);
                            HBox hbox = new HBox(30);
                            Label name=new Label();
                            name.setText("\n"+user.getFirstName() + " " + user.getLastName());
                            hbox.getChildren().add(imageView);
                            hbox.getChildren().add(name);
                            setGraphic(hbox);
                        }
                    } else {
                        Event event = (Event)(entity);
                        Image image = service.loadEventAvatar(event.getId());
                        imageView.setImage(image);
                        imageView.setPreserveRatio(true);
                        imageView.setFitHeight(60);
                        imageView.setFitHeight(60);
                        setText(null);
                        HBox hbox = new HBox(30);
                        Label nameLabel = new Label();
                        nameLabel.setText("\n" + event.getName());
                        Label typeLabel = new Label();
                        typeLabel.setText("Event");
                        typeLabel.setFont(Font.font("System", FontPosture.ITALIC,
                                Font.getDefault().getSize()));

                        typeLabel.setStyle("");
                        hbox.getChildren().add(imageView);
                        VBox userInfo=new VBox(3);
                        userInfo.getChildren().add(nameLabel);
                        userInfo.getChildren().add(typeLabel);
                        hbox.getChildren().add(userInfo);
                        setGraphic(hbox);
                    }
                }
            }
        });
        EntityList.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {

                if (event.getClickCount() == 1) {
                    Entity selectedEntity=EntityList.getSelectionModel().getSelectedItem();
                    if(selectedEntity instanceof User){
                        seeDetailsButton.setVisible(false);
                        AddFriendButton.setVisible(true);
                        CancelRequestButton.setVisible(true);
                    }
                    else {
                        AddFriendButton.setVisible(false);
                        CancelRequestButton.setVisible(false);
                        seeDetailsButton.setVisible(true);
                    }
                }
            }
        });
    }

    @FXML
    void CancelRequestButtonClicked(MouseEvent event) {
        ErrorMessageAddFriend.setText("");

        User selectedUser=(User)EntityList.getSelectionModel().getSelectedItem();
        Long idTo = selectedUser.getId();
        List<User> users=service.getUserSentFriendRequests(ID);
        boolean found=false;
        for(User user: users){
            Long idUser=user.getId();
            if(idUser==idTo && idTo<ID) {
                service.deleteFriendship(new Tuple<>(idTo, ID));
                found=true;
            }
            else if(idUser==idTo && idTo>ID) {
                service.deleteFriendship(new Tuple<>(ID, idTo));
                found=true;
            }
        }
        if(found==false)
            ErrorMessageAddFriend.setText("You haven't sent any friend request");

        reloadList();
    }

    @FXML
    void seeDetailsButtonClicked(MouseEvent event) throws IOException{
        Event selectedItem = (Event)EntityList.getSelectionModel().getSelectedItem();
        Long idEvent = selectedItem.getId();
        EventDetailsController eventDetailsController = new EventDetailsController();
        FXMLLoader loader= new FXMLLoader(getClass().getResource("eventDetails-view.fxml"));
        eventDetailsController.setService(service,ID,idEvent);
        loader.setController(eventDetailsController);
        root=loader.load();

        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene=new Scene(root);
        stage.setTitle("CyberBear");
        stage.setScene(scene);

        stage.show();
    }
}
