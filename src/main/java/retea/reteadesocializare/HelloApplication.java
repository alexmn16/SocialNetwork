package retea.reteadesocializare;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import retea.reteadesocializare.domain.Friendship;
import retea.reteadesocializare.domain.Tuple;
import retea.reteadesocializare.domain.User;
import retea.reteadesocializare.domain.validators.FriendshipValidator;
import retea.reteadesocializare.domain.validators.MessageValidator;
import retea.reteadesocializare.domain.validators.UserValidator;
import retea.reteadesocializare.repository.Repository;
import retea.reteadesocializare.repository.db.*;
import retea.reteadesocializare.service.Service;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setTitle("Log In");
        stage.getIcons().add(new Image("retea/reteadesocializare/images/logoIcon.PNG"));
        stage.setResizable(false);
        stage.setScene(scene);


        Repository<Long, User> userDbRepository = new UserDbRepository("jdbc:postgresql://localhost:5432/ReteaDeSocializare", "postgres", "142001", new UserValidator());
        Repository<Tuple<Long, Long>, Friendship> friendshipDbRepository = new FriendshipDbRepository("jdbc:postgresql://localhost:5432/ReteaDeSocializare", "postgres", "142001", new FriendshipValidator());
        MessageDbRepository messageDbRepository=new MessageDbRepository("jdbc:postgresql://localhost:5432/ReteaDeSocializare", "postgres", "142001", new MessageValidator(),userDbRepository);
        GroupsDbRepository groupsDbRepository=new GroupsDbRepository("jdbc:postgresql://localhost:5432/ReteaDeSocializare", "postgres", "142001", userDbRepository,messageDbRepository);
        EventsDbRepository eventsDbRepository = new EventsDbRepository("jdbc:postgresql://localhost:5432/ReteaDeSocializare", "postgres", "142001", userDbRepository);
        Service service = new Service(userDbRepository, friendshipDbRepository, messageDbRepository, groupsDbRepository, eventsDbRepository);

        HelloController taskController = fxmlLoader.getController();
        taskController.setService(service);

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}