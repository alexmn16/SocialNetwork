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
import retea.reteadesocializare.domain.Tuple;
import retea.reteadesocializare.domain.User;
import retea.reteadesocializare.domain.validators.FriendshipValidator;
import retea.reteadesocializare.domain.validators.MessageValidator;
import retea.reteadesocializare.domain.validators.UserValidator;
import retea.reteadesocializare.repository.Repository;
import retea.reteadesocializare.repository.db.FriendshipDbRepository;
import retea.reteadesocializare.repository.db.MessageDbRepository;
import retea.reteadesocializare.repository.db.UserDbRepository;
import retea.reteadesocializare.service.Service;
import retea.reteadesocializare.service.ServiceException;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HelloController {
    Service service;
    ObservableList<String> items = FXCollections.observableArrayList();

    public HelloController() {
        Repository<Long, User> userDbRepository = new UserDbRepository("jdbc:postgresql://localhost:5432/ReteaDeSocializare", "postgres", "142001", new UserValidator());
        Repository<Tuple<Long, Long>, Friendship> friendshipDbRepository = new FriendshipDbRepository("jdbc:postgresql://localhost:5432/ReteaDeSocializare", "postgres", "142001", new FriendshipValidator());
        MessageDbRepository messageDbRepository=new MessageDbRepository("jdbc:postgresql://localhost:5432/ReteaDeSocializare", "postgres", "142001", new MessageValidator(),userDbRepository);
        Service service1 = new Service(userDbRepository, friendshipDbRepository,messageDbRepository);
        this.service = service1;
    }

    Long ID;
    @FXML
    private ListView<String> FriendsList;


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
    private TextField SearchBar;

    @FXML
    private GridPane GridPaneListFriends;

    private Parent root;

    @FXML
    public void LogInButtonClicked(MouseEvent event) throws IOException {
        /*
        Stage stage = new Stage();
        stage.setTitle("My New Stage Title");
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 200, 200);
        stage.setScene(scene);
        ((Node)(event.getSource())).getScene().getWindow().hide();
        stage.show();
        */
        String id = null;
        id= LoginTextField.getText();
        try {
            ID = Long.parseLong(id);

            Iterable<User> l= service.getAllUsers();
            service.checkUserExistence(ID);

            //FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("mainMenu-view.fxml"));

            /*
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(HelloApplication.getResource("mainMenu-view.fxml"));

            FriendsListController editMessageViewController = fxmlLoader.getController();
            editMessageViewController.setService(service, ID);
*/

            FXMLLoader loader= new FXMLLoader(getClass().getResource("mainMenu-view.fxml"));
            root=loader.load();
            MainMenuController mainMenuController = loader.getController();
            mainMenuController.setService(service,ID);

            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            Scene scene=new Scene(root);
            stage.setTitle("CyberBear");
            stage.setScene(scene);
            //((Node)(event.getSource())).getScene().getWindow().hide();
            stage.show();

        } catch (NumberFormatException ex) {
            ErrorMessageLoginIn.setText("ID should be a number");
        }catch(ServiceException ex){
            ErrorMessageLoginIn.setText(ex.getMessage());
        }

    }


    public void setService(Service service) {
        this.service = service;
    }
}