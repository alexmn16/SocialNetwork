package retea.reteadesocializare;

import retea.reteadesocializare.domain.*;
import retea.reteadesocializare.domain.validators.FriendshipValidator;
import retea.reteadesocializare.domain.validators.MessageValidator;
import retea.reteadesocializare.domain.validators.UserValidator;
import retea.reteadesocializare.domain.validators.ValidationException;
import retea.reteadesocializare.repository.Repository;
import retea.reteadesocializare.repository.db.FriendshipDbRepository;
import retea.reteadesocializare.repository.db.GroupsDbRepository;
import retea.reteadesocializare.repository.db.MessageDbRepository;
import retea.reteadesocializare.repository.db.UserDbRepository;
import retea.reteadesocializare.repository.file.FriendshipFile;
import retea.reteadesocializare.repository.file.UserFile;
import retea.reteadesocializare.repository.memory.InMemoryRepository;
import retea.reteadesocializare.service.Service;
//import ui.UI;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;

public class Main {

    public static void main(String[] args) {
        /*
        Repository<Long, User> userDbRepository = new UserDbRepository("jdbc:postgresql://localhost:5432/ReteaDeSocializare", "postgres", "142001", new UserValidator());
        Repository<Tuple<Long, Long>, Friendship> friendshipDbRepository = new FriendshipDbRepository("jdbc:postgresql://localhost:5432/ReteaDeSocializare", "postgres", "142001", new FriendshipValidator());
        MessageDbRepository messageDbRepository=new MessageDbRepository("jdbc:postgresql://localhost:5432/ReteaDeSocializare", "postgres", "142001", new MessageValidator(),userDbRepository);
        GroupsDbRepository groupsDbRepository=new GroupsDbRepository("jdbc:postgresql://localhost:5432/ReteaDeSocializare", "postgres", "142001", userDbRepository,messageDbRepository);

        List<Entity> lista=new ArrayList<>();
        Event event=new Event("1","1","mures","da",1L);
        User user=new User("A","B","ab","parla");
        lista.add(event);
        lista.add(user);
        for(Entity entity: lista){
            if(entity instanceof User){
                User user1 = (User)(entity);
                System.out.println(user1);
                System.out.println(entity);
        }}
        //File file = new File("src/main/resources/retea/reteadesocializare/images/loginIcon.jpg");
        //groupsDbRepository.saveAvatar(8L,file);
        //groupsDbRepository.findAvatar(8L);
    */


    }


    //UI ui = new UI(service);
    //ui.run();
}



