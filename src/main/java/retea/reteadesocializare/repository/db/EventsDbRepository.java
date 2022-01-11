package retea.reteadesocializare.repository.db;

import javafx.scene.image.Image;
import retea.reteadesocializare.domain.Event;
import retea.reteadesocializare.domain.Group;
import retea.reteadesocializare.domain.Message;
import retea.reteadesocializare.domain.User;
import retea.reteadesocializare.repository.Repository;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EventsDbRepository {
    private String url;
    private String username;
    private String password;
    private Repository<Long, User> userRepository;

    public EventsDbRepository(String url, String username, String password, Repository<Long, User> userRepository) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.userRepository = userRepository;
    }

    public void saveEvent(String event_name, String event_date, String event_location, String event_description, Long event_creator, File image){
        String sql = "insert into events ( event_name, event_date, event_location, event_description, event_creator, event_image ) values (?,?,?,?,?,?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1,event_name);
            ps.setString(2,event_date);
            ps.setString(3,event_location);
            ps.setString(4,event_description);
            ps.setLong(5,event_creator);

            if(image==null)
                image=new File("src/main/resources/retea/reteadesocializare/images/userIcon.jpg");

            FileInputStream fis = new FileInputStream(image);

            ps.setBinaryStream(6, fis, image.length());

            ps.executeUpdate();
        } catch (SQLException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void saveUserToEvent(Long user, Event event) {
        String sql = "insert into events_users ( event_id, user_id,  notifications, last_notification_seen) values (?, ?, true, ?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, event.getId());

            ps.setLong(2, user);

            ps.setLong(3,0);

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<User> findUsers(Long event){
        List<User> users=new ArrayList<>();
        String sql = "select * from events_users inner join users on user_id=id where event_id = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setLong(1, event);

            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()) {
                String firstName=resultSet.getString("first_name");
                String lastName=resultSet.getString("last_name");
                String username=resultSet.getString("username");
                String password=resultSet.getString("password");
                User user=new User(firstName,lastName,username,password);
                user.setId(resultSet.getLong("user_id"));
                users.add(user);
            }
            return users;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Event findOne(Long id) {
        String sql="SELECT * from events WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password))
        {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if(!resultSet.next())
                return null;
            Event event=new Event(resultSet.getString("event_name"), resultSet.getString("event_date")
                    , resultSet.getString("event_location"), resultSet.getString("event_description"), resultSet.getLong("event_creator"));
            event.setId(resultSet.getLong("id"));
            resultSet.close();
            return event;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public List<Event> findEvents(Long id){
        List<Event> events=new ArrayList<>();
        String sql = "select * from events_users where user_id = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setLong(1, id);

            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()) {
                Event event=findOne(resultSet.getLong("event_id"));
                events.add(event);
            }
            return events;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public Image loadEventAvatar(Long id){
        String sql="SELECT * from events WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password))
        {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setLong(1, id);
            ResultSet rs = statement.executeQuery();
            if (rs != null) {
                while(rs.next()) {
                    byte[] imgBytes = rs.getBytes(7);
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

    public Event getLastCreatedEvent(){
        String sql="SELECT * FROM events ORDER BY ID DESC LIMIT 1";
        try (Connection connection = DriverManager.getConnection(url, username, password))
        {
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            if(!resultSet.next())
                return null;
            Event event=new Event(resultSet.getString("event_name"), resultSet.getString("event_date")
                    , resultSet.getString("event_location"), resultSet.getString("event_description"), resultSet.getLong("event_creator"));
            event.setId(resultSet.getLong("id"));
            resultSet.close();
            return event;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Iterable<Event> findAllEventsStartsWith(String text) {
        Set<Event> events = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from events WHERE event_name like '%"+text+"%' OR event_location like '"+text+"%'");
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String name = resultSet.getString("event_name");
                String date = resultSet.getString("event_date");
                String location = resultSet.getString("event_location");
                String description = resultSet.getString("event_description");
                Long creator = resultSet.getLong("event_creator");
                Event event = new Event(name, date , location, description, creator);
                event.setId(id);
                events.add(event);
            }
            return events;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }

    public boolean checkUserEvent(Long idUser, Long idEvent){
        boolean check = false;
        String sql="SELECT * from events_users WHERE user_id = ? and event_id = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password))
        {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setLong(1, idUser);
            statement.setLong(2, idEvent);
            ResultSet rs = statement.executeQuery();
            if (rs != null) {
                while(rs.next()) {
                    check = true;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return check;
    }

    public void deleteUserFromEvent(Long idUser, Long idEvent){
        String sql = "delete from events_users where event_id = ? and user_id = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, idEvent);

            ps.setLong(2, idUser);

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean checkUserEventNotifications(Long idUser, Long idEvent){
        boolean check = false;
        String sql="SELECT * from events_users WHERE user_id = ? and event_id = ? and notifications = true";
        try (Connection connection = DriverManager.getConnection(url, username, password))
        {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setLong(1, idUser);
            statement.setLong(2, idEvent);
            ResultSet rs = statement.executeQuery();
            if (rs != null) {
                while(rs.next()) {
                    check = true;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return check;
    }

    public boolean checkUserHasNotifications(Long idUser,Long idEvent, Long minutesLeft){
        boolean check=false;
        String sql="SELECT * from notifications_events_users" +
                "inner join events_users on  events_users.user_id=notifications_events_users.user_id" +
                "and events_users.event_id=notifications_events_users.event_id" +
                "where notifications=true and notifications_events_users.user_id= ? and notifications_events_users.event_id = ? and time_left>=?";
        try (Connection connection = DriverManager.getConnection(url, username, password))
        {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setLong(1, idUser);
            statement.setLong(2,idEvent);
            statement.setLong(3,minutesLeft);

            ResultSet rs = statement.executeQuery();
            if (rs != null) {
                while(rs.next()) {
                    check = true;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return check;
    }


    public void setNotificationOn(Long idUser, Long idEvent){
        String sql="update events_users set notifications=true WHERE user_id = ? and event_id = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password))
        {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setLong(1, idUser);
            statement.setLong(2, idEvent);
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setNotificationOff(Long idUser, Long idEvent){
        String sql="update events_users set notifications=false WHERE user_id = ? and event_id = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password))
        {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setLong(1, idUser);
            statement.setLong(2, idEvent);
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getLastNotificationSeenFromUser(Long idUser, Long idEvent){
        String sql="SELECT * from events_users WHERE user_id = ? and event_id=?";
        try (Connection connection = DriverManager.getConnection(url, username, password))
        {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setLong(1, idUser);
            statement.setLong(2,idEvent);
            ResultSet rs = statement.executeQuery();
            if (rs != null) {
                while(rs.next()) {
                    return rs.getInt(4);
                }

                return -1;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void setLastNotificationSeenFromUser(Long idUser, Long idEvent, int notification){
        String sql="update events_users set last_notification_seen=? WHERE user_id = ? and event_id = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password))
        {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1,notification);
            statement.setLong(2, idUser);
            statement.setLong(3, idEvent);
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
