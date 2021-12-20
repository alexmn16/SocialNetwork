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

import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;

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

        String password = "myPassword";
        String encryptedpassword = null;
        try
        {
            /* MessageDigest instance for MD5. */
            MessageDigest m = MessageDigest.getInstance("MD5");

            /* Add plain-text password bytes to digest using MD5 update() method. */
            m.update(password.getBytes());

            /* Convert the hash value into bytes */
            byte[] bytes = m.digest();

            /* The bytes array has bytes in decimal form. Converting it into hexadecimal format. */
            StringBuilder s = new StringBuilder();
            for(int i=0; i< bytes.length ;i++)
            {
                s.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }

            /* Complete hashed password in hexadecimal format */
            encryptedpassword = s.toString();
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }

        /* Display the unencrypted and encrypted passwords. */
        System.out.println("Plain-text password: " + password);
        System.out.println("Encrypted password using MD5: " + encryptedpassword);
    }

    //UI ui = new UI(service);
        //ui.run();
    }

