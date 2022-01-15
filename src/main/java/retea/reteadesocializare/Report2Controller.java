package retea.reteadesocializare;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import retea.reteadesocializare.domain.Friendship;
import retea.reteadesocializare.domain.Message;
import retea.reteadesocializare.domain.Tuple;
import retea.reteadesocializare.domain.User;
import retea.reteadesocializare.domain.observer.ListObservable;
import retea.reteadesocializare.domain.validators.ValidationException;
import retea.reteadesocializare.service.Service;
import retea.reteadesocializare.service.ServiceException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class Report2Controller implements Initializable {

    Long ID;


    @FXML
    private Button reportsButton;

    @FXML
    private ComboBox<User> comboBox;

    @FXML
    private ListView<User> FriendsList;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private Button generatePDFButton;

    @FXML
    private BorderPane BorderPane;

    @FXML
    private Label ErrorMessageLoginIn;

    @FXML
    private Label welcomeText;

    @FXML
    private Button LogInButton;

    @FXML
    private Label errorDateLabel;

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
    private Button report1Button;

    @FXML
    private Button report2Button;

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
        errorDateLabel.setVisible(false);

        startDatePicker.setEditable(false);

        endDatePicker.setEditable(false);

        ObservableList<User> items = FXCollections.observableArrayList (
                service.getUserFriends(ID));
        comboBox.setItems(items);


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


    @FXML
    void generatePDFButtonClicked(MouseEvent event) throws IOException {
        errorDateLabel.setVisible(false);
        Date getStartDate = java.sql.Date.valueOf(startDatePicker.getValue());
        Date getEndDate = java.sql.Date.valueOf(endDatePicker.getValue());
        if( getEndDate.before(getStartDate)) {
            errorDateLabel.setVisible(true);
            return;
        }
        User user = comboBox.getSelectionModel().getSelectedItem();


        Reports2PreviewController previewController = new Reports2PreviewController();
        FXMLLoader loader= new FXMLLoader(getClass().getResource("reports2Preview-view.fxml"));
        previewController.setService(service,ID,getEndDate,getStartDate,user.getId());
        loader.setController(previewController);
        root=loader.load();

        Stage stage = new Stage();
        Scene scene=new Scene(root);
        stage.setTitle("CyberBear");
        stage.setScene(scene);
        Image image = new Image(getClass().getResource("images/logoIcon.PNG").toExternalForm());
        stage.getIcons().add(image);
        stage.show();

    }

}
