package retea.reteadesocializare;

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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import retea.reteadesocializare.domain.Group;
import retea.reteadesocializare.domain.Message;
import retea.reteadesocializare.domain.User;
import retea.reteadesocializare.service.Service;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class GroupConversationController implements Initializable {

    Long ID;
    Long selectedGroupID;

    @FXML
    private ListView<String> MessageDateList;

    @FXML
    private ScrollPane MessageList;

    @FXML
    private ListView<Label> MessageListRight;

    @FXML
    private TextField MessageTextField;

    @FXML
    private Button WriteMessageButton;

    @FXML
    private BorderPane BorderPane;

    @FXML
    private Button reportsButton;

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
    private ImageView logoBackToMainMenu;

    @FXML
    private Label InfoLabel;

    private Service service;

    private Parent root;

    public void setService(Service service, Long id, Long selectedGroupId) {
        this.service = service;
        this.ID=id;
        this.selectedGroupID=selectedGroupId;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources){
        reloadConversation();

    }

    private String adjustText(String str){
        int position=30;
        while(position<str.length()) {
            str=str.substring(0, position) + '\n' + str.substring(position);
            position+=30;
        }
        return str;
    }

    void reloadConversation() {
        List<Message> messages = service.seeGroupConversation(selectedGroupID);
        VBox vbox=new VBox(3);

        vbox.setPadding(new Insets(0, 10, 15, 10));

        HBox toUserHBox=new HBox(40);

        Label InfoLabel=new Label();
        InfoLabel.getStyleClass().add("infoLabel");
        Group group=service.findGroup(selectedGroupID);
        InfoLabel.setText(group.getName());
        InfoLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 16));

        ImageView groupAvatar=new ImageView();
        Image image= service.loadGroupAvatar(selectedGroupID);
        groupAvatar.setImage(image);
        groupAvatar.setPreserveRatio(true);
        groupAvatar.setFitWidth(50);
        groupAvatar.setFitHeight(50);

        toUserHBox.getChildren().add(groupAvatar);
        toUserHBox.getChildren().add(InfoLabel);

        vbox.getChildren().add(toUserHBox);
        for (Message message : messages) {
            if(message.getFrom().getId().equals(ID)) {
                HBox hbox=new HBox();
                Label messageLabel=new Label();
                hbox.setAlignment(Pos.BASELINE_RIGHT);
                messageLabel.setText(adjustText(message.getMessageText()));
                messageLabel.getStyleClass().add("messageReceivedLabel");
                //messageLabel.setAlignment(Pos.CENTER_RIGHT);
                VBox vboxMessage=new VBox();
                Label messageLabel2=new Label();
                messageLabel2.setVisible(false);
                messageLabel2.setPrefWidth(230);

                Label nameDateLabel=new Label();
                nameDateLabel.setFont(new Font("Arial",7));
                nameDateLabel.getStyleClass().add("invisibleLabel");
                nameDateLabel.setText(message.getDate().toString().substring(0,10)+"    "+message.getDate().toString().substring(11,16));
                vboxMessage.getChildren().add(nameDateLabel);
                vboxMessage.getChildren().add(messageLabel);
                vboxMessage.setAlignment(Pos.BASELINE_RIGHT);
                vbox.getChildren().add(vboxMessage);
                //messageListRight.add(message.getMessageText());
                //messageList.add("");
            }
            else{
                //messageList.add(message.getMessageText());
                //messageListRight.add("");
                Label messageLabel=new Label();
                messageLabel.setText(adjustText(message.getMessageText()));

                Label userLabel=new Label();
                userLabel.setFont(Font.font(null, FontWeight.BOLD, 7));
                userLabel.setText(message.getFrom().toString());
                userLabel.getStyleClass().add("invisibleLabel");
                Label nameDateLabel=new Label();
                nameDateLabel.setFont(new Font("Arial",7));
                nameDateLabel.getStyleClass().add("invisibleLabel");

                nameDateLabel.setText(message.getDate().toString().substring(0,10)+"    "+message.getDate().toString().substring(11,16));

                HBox userDate=new HBox();
                userDate.getChildren().add(userLabel);
                userDate.getChildren().add(nameDateLabel);

                VBox vboxMessage=new VBox();
                vboxMessage.getChildren().add(userDate);
                vboxMessage.getChildren().add(messageLabel);

                vbox.getChildren().add(vboxMessage);
            }

        }
        MessageList.setContent(vbox);
        MessageList.setVvalue(1D);
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

    @FXML
    void WriteMessageButtonClicked(MouseEvent event) {
        String messageText=MessageTextField.getText();
        service.saveMessageToGroup(ID,messageText,selectedGroupID);
        reloadConversation();
        MessageTextField.setText("");
    }

    @FXML
    void refreshButtonClicked(MouseEvent event) {
        reloadConversation();
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

