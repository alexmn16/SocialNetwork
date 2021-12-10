package retea.reteadesocializare.repository.db;

import retea.reteadesocializare.domain.Friendship;
import retea.reteadesocializare.domain.User;
import retea.reteadesocializare.domain.validators.ValidationException;
import retea.reteadesocializare.domain.validators.Validator;
import retea.reteadesocializare.repository.Repository;

import java.sql.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
public class UserDbRepository implements Repository<Long, User> {
    private String url;
    private String username;
    private String password;
    private Validator<User> validator;
    public UserDbRepository(String url, String username, String password, Validator<User> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }
    @Override
    public User findOne(Long id) {
        String sql="SELECT * from users WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password))
        {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if(!resultSet.next())
                return null;
            User user = new User(resultSet.getString("first_name"), resultSet.getString("last_name"));
            user.setId(resultSet.getLong("id"));
            return user;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    public Iterable<User> findAll() {
        Set<User> users = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from users");
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                User utilizator = new User(firstName, lastName);
                utilizator.setId(id);
                users.add(utilizator);
            }
            return users;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }
    @Override
    public User save(User entity) {
        validator.validate(entity);
        String sql = "insert into users (first_name, last_name ) values (?, ?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, entity.getFirstName());
            ps.setString(2, entity.getLastName());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    public User delete(Long id) {

        String sql="DELETE from users WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            User user = findOne(id);
            statement.setLong(1, id);
            statement.executeUpdate();


        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
    @Override
    public User update(User entity) throws ValidationException {
        validator.validate(entity);
        String sql="UPDATE users SET first_name='"+entity.getFirstName()+"', last_name='"+entity.getLastName()+"' WHERE id='"+entity.getId()+"'";
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
}