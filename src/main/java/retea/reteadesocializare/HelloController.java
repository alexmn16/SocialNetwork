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
import javafx.scene.input.MouseEvent;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import retea.reteadesocializare.domain.Friendship;
import retea.reteadesocializare.domain.Tuple;
import retea.reteadesocializare.domain.User;
import retea.reteadesocializare.domain.utils.Hashing;
import retea.reteadesocializare.domain.validators.FriendshipValidator;
import retea.reteadesocializare.domain.validators.MessageValidator;
import retea.reteadesocializare.domain.validators.UserValidator;
import retea.reteadesocializare.repository.Repository;
import retea.reteadesocializare.repository.db.*;
import retea.reteadesocializare.service.Service;
import retea.reteadesocializare.service.ServiceException;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HelloController implements Initializable{
    Service service;
    ObservableList<String> items = FXCollections.observableArrayList();


    public HelloController() {

        Repository<Long, User> userDbRepository = new UserDbRepository("jdbc:postgresql://localhost:5432/ReteaDeSocializare", "postgres", "142001", new UserValidator());
        Repository<Tuple<Long, Long>, Friendship> friendshipDbRepository = new FriendshipDbRepository("jdbc:postgresql://localhost:5432/ReteaDeSocializare", "postgres", "142001", new FriendshipValidator());
        MessageDbRepository messageDbRepository=new MessageDbRepository("jdbc:postgresql://localhost:5432/ReteaDeSocializare", "postgres", "142001", new MessageValidator(),userDbRepository);
        GroupsDbRepository groupsDbRepository=new GroupsDbRepository("jdbc:postgresql://localhost:5432/ReteaDeSocializare", "postgres", "142001", userDbRepository,messageDbRepository);
        EventsDbRepository eventsDbRepository = new EventsDbRepository("jdbc:postgresql://localhost:5432/ReteaDeSocializare", "postgres", "142001", userDbRepository);
        Service service1 = new Service(userDbRepository, friendshipDbRepository, messageDbRepository, groupsDbRepository, eventsDbRepository);
        this.service = service1;

    }

    Long ID;

    @FXML
    private ListView<String> FriendsList;

    @FXML
    private PasswordField InvisiblePasswordField;

    @FXML
    private Button RegisterButton;

    @FXML
    private TextField VisiblePasswordField;

    @FXML
    private BorderPane BorderPane;

    @FXML
    private Label ErrorMessageLoginIn;

    @FXML
    private Label welcomeText;

    @FXML
    private Button LogInButton;

    @FXML
    private TextField UsernameTextField;

    @FXML
    private TextField EditProfileTextField;

    @FXML
    private Button FriendRequestsButton;

    @FXML
    private Button FriendsListButton;

    @FXML
    private Button LogOutButton;

    @FXML
    private TextField SearchBar;

    @FXML
    private CheckBox ShowPasswordCheckBox;

    @FXML
    private GridPane GridPaneListFriends;

    private Parent root;

    @Override
    public void initialize(URL location, ResourceBundle resources){
        VisiblePasswordField.setManaged(false);
        VisiblePasswordField.setVisible(false);

        VisiblePasswordField.managedProperty().bind(ShowPasswordCheckBox.selectedProperty());
        VisiblePasswordField.visibleProperty().bind(ShowPasswordCheckBox.selectedProperty());

        InvisiblePasswordField.managedProperty().bind(ShowPasswordCheckBox.selectedProperty().not());
        InvisiblePasswordField.visibleProperty().bind(ShowPasswordCheckBox.selectedProperty().not());
        VisiblePasswordField.textProperty().bindBidirectional(InvisiblePasswordField.textProperty());

    }
    @FXML
    public void LogInButtonClicked(MouseEvent event) throws IOException {
        String username=UsernameTextField.getText();
        String password=InvisiblePasswordField.getText();

        Hashing hashing=new Hashing();
        password= hashing.hash(password);

        try {
            ID=service.findOneUser(username,password);

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

        }catch(ServiceException ex){
            ErrorMessageLoginIn.setText(ex.getMessage());
        }

    }

    @FXML
    void RegisterButtonClicked(MouseEvent event) throws IOException{
        RegisterController registerController = new RegisterController();
        FXMLLoader loader= new FXMLLoader(getClass().getResource("register-view.fxml"));
        registerController.setService(service);
        loader.setController(registerController);
        root=loader.load();

        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene=new Scene(root);
        stage.setTitle("CyberBear");
        stage.setScene(scene);

        stage.show();
    }


    public void setService(Service service) {

        this.service = service;
    }
}