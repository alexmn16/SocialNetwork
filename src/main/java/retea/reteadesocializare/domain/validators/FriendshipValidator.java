package retea.reteadesocializare.domain.validators;

import retea.reteadesocializare.domain.Friendship;

/**
 * @throws ValidationException if friendship is not valid
 */
public class FriendshipValidator implements Validator<Friendship> {
    @Override
    public void validate(Friendship entity) throws ValidationException {
        String errorMessage = "";
        if(entity.getId().getLeft().equals(entity.getId().getRight()))
            errorMessage += "Identical IDs";
        if(entity.getId().getLeft() == null || "".equals(entity.getId().getLeft()) || entity.getId().getLeft() < 0)
            errorMessage += "user1 id error\n";
        if(entity.getId().getRight() == null || "".equals(entity.getId().getRight()) || entity.getId().getRight() < 0)
            errorMessage += "user2 id error\n";
        if(errorMessage != "")
            throw new ValidationException(errorMessage);
    }
}
