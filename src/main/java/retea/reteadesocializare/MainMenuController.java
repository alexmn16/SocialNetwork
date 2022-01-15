package retea.reteadesocializare;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import retea.reteadesocializare.domain.Event;
import retea.reteadesocializare.domain.Friendship;
import retea.reteadesocializare.domain.Message;
import retea.reteadesocializare.domain.User;
import retea.reteadesocializare.domain.observer.ListObservable;
import retea.reteadesocializare.domain.observer.ListObserver;
import retea.reteadesocializare.domain.utils.DateDifference;
import retea.reteadesocializare.service.Service;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MainMenuController implements Initializable{

    @FXML
    private Button GroupsListButton;


    @FXML
    private ListView<User> FriendsList;


    @FXML
    private javafx.scene.layout.BorderPane BorderPane;

    @FXML
    private Label ErrorMessageLoginIn;

    @FXML
    private Label welcomeText;

    @FXML
    private Button LogInButton;

    @FXML
    private Button homeButton;

    @FXML
    private Button reportsButton;

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
    private Button EventsButton;

    @FXML
    private Button LogOutButton;

    @FXML
    private TextField SearchBar;

    @FXML
    private GridPane GridPaneListFriends;

    @FXML
    private ImageView AvatarImage;

    @FXML
    private ListView<Event> Events;

    @FXML
    private ListView<User> FriendRequests;

    @FXML
    private ListView<Message> LastMessages;

    @FXML
    private Label UserLoggedName;

    @FXML
    private ScrollPane MenuScrollPane;

    @FXML
    private ImageView NotificationIcon;

    @FXML
    private ListView<Event> NotificationList;

    @FXML
    private VBox Notifications;

    Service service;
    Long ID;

    private Parent root;

    public void setService(Service service,Long ID) {
        this.service = service;
        this.ID=ID;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources){
        User user=service.findUser(ID);
        UserLoggedName.setText("Welcome back, "+"\n\n"+ user.getFirstName()+" "+user.getLastName()+" !");
        UserLoggedName.setFont(Font.font("Arial Narrow", FontWeight.BOLD, 16));

        Notifications.setVisible(false);

        MenuScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        AvatarImage.setPreserveRatio(true);
        AvatarImage.setFitWidth(100);
        AvatarImage.setFitHeight(100);
        loadAvatar();
        reloadNotifications();
        reloadFriendRequests();
        reloadFriends();
        reloadUnseenMessages();
        reloadEvents();


        ExecutorService executorService= Executors.newCachedThreadPool();
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try{
                        Thread.sleep(10000);
                    } catch (InterruptedException e){
                        e.printStackTrace();
                    }
                    reloadNotifications();
                }
            }
        });
    }

    private String adjustText(String str){
        return str.substring(0,7);
    }

    public void reloadEvents(){
        ObservableList<Event> items = FXCollections.observableArrayList (
                service.findEvents(ID));
        Events.setItems(items);

        Events.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {

                if (event.getClickCount() == 2) {
                    Event selectedItem = Events.getSelectionModel().getSelectedItem();
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

        Events.setCellFactory(param -> new ListCell<Event>() {
            private ImageView imageView = new ImageView();
            @Override
            public void updateItem(Event event, boolean empty) {
                super.updateItem(event, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    VBox vbox=new VBox(3);
                    Image image= service.loadEventAvatar(event.getId());
                    imageView.setImage(image);
                    imageView.setPreserveRatio(true);
                    imageView.setFitHeight(50);
                    imageView.setFitHeight(50);
                    setText(null);
                    vbox.getChildren().add(imageView);
                    Label name=new Label();
                    name.getStyleClass().add("nameLabel");
                    name.setText(event.getName());
                    name.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
                    vbox.getChildren().add(name);
                    Label date=new Label();
                    date.setText(event.getDate());
                    vbox.getChildren().add(date);
                    vbox.setAlignment(Pos.CENTER);
                    MenuScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
                    setGraphic(vbox);
                }
            }
        });
    }

    public void reloadUnseenMessages(){
        ObservableList<Message> items = FXCollections.observableArrayList (
                service.findUnseenMessages(ID));
        LastMessages.setItems(items);

        LastMessages.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {

                if (event.getClickCount() == 2) {
                    ConversationController conversationController = new ConversationController();
                    FXMLLoader loader= new FXMLLoader(getClass().getResource("conversation-view.fxml"));
                    User selectedUser=LastMessages.getSelectionModel().getSelectedItem().getFrom();
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
    }

    public void reloadNotifications() {
        List<Event> events=new ArrayList<>();
        DateDifference dateDifference=new DateDifference();
        List<Event> userEvents=service.findEvents(ID);
        boolean showIcon=false;
        for(Event event: userEvents){
            Date dateAux;
            try {
                dateAux = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(event.getDate());
                Date currentDate= java.util.Calendar.getInstance().getTime();
                boolean check=service.checkUserEventNotifications(16L,16L);
                if(dateAux.after(currentDate)) {
                    Map<TimeUnit, Long> diffMap = dateDifference.computeDiff(currentDate,dateAux);
                    Long timeLeft = diffMap.get(TimeUnit.MINUTES) + diffMap.get(TimeUnit.HOURS) * 60 + diffMap.get(TimeUnit.DAYS) * 60 * 24;
                    //if(service.checkUserHasNotifications(ID,event.getId(),timeLeft))
                        //events.add(event);
                    if(service.checkUserEventNotifications(ID,event.getId())) {
                        if (timeLeft < 1) {
                            events.add(event);
                            if (service.getLastNotificationSeenFromUser(ID, event.getId()) != 1)
                                showIcon = true;
                        } else if (timeLeft < 2) {
                            events.add(event);
                            if (service.getLastNotificationSeenFromUser(ID, event.getId()) != 2)
                                showIcon = true;
                        } else if (timeLeft < 3) {
                            events.add(event);
                            if (service.getLastNotificationSeenFromUser(ID, event.getId()) != 3)
                                showIcon = true;
                        } else if (timeLeft < 4) {
                            events.add(event);
                            if (service.getLastNotificationSeenFromUser(ID, event.getId()) != 4)
                                showIcon = true;
                        } else if (timeLeft < 5) {
                            events.add(event);
                            if (service.getLastNotificationSeenFromUser(ID, event.getId()) != 5)
                                showIcon = true;
                        } else if (timeLeft < 10) {
                            events.add(event);
                            if (service.getLastNotificationSeenFromUser(ID, event.getId()) != 10)
                                showIcon = true;
                        }
                    }

                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

        if(!showIcon) {
            Image image = new Image(getClass().getResource("images/notificationIcon.png").toExternalForm());
            NotificationIcon.setImage(image);
        }
        else{
            Image image = new Image(getClass().getResource("images/notificationIconNew.png").toExternalForm());
            NotificationIcon.setImage(image);
        }

        ObservableList<Event> items = FXCollections.observableArrayList(events);
        NotificationList.setItems(items);
        NotificationList.setCellFactory(param -> new ListCell<Event>() {
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

                    try {
                        Date dateAux = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(event.getDate());
                        Date currentDate= java.util.Calendar.getInstance().getTime();
                        Map<TimeUnit, Long> diffMap = dateDifference.computeDiff(currentDate,dateAux);
                        Long timeLeft = diffMap.get(TimeUnit.MINUTES) + diffMap.get(TimeUnit.HOURS) * 60 + diffMap.get(TimeUnit.DAYS) * 60 * 24;
                        int lessThan=0;
                        if(timeLeft<1)
                            lessThan=1;
                        else if(timeLeft<2)
                            lessThan=2;
                        else if(timeLeft<3)
                            lessThan=3;
                        else if(timeLeft<4)
                            lessThan=4;
                        else if(timeLeft<5)
                            lessThan=5;
                        else if(timeLeft<10)
                            lessThan=10;
                        Label text= new Label();
                        setText("");
                        text.setTextAlignment(TextAlignment.CENTER);
                        text.setText("Less than "+ lessThan + " \n minutes left until\n" + event.getName());
                        HBox hBox=new HBox();
                        hBox.getChildren().add(imageView);
                        hBox.getChildren().add(text);
                        hBox.setAlignment(Pos.CENTER);
                        setGraphic(hBox);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }
            }
        });

        NotificationList.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {

                if (event.getClickCount() == 2) {
                    Event selectedItem = NotificationList.getSelectionModel().getSelectedItem();
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
    }

    public void reloadFriends(){
        ObservableList<User> items = FXCollections.observableArrayList (
                service.getUserFriends(ID));
        FriendsList.setItems(items);

        FriendsList.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {

                if (event.getClickCount() == 2) {
                    ConversationController conversationController = new ConversationController();
                    FXMLLoader loader= new FXMLLoader(getClass().getResource("conversation-view.fxml"));
                    User selectedUser= FriendsList.getSelectionModel().getSelectedItem();
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

        FriendsList.setCellFactory(param -> new ListCell<User>() {
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

    public void reloadFriendRequests(){


        FriendRequests.setOrientation(Orientation.HORIZONTAL);
        List <User> userList = service.pendingFriendships(ID);
        /*List<Friendship> friendshipList = service.pendingFriendships(ID);
        List<User> userList =  new ArrayList<>();
        for (Friendship friendship : friendshipList){
            userList.add(service.findOneUser(friendship.getSender()));
        }*/
        ObservableList<User> items = FXCollections.observableArrayList(userList);
        FriendRequests.setItems(items);

        FriendRequests.setCellFactory(param -> new ListCell<User>() {
            private ImageView imageView = new ImageView();

            @Override
            public void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    VBox vbox=new VBox(3);
                    Image image= service.loadAvatar(user.getId());
                    imageView.setImage(image);
                    imageView.setPreserveRatio(true);
                    imageView.setFitWidth(50);
                    imageView.setFitHeight(50);
                    setText(null);
                    vbox.getChildren().add(imageView);
                    Label name=new Label();
                    name.getStyleClass().add("nameLabel");
                    name.setText(user.getFirstName()+" "+user.getLastName());
                    name.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
                    vbox.getChildren().add(name);

                    HBox hbox=new HBox(3);
                    Button addFriend=new Button();
                    addFriend.setText(" Add  ");
                    addFriend.setFont(new Font(10));
                    Button refuseFriend=new Button();
                    refuseFriend.setText("Refuse");
                    refuseFriend.setFont(new Font(10));


                    addFriend.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            Long idFrom;
                            User selectedUser = FriendRequests.getSelectionModel().getSelectedItem();
                            idFrom = selectedUser.getId();
                            String status = "approved";
                            service.responseToFriendRequest( idFrom, ID, status);
                            reloadFriendRequests();
                            reloadFriends();
                        }
                    });

                    refuseFriend.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            Long idFrom;
                            User selectedUser = FriendRequests.getSelectionModel().getSelectedItem();
                            idFrom = selectedUser.getId();
                            String status = "rejected";
                            service.responseToFriendRequest( idFrom, ID, status);
                            reloadFriendRequests();
                            reloadFriends();
                        }
                    });

                    hbox.getChildren().add(addFriend);
                    hbox.getChildren().add(refuseFriend);
                    hbox.setAlignment(Pos.CENTER);

                    vbox.getChildren().add(hbox);
                    vbox.setAlignment(Pos.CENTER);

                    MenuScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
                    setGraphic(vbox);
                }
            }
        });

    }
    public void loadAvatar(){
        Image image=service.loadAvatar(ID);
        AvatarImage.setImage(image);
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
    void LogOutButtonClicked(MouseEvent event) throws IOException {
        Image image = new Image(getClass().getResource("images/logoIcon.PNG").toExternalForm());
        ((Node)(event.getSource())).getScene().getWindow().hide();
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setTitle("Log In");
        stage.setScene(scene);
        stage.getIcons().add(image);

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
    void ChangeAvatar(MouseEvent event) {
        FileChooser fileChooser = new FileChooser();
        try {
            File selectedFile = fileChooser.showOpenDialog((Stage) ((Node) event.getSource()).getScene().getWindow());
            if(selectedFile.exists()) {
                service.deleteAvatar(ID);
                service.saveAvatar(ID, selectedFile);
                loadAvatar();
            }
        }
        catch(NullPointerException e){
            return;
        }

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

    boolean notificationsVisible=false;
    @FXML
    void NotificationIconClicked(MouseEvent event) {
        if (notificationsVisible) {
            Notifications.setVisible(false);
            notificationsVisible = false;
        } else {
            Notifications.setVisible(true);
            notificationsVisible = true;
        }

        for (Event userEvent : NotificationList.getItems()) {
            Date dateAux = null;
            try {
                dateAux = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(userEvent.getDate());
                Date currentDate = java.util.Calendar.getInstance().getTime();
                DateDifference dateDifference=new DateDifference();
                Map<TimeUnit, Long> diffMap = dateDifference.computeDiff(currentDate, dateAux);
                Long timeLeft = diffMap.get(TimeUnit.MINUTES) + diffMap.get(TimeUnit.HOURS) * 60 + diffMap.get(TimeUnit.DAYS) * 60 * 24;
                if(timeLeft<1)
                    service.setLastNotificationSeenFromUser(ID, userEvent.getId(), 1);
                else if(timeLeft<2)
                    service.setLastNotificationSeenFromUser(ID, userEvent.getId(), 2);
                else if(timeLeft<3)
                    service.setLastNotificationSeenFromUser(ID, userEvent.getId(), 3);
                else if(timeLeft<4)
                    service.setLastNotificationSeenFromUser(ID, userEvent.getId(), 4);
                else if(timeLeft<5)
                    service.setLastNotificationSeenFromUser(ID, userEvent.getId(), 5);
                else if(timeLeft<10)
                    service.setLastNotificationSeenFromUser(ID, userEvent.getId(), 10);
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
            reloadNotifications();
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

}


