package retea.reteadesocializare.repository.memory;

import retea.reteadesocializare.domain.Entity;
import retea.reteadesocializare.domain.Message;
import retea.reteadesocializare.domain.User;
import retea.reteadesocializare.domain.validators.ValidationException;
import retea.reteadesocializare.domain.validators.Validator;
import retea.reteadesocializare.repository.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryRepository<ID, E extends Entity<ID>> implements Repository<ID,E> {

    private Validator<E> validator;
    Map<ID,E> entities;

    public InMemoryRepository(Validator<E> validator) {
        this.validator = validator;
        entities=new HashMap<ID,E>();
    }

    @Override
    public E findOne(ID id){
        if (id==null)
            throw new IllegalArgumentException("ID must be not null");
        return entities.get(id);
    }

    @Override
    public Iterable<E> findAll() {

        return entities.values();
    }

    @Override
    public E save(E entity) {
        if (entity==null)
            throw new IllegalArgumentException("Entity must be not null");
            validator.validate(entity);
            if(entities.get(entity.getId()) != null) {
                return entity;
            }
            else entities.put(entity.getId(),entity);
            return null;

    }

    @Override
    public E delete(ID id) {

        if(id == null)
            throw new IllegalArgumentException("ID must be not null");
        if(findOne(id) != null){
            E entity = entities.get(id);
            entities.remove(id);
            return entity;
        }
        return null;
    }

    @Override
    public E update(E entity) {

        if(entity == null)
            throw new IllegalArgumentException("Entity must be not null!");
        validator.validate(entity);

        entities.put(entity.getId(),entity);

        if(entities.get(entity.getId()) != null) {
            entities.put(entity.getId(),entity);
            return null;
        }
        return entity;

    }

    @Override
    public Iterable<E> findAllUsersStartsWith(String text) {
        return null;
    }

    @Override
    public List<E> findConversation(Long id1, Long id2){return null;}
}
