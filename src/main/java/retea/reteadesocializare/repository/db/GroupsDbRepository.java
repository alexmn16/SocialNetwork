package retea.reteadesocializare.repository.db;

import javafx.scene.image.Image;
import retea.reteadesocializare.domain.Group;
import retea.reteadesocializare.domain.Message;
import retea.reteadesocializare.domain.User;
import retea.reteadesocializare.domain.validators.Validator;
import retea.reteadesocializare.repository.Repository;

import java.io.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class GroupsDbRepository {
    private String url;
    private String username;
    private String password;
    private Repository<Long, User> userRepository;
    private Repository<Long, Message> messageRepository;

    public GroupsDbRepository(String url, String username, String password, Repository<Long, User> userRepository, Repository<Long, Message> messageRepository) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
    }

    public void saveGroup(String name,File image) {
        String sql = "insert into socialgroups ( group_name, group_image ) values (?,?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1,name);

            if(image==null)
                image=new File("src/main/resources/retea/reteadesocializare/images/userIcon.jpg");

            FileInputStream fis = new FileInputStream(image);

            ps.setBinaryStream(2, fis, image.length());

            ps.executeUpdate();
        } catch (SQLException | FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void saveUserToGroup(User user, Group group) {
        String sql = "insert into groups_users ( id_group, id_user ) values (?, ?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, group.getId());

            ps.setLong(2, user.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveMessageToGroup(Message message, Long group) {
        String sql = "insert into groups_messages ( id_group, id_message ) values (?, ?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, group);

            ps.setLong(2, message.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Message> findMessages(Long group){
        List<Message> messagesList=new ArrayList<>();
       // String sql = "select * from groups_messages where id_group = ?";
        //String sql="select * from messages inner join groups_messages on id_message=id where id_group= ?";
        String sql="select * from socialgroups inner join groups_messages on id=id_group\n" +
                "                            inner join messages on id_message=messages.id\n" +
                "                            inner join groups_users on socialgroups.id=groups_users.id_group\n" +
                "                            inner join users on groups_users.id_user = users.id and from_user=id_user\n" +
                "                            where socialgroups.id= ?";
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setLong(1, group);

            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()) {
                //Message message=messageRepository.findOne(resultSet.getLong("id_message"));
                //messagesList.add(message);
                DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
                LocalDateTime date = LocalDateTime.parse(resultSet.getString("date_sent"), formatter);

                /*
                User searchedUser=null;
                Long userId=resultSet.getLong("from_user");
                for(User user:users){
                    if(userId.equals(user.getId())) {
                        searchedUser = user;
                        break;
                    }
                }

                 */
                String firstName=resultSet.getString("first_name");
                String lastName=resultSet.getString("last_name");
                String username=resultSet.getString("username");
                String password=resultSet.getString("password");
                User searchedUser=new User(firstName,lastName,username,password);
                searchedUser.setId(resultSet.getLong("id_user"));

                List<User> to = new ArrayList<User>();
                Message message = new Message(searchedUser, to, resultSet.getString("message_text"), date);
                message.setId(resultSet.getLong("ID"));
                messagesList.add(message);
            }
            return messagesList;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<User> findUsers(Long group){
        List<User> users=new ArrayList<>();
        String sql = "select * from groups_users inner join users on id_user=id where id_group = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setLong(1, group);

            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()) {
                String firstName=resultSet.getString("first_name");
                String lastName=resultSet.getString("last_name");
                String username=resultSet.getString("username");
                String password=resultSet.getString("password");
                User user=new User(firstName,lastName,username,password);
                user.setId(resultSet.getLong("id_user"));
                users.add(user);
            }
            return users;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Group getLastCreatedGroup(){
        String sql="SELECT * FROM socialgroups ORDER BY ID DESC LIMIT 1";
        try (Connection connection = DriverManager.getConnection(url, username, password))
        {
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            if(!resultSet.next())
                return null;
            Group group=new Group(resultSet.getString("group_name"));
            group.setId(resultSet.getLong("id"));
            resultSet.close();
            return group;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public User getLastCreatedUser(){
        String sql="SELECT * FROM users ORDER BY id DESC LIMIT 1";
        try (Connection connection = DriverManager.getConnection(url, username, password))
        {
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            if(!resultSet.next())
                return null;
            User user = new User(resultSet.getString("first_name"), resultSet.getString("last_name"), resultSet.getString("username"),resultSet.getString("password"));
            user.setId(resultSet.getLong("id"));
            resultSet.close();
            return user;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Message getLastCreatedMessage(){
        String sql="SELECT * FROM messages ORDER BY ID DESC LIMIT 1";
        try (Connection connection = DriverManager.getConnection(url, username, password))
        {
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next())
                return null;

            DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
            LocalDateTime date = LocalDateTime.parse(resultSet.getString("date_sent"), formatter);

            User user = userRepository.findOne(resultSet.getLong("from_user"));

            List<User> to = new ArrayList<User>();

            Message message = new Message(user, to, resultSet.getString("message_text"), date);
            message.setId(resultSet.getLong("ID"));
            return message;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public Group findOne(Long id) {
        String sql="SELECT * from socialgroups WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password))
        {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if(!resultSet.next())
                return null;
            Group group=new Group(resultSet.getString("group_name"));
            group.setId(resultSet.getLong("id"));
            resultSet.close();
            return group;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public List<Group> findGroups(Long id){
        List<Group> groups=new ArrayList<>();
        String sql = "select * from groups_users where id_user = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setLong(1, id);

            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()) {
                Group group=findOne(resultSet.getLong("id_group"));
                groups.add(group);
            }
            return groups;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteAvatar(Long id) {
        String sql = "delete from user_avatars where id_user = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, id);

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveAvatar(Long id, File image){
        String sql = "insert into user_avatars ( id_user, avatar ) values (?, ?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, id);

            FileInputStream fis = new FileInputStream(image);

            ps.setBinaryStream(2, fis, image.length());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Image findAvatar(Long id){
        String sql="SELECT * from user_avatars WHERE id_user = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password))
        {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setLong(1, id);
            ResultSet rs = statement.executeQuery();
            if (rs != null) {
                while(rs.next()) {
                    byte[] imgBytes = rs.getBytes(2);
                    Image img = new Image(new ByteArrayInputStream(imgBytes));
                    return img;
                }

                return null;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Image loadGroupAvatar(Long id){
        String sql="SELECT * from socialgroups WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password))
        {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setLong(1, id);
            ResultSet rs = statement.executeQuery();
            if (rs != null) {
                while(rs.next()) {
                    byte[] imgBytes = rs.getBytes(3);
                    Image img = new Image(new ByteArrayInputStream(imgBytes));
                    return img;
                }

                return null;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setMessagesAsSeen(Long id1,Long id2){
        String sql = "update messages set seen=true where seen=false and from_user=? and to_users=?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1,id1);
            ps.setLong(2,id2);

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public List<Message> findUnseenMessages(Long id){
        User user1=userRepository.findOne(id);
        List<Message> messages=new ArrayList<>();
        String sql = "select * from messages inner join users on from_user=users.id where to_users = ? and seen=false";
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setLong(1, id);

            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()) {
                DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
                LocalDateTime date = LocalDateTime.parse(resultSet.getString("date_sent"), formatter);

                List<User> to=new ArrayList<>();
                to.add(user1);
                Long idFrom= resultSet.getLong("from_user");
                String firstName=resultSet.getString("first_name");
                String lastName=resultSet.getString("last_name");
                String username=resultSet.getString("username");
                String password=resultSet.getString("password");
                User user=new User(firstName,lastName,username,password);
                user.setId(idFrom);

                Message message = new Message(user, to, resultSet.getString("message_text"), date);
                message.setId(resultSet.getLong("ID"));
                messages.add(message);
            }
            return messages;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<User> findUserFriends(Long id){
        List<User> users=new ArrayList<>();
        String sql = "select * from users inner join friendships on id=id1 where id2=? and friendship_status='approved' union select * from users inner join friendships on id=id2 where id1=? and friendship_status='approved'";
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setLong(1, id);
            statement.setLong(2,id);

            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()) {

                Long idUser = resultSet.getLong("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String username= resultSet.getString("username");
                String password=resultSet.getString("password");
                User utilizator = new User(firstName, lastName,username,password);
                utilizator.setId(idUser);
                users.add(utilizator);
            }
            return users;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<User> findUserRequests(Long id){
        List<User> users=new ArrayList<>();
        String sql = "select * from users inner join friendships on id=id1 where id2=? and friendship_status='pending' and sender != ? union select * from users inner join friendships on id=id2 where id1=? and friendship_status='pending' and sender != ?";
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setLong(1, id);
            statement.setLong(2,id);
            statement.setLong(3, id);
            statement.setLong(4, id);

            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()) {

                Long idUser = resultSet.getLong("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String username= resultSet.getString("username");
                String password=resultSet.getString("password");
                User utilizator = new User(firstName, lastName,username,password);
                utilizator.setId(idUser);
                users.add(utilizator);
            }
            return users;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


}
