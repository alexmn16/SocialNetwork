package retea.reteadesocializare.domain.validators;

public interface Validator<T> {
    /**
     *
     * @param entity
     * @throws ValidationException if entity is not valid
     */
    void validate(T entity) throws ValidationException;
}
