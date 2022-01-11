package retea.reteadesocializare;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import retea.reteadesocializare.domain.Friendship;
import retea.reteadesocializare.domain.Tuple;
import retea.reteadesocializare.domain.User;
import retea.reteadesocializare.domain.validators.FriendshipValidator;
import retea.reteadesocializare.domain.validators.MessageValidator;
import retea.reteadesocializare.domain.validators.UserValidator;
import retea.reteadesocializare.domain.validators.ValidationException;
import retea.reteadesocializare.repository.Repository;
import retea.reteadesocializare.repository.db.FriendshipDbRepository;
import retea.reteadesocializare.repository.db.MessageDbRepository;
import retea.reteadesocializare.repository.db.UserDbRepository;
import retea.reteadesocializare.service.Service;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class RegisterController implements Initializable {

    @FXML
    private Label ErrorMessageLoginIn;

    @FXML
    private Button GoBackButton;

    @FXML
    private CheckBox ShowPasswordCheckBox;

    @FXML
    private Button SignUpButton;

    @FXML
    private TextField UsernameTextField;

    @FXML
    private TextField VisiblePasswordField;

    @FXML
    private Label FirstNameError;

    @FXML
    private Label UsernameError;

    @FXML
    private Label LastNameError;

    @FXML
    private Label PasswordError;

    @FXML
    private Label ConfirmPasswordError;

    @FXML
    private TextField VisibleConfirmPasswordField;

    @FXML
    private TextField InvisibleConfirmPasswordField;

    @FXML
    private TextField InvisiblePasswordField;

    @FXML
    private TextField FirstNameTextField;

    @FXML
    private TextField LastNameTextField;

    private Parent root;
    private Service service;

    public void setService(Service service) {
        this.service = service;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources){
        VisiblePasswordField.setManaged(false);
        VisiblePasswordField.setVisible(false);

        VisiblePasswordField.managedProperty().bind(ShowPasswordCheckBox.selectedProperty());
        VisiblePasswordField.visibleProperty().bind(ShowPasswordCheckBox.selectedProperty());

        InvisiblePasswordField.managedProperty().bind(ShowPasswordCheckBox.selectedProperty().not());
        InvisiblePasswordField.visibleProperty().bind(ShowPasswordCheckBox.selectedProperty().not());
        VisiblePasswordField.textProperty().bindBidirectional(InvisiblePasswordField.textProperty());

        VisibleConfirmPasswordField.setManaged(false);
        VisibleConfirmPasswordField.setVisible(false);

        VisibleConfirmPasswordField.managedProperty().bind(ShowPasswordCheckBox.selectedProperty());
        VisibleConfirmPasswordField.visibleProperty().bind(ShowPasswordCheckBox.selectedProperty());

        InvisibleConfirmPasswordField.managedProperty().bind(ShowPasswordCheckBox.selectedProperty().not());
        InvisibleConfirmPasswordField.visibleProperty().bind(ShowPasswordCheckBox.selectedProperty().not());
        VisibleConfirmPasswordField.textProperty().bindBidirectional(InvisibleConfirmPasswordField.textProperty());

    }
    @FXML
    void GoBackButtonClicked(MouseEvent event) throws IOException {
        HelloController loginController = new HelloController();
        FXMLLoader loader= new FXMLLoader(getClass().getResource("hello-view.fxml"));
        root=loader.load();

        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene=new Scene(root);
        stage.setTitle("CyberBear");
        stage.setScene(scene);

        stage.show();
    }

    @FXML
    void SignUpButtonClicked(MouseEvent event) throws IOException{
        String username=UsernameTextField.getText();
        String password=InvisiblePasswordField.getText();
        String confirmPassword=InvisibleConfirmPasswordField.getText();
        String firstName=FirstNameTextField.getText();
        String lastName=LastNameTextField.getText();

        UserValidator validator=new UserValidator();

        boolean validData=true;

        PasswordError.setText("");
        LastNameError.setText("");
        FirstNameError.setText("");
        UsernameError.setText("");
        PasswordError.setText("");

        if(!password.equals(confirmPassword)) {
            ConfirmPasswordError.setText("Incorrect password");
            validData=false;
        }

        try{
            validator.validateLastName(lastName);
        }catch(ValidationException exception){
            validData=false;
            LastNameError.setText(exception.getMessage());
        }

        try{
            validator.validateFirstName(firstName);
        }catch(ValidationException exception){
            validData=false;
            FirstNameError.setText(exception.getMessage());
        }

        try{
            validator.validateUsername(username);
        }catch(ValidationException exception){
            validData=false;
            UsernameError.setText(exception.getMessage());
        }

        try{
            validator.validatePassword(password);
        }catch(ValidationException exception){
            validData=false;
            PasswordError.setText(exception.getMessage());
        }

        if(validData==true) {

            service.addUser(new User(firstName, lastName, username, password));
            User user= service.getLastCreatedUser();
            service.saveAvatar(user.getId(), new File("src/main/resources/retea/reteadesocializare/images/userIcon.jpg"));
            HelloController loginController = new HelloController();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
            root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setTitle("CyberBear");
            stage.setScene(scene);

            stage.show();
        }
    }

}

