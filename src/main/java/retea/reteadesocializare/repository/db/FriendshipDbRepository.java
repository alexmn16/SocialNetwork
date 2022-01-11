package retea.reteadesocializare.repository.db;

import retea.reteadesocializare.domain.Friendship;
import retea.reteadesocializare.domain.Message;
import retea.reteadesocializare.domain.Tuple;
import retea.reteadesocializare.domain.User;
import retea.reteadesocializare.domain.validators.ValidationException;
import retea.reteadesocializare.domain.validators.Validator;
import retea.reteadesocializare.repository.Repository;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class FriendshipDbRepository implements Repository<Tuple<Long, Long>, Friendship> {
    private String url;
    private String username;
    private String password;
    private Validator<Friendship> validator;
    public FriendshipDbRepository(String url, String username, String password, Validator<Friendship> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }
    @Override
    public Friendship findOne(Tuple<Long,Long> tuple) {
        String sql="SELECT * from friendships WHERE id1 = ? and id2 = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password))

        {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setLong(1, tuple.getLeft());
            statement.setLong(2,tuple.getRight());
            ResultSet resultSet = statement.executeQuery();
            if(!resultSet.next())
                return null;

            Friendship friendship = new Friendship(resultSet.getLong("id1"), resultSet.getLong("id2"), resultSet.getString("date"), resultSet.getString("friendship_status"), resultSet.getLong("sender"));
            return friendship;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    public Iterable<Friendship> findAll() {
        Set<Friendship> friendships = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from friendships");
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Long id1 = resultSet.getLong("id1");
                Long id2 = resultSet.getLong("id2");
                String date = resultSet.getString("date");
                String friendshipStatus = resultSet.getString("friendship_status");
                Long sender = resultSet.getLong("sender");
                Friendship friendship = new Friendship(id1,id2,date,friendshipStatus,sender);
                friendships.add(friendship);
            }
            return friendships;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friendships;
    }
    @Override
    public Friendship save(Friendship entity) {
        validator.validate(entity);
        String sql = "insert into friendships (id1, id2, date, friendship_status,sender ) values (?, ?, ?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, entity.getId().getLeft());
            ps.setLong(2, entity.getId().getRight());
            String dateAsString=entity.getDate();
            ps.setString(3,dateAsString);
            ps.setString(4, entity.getFriendshipStatus());
            ps.setLong(5, entity.getSender());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    public Friendship delete(Tuple<Long,Long> tuple) {
        String sql="DELETE from friendships WHERE id1 = ? and id2 = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password))
        {
            Friendship friendship = findOne(tuple);
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setLong(1, tuple.getLeft());
            statement.setLong(2, tuple.getRight());
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
    public Friendship update(Friendship entity){

        validator.validate(entity);
        String sql = "UPDATE friendships SET friendship_status='" + entity.getFriendshipStatus() + "', date='" + entity.getDate() +"' WHERE id1='" + entity.getId().getLeft() + "' and id2='" + entity.getId().getRight() + "' ";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql);)
        {
            statement.executeUpdate();
            return entity;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Iterable<Friendship> findAllUsersStartsWith(String text) {
        return null;
    }

    @Override
    public List<Friendship> findConversation(Long id1, Long id2){return null;}
}
