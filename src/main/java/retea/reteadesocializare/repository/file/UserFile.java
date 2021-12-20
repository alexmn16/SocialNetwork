package retea.reteadesocializare.repository.file;

import retea.reteadesocializare.domain.User;
import retea.reteadesocializare.domain.validators.Validator;

import java.io.FileNotFoundException;
import java.util.List;

public class UserFile extends AbstractFileRepository<Long, User> {

    public UserFile(String fileName, Validator<User> validator) throws FileNotFoundException{
        super(fileName, validator);
    }

    @Override
    public User extractEntity(List<String> attributes) {
        User user = new User(attributes.get(1),attributes.get(2), attributes.get(3), attributes.get(4) );
        user.setId(Long.parseLong(attributes.get(0)));

        return user;
    }

    @Override
    protected String createEntityAsString(User entity) {
        return entity.getId()+";"+entity.getFirstName()+";"+entity.getLastName();
    }
}
