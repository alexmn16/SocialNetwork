package retea.reteadesocializare;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import retea.reteadesocializare.domain.Message;
import retea.reteadesocializare.domain.User;
import retea.reteadesocializare.service.Service;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static javafx.scene.control.PopupControl.USE_COMPUTED_SIZE;
import static javafx.scene.control.PopupControl.USE_PREF_SIZE;

public class ConversationController implements Initializable {

    Long ID;
    Long selectedFriendID;

    @FXML
    private Button reportsButton;

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

    private Service service;

    private Parent root;

    @FXML
    private ListView<VBox> list;

    @FXML
    private Label InfoLabel;

    public void setService(Service service, Long id, Long selectedFriendId) {
        this.service = service;
        this.ID=id;
        this.selectedFriendID=selectedFriendId;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources){
        service.setMessagesAsSeen(selectedFriendID,ID);
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
        List<Message> messages = service.seeConversation(ID, selectedFriendID);
        VBox vbox=new VBox(3);

        vbox.setPadding(new Insets(0, 10, 15, 10));

        HBox toUserHBox=new HBox(40);

        Label InfoLabel=new Label();
        InfoLabel.getStyleClass().add("infoLabel");
        User user=service.findOneUser(selectedFriendID);
        InfoLabel.setText(user.getFirstName()+" "+user.getLastName());
        InfoLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 16));

        ImageView userAvatar=new ImageView();
        Image image= service.loadAvatar(selectedFriendID);
        userAvatar.setImage(image);
        userAvatar.setPreserveRatio(true);
        userAvatar.setFitWidth(50);
        userAvatar.setFitHeight(50);

        toUserHBox.getChildren().add(userAvatar);
        toUserHBox.getChildren().add(InfoLabel);

        vbox.getChildren().add(toUserHBox);
        //vbox.setMaxHeight(USE_PREF_SIZE);
        //vbox.setPrefHeight(USE_COMPUTED_SIZE);
        for (Message message : messages) {
            if(message.getFrom().getId().equals(ID)) {
                HBox hbox=new HBox();
                Label messageLabel=new Label();
                hbox.setAlignment(Pos.BASELINE_RIGHT);
                //messageLabel.setTextOverrun(OverrunStyle.LEADING_WORD_ELLIPSIS);
                messageLabel.setText(adjustText(message.getMessageText()));
                //messageLabel.setText(message.getMessageText());
                messageLabel.getStyleClass().add("messageReceivedLabel");
                //messageLabel.setMaxHeight(USE_PREF_SIZE);
                //messageLabel.setWrapText(true);
                //messageLabel.setPrefHeight(USE_COMPUTED_SIZE);
                // hbox.getStyleClass().add("friendRequestsHBox");
               // messageLabel.setAlignment(Pos.BOTTOM_RIGHT);
                VBox vboxMessage=new VBox();
                //Label messageLabel2=new Label();
                //messageLabel2.setVisible(false);
                //messageLabel2.setPrefWidth(230);

               Label nameDateLabel=new Label();
               nameDateLabel.setFont(new Font("Arial",7));
               nameDateLabel.getStyleClass().add("invisibleLabel");
               nameDateLabel.setText(message.getDate().toString().substring(0,10)+"    "+message.getDate().toString().substring(11,16));
                //vboxMessage.getChildren().add(nameDateLabel);
                //vboxMessage.getChildren().add(messageLabel);
                vboxMessage.getChildren().add(nameDateLabel);
                vboxMessage.getChildren().add(messageLabel);
                vboxMessage.setAlignment(Pos.BASELINE_RIGHT);
               // vboxMessage.setMaxHeight(USE_PREF_SIZE);
               // vboxMessage.setPrefHeight(USE_COMPUTED_SIZE);
                vbox.getChildren().add(vboxMessage);
                //messageListRight.add(message.getMessageText());
                //messageList.add("");
            }
            else{
                //messageList.add(message.getMessageText());
                //messageListRight.add("");
                Label messageLabel=new Label();
                messageLabel.setText(adjustText(message.getMessageText()));
                //messageLabel.setText(message.getMessageText());
                //messageLabel.setTextOverrun(OverrunStyle.LEADING_WORD_ELLIPSIS);
                //messageLabel.setPrefHeight(USE_COMPUTED_SIZE);
               // messageLabel.setWrapText(true);
                Label nameDateLabel=new Label();
                nameDateLabel.setFont(new Font("Arial",7));
                nameDateLabel.getStyleClass().add("invisibleLabel");
                nameDateLabel.setText(message.getDate().toString().substring(0,10)+"    "+message.getDate().toString().substring(11,16));

                VBox vboxMessage=new VBox();
                vboxMessage.getChildren().add(nameDateLabel);
                vboxMessage.getChildren().add(messageLabel);
               // vboxMessage.setMaxHeight(USE_PREF_SIZE);
               // vboxMessage.setPrefHeight(USE_COMPUTED_SIZE);
                vbox.getChildren().add(vboxMessage);
            }

        }

      //  vbox.setMaxHeight(USE_COMPUTED_SIZE);
      //  vbox.setMinWidth(USE_COMPUTED_SIZE);
        MessageList.setContent(vbox);
        MessageList.setVvalue(1D);
       // MessageList.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
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
        service.writeMessage(ID,selectedFriendID.toString(),messageText);
        reloadConversation();
        MessageTextField.setText("");
    }

    @FXML
    void reportsButtonClicked(MouseEvent event) throws IOException {
        ReportsController  reportsController = new ReportsController();
        FXMLLoader loader= new FXMLLoader(getClass().getResource("reports-view.fxml"));
        reportsController.setService(service,ID);
        loader.setController(reportsController);
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

    @FXML
    void refreshButtonClicked(MouseEvent event) {

        reloadConversation();
    }


}
