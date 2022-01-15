package retea.reteadesocializare;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import retea.reteadesocializare.domain.Friendship;
import retea.reteadesocializare.domain.User;
import retea.reteadesocializare.domain.observer.ListObservable;
import retea.reteadesocializare.service.Service;

public class WarningController extends ListObservable {
    Long ID;
    Long id_friend;
    Service service;
    @FXML
    private Button cancelButton;

    @FXML
    private Button deleteButton;


    public void setService(Long ID, Long id_friend, Service service){
        this.ID = ID;
        this.id_friend = id_friend;
        this.service = service;
    }

    @FXML
    void cancelButtonClicked(MouseEvent event) {
        // get a handle to the stage
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        // do what you have to do
        stage.close();
    }

    @FXML
    void deleteButtonClicked(MouseEvent event) {
        User selectedUser=service.findUser(id_friend);
        Long idUser= id_friend;

        Long ID1=ID;
        Long ID2=idUser;
        if (ID1 > ID2) {
            Long swap = ID1;
            ID1 = ID2;
            ID2 = swap;
        }
        Friendship friendship = new Friendship(ID1, ID2);
        service.deleteFriendship(friendship.getId());
        notifyObservers();
        //ObservableList<User> items = FXCollections.observableArrayList (
                //service.getUserFriends(ID));
       // FriendsList.setItems(items);

        // get a handle to the stage
        Stage stage = (Stage) deleteButton.getScene().getWindow();
        // do what you have to do
        stage.close();
    }

}
