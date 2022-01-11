package retea.reteadesocializare.repository.db;

import retea.reteadesocializare.domain.Friendship;
import retea.reteadesocializare.domain.Message;
import retea.reteadesocializare.domain.Tuple;
import retea.reteadesocializare.domain.User;
import retea.reteadesocializare.domain.validators.Validator;
import retea.reteadesocializare.repository.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class MessageDbRepository implements Repository<Long, Message> {
    private String url;
    private String username;
    private String password;
    private Validator<Message> validator;
    private Repository<Long, User> userRepository;

    public MessageDbRepository(String url, String username, String password, Validator<Message> validator, Repository<Long, User> userRepository) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
        this.userRepository = userRepository;
    }

    @Override
    public Message findOne(Long id) {
        String sql = "SELECT * from messages WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next())
                return null;

            DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
            LocalDateTime date = LocalDateTime.parse(resultSet.getString("date_sent"), formatter);

            User user = userRepository.findOne(resultSet.getLong("from_user"));

            List<String> toAsString = Arrays.asList(resultSet.getString("to_users").split(";"));
            List<User> to = new ArrayList<User>();
            for (String userString : toAsString)
                to.add(userRepository.findOne(Long.parseLong(userString)));

            Message message = new Message(user, to, resultSet.getString("message_text"), date);
            message.setId(resultSet.getLong("ID"));


            return message;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Message> findConversation(Long id1,Long id2) {
        User user1=userRepository.findOne(id1);
        User user2=userRepository.findOne(id2);
        List<Message> messages=new ArrayList<>();
        String sql = "select * from messages where from_user= ? and to_users = ? or from_user = ? and to_users = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setLong(1, id1);
            statement.setLong(2, id2);
            statement.setLong(4, id1);
            statement.setLong(3, id2);

            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()) {
                DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
                LocalDateTime date = LocalDateTime.parse(resultSet.getString("date_sent"), formatter);

                User user;
                List<User> to=new ArrayList<>();

                Long idFrom= resultSet.getLong("from_user");
                if(idFrom.equals(user1.getId())) {
                    user = user1;
                    to.add(user2);
                }
                else{
                    user=user2;
                    to.add(user1);
                }

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
    @Override
    public Iterable<Message> findAll() {
        Set<Message> messages = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from messages");
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
                LocalDateTime date = LocalDateTime.parse(resultSet.getString("date_sent"), formatter);

                User user = userRepository.findOne(resultSet.getLong("from_user"));

                List<String> toAsString = Arrays.asList(resultSet.getString("to_users").split(";"));
                List<User> to = new ArrayList<User>();
                for (String userString : toAsString)
                    to.add(userRepository.findOne(Long.parseLong(userString)));

                Message message = new Message(user, to, resultSet.getString("message_text"), date);
                message.setId(resultSet.getLong("ID"));


                messages.add(message);
            }

            return messages;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }


    @Override
    public Message save(Message entity) {
        validator.validate(entity);
        String sql = "insert into messages ( from_user, to_users, message_text, date_sent, seen ) values (?, ?, ?, ?, false)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1,entity.getFrom().getId());

            if(entity.getTo().size()!=0)
                ps.setLong(2,entity.getTo().get(0).getId() );
            else
                ps.setLong(2,0);

            ps.setString(3, entity.getMessageText());

            DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
            String date = entity.getDate().format(formatter);
            ps.setString(4,date);

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Message delete(Long id) {
        String sql = "DELETE from friendships WHERE ID = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            Message message = findOne(id);
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setLong(1, id);;
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Message update(Message entity) {
        validator.validate(entity);

        String repliersAsString="";

        if(entity.getReply().size()!=0) {
            for (Message message : entity.getReply())
                repliersAsString = repliersAsString + String.valueOf(message.getId()) + ";";
            repliersAsString = repliersAsString.substring(0, repliersAsString.length() - 1);


            String sql = "UPDATE messages SET repliers='" + repliersAsString + "' WHERE id='" + entity.getId() + "'";
            try (Connection connection = DriverManager.getConnection(url, username, password);
                 PreparedStatement statement = connection.prepareStatement(sql);) {
                statement.executeUpdate();
                return entity;

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public Iterable<Message> findAllUsersStartsWith(String text) {
        return null;
    }
}
