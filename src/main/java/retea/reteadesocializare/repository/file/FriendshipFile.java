package retea.reteadesocializare.repository.file;

import retea.reteadesocializare.domain.Friendship;
import retea.reteadesocializare.domain.Tuple;
import retea.reteadesocializare.domain.validators.Validator;

import java.util.List;

public class FriendshipFile extends AbstractFileRepository<Tuple<Long,Long>, Friendship> {
    @Override
    public Friendship extractEntity(List<String> attributes) {
        Friendship friendship = new Friendship(Long.parseLong(attributes.get(0)), Long.parseLong(attributes.get(1)));
        return friendship;
    }

    @Override
    protected String createEntityAsString(Friendship entity) {
        return entity.getId().getLeft() + ";" + entity.getId().getRight();
    }

    public FriendshipFile(String fileName, Validator<Friendship> validator) {

        super(fileName, validator);
    }

}
