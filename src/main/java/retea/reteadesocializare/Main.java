package retea.reteadesocializare;

import retea.reteadesocializare.domain.Friendship;
import retea.reteadesocializare.domain.Message;
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
import retea.reteadesocializare.repository.file.FriendshipFile;
import retea.reteadesocializare.repository.file.UserFile;
import retea.reteadesocializare.repository.memory.InMemoryRepository;
import retea.reteadesocializare.service.Service;
//import ui.UI;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class Main {

    public static void main(String[] args) {
        String userFileName = "data/users.csv";
        String friendshipFileName = "data/friendships.csv";
       // try{
            //Repository<Long, User> userFileRepository = new UserFile(userFileName, new UserValidator());
            //Repository<Tuple<Long,Long>, Friendship> friendshipFileRepository = new FriendshipFile(friendshipFileName, new FriendshipValidator());
            //Service service = new Service(userFileRepository, friendshipFileRepository);
            //UI ui = new UI(service);
            //ui.run();

        //}catch(FileNotFoundException e)
        //{
        //   e.printStackTrace();
        //}


        Repository<Long, User> userDbRepository = new UserDbRepository("jdbc:postgresql://localhost:5432/ReteaDeSocializare", "postgres", "xela160302", new UserValidator());
        Repository<Tuple<Long, Long>, Friendship> friendshipDbRepository = new FriendshipDbRepository("jdbc:postgresql://localhost:5432/ReteaDeSocializare", "postgres", "xela160302", new FriendshipValidator());
        MessageDbRepository messageDbRepository=new MessageDbRepository("jdbc:postgresql://localhost:5432/ReteaDeSocializare", "postgres", "xela160302", new MessageValidator(),userDbRepository);
        Service service = new Service(userDbRepository, friendshipDbRepository,messageDbRepository);


        //UI ui = new UI(service);
        //ui.run();
    }
}
