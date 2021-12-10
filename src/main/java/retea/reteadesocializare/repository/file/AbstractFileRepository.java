package retea.reteadesocializare.repository.file;

import retea.reteadesocializare.domain.Entity;
import retea.reteadesocializare.domain.validators.Validator;
import retea.reteadesocializare.repository.memory.InMemoryRepository;

import java.io.*;

import java.util.Arrays;
import java.util.List;


///Aceasta clasa implementeaza sablonul de proiectare Template Method; puteti inlucui solutia propusa cu un Factori (vezi mai jos)
public abstract class AbstractFileRepository<ID, E extends Entity<ID>> extends InMemoryRepository<ID,E> {
    String fileName;
    public AbstractFileRepository(String fileName, Validator<E> validator) {
        super(validator);
        this.fileName = fileName;
        loadData();

    }

    private void loadData() {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String linie;
            while((linie = br.readLine()) != null){
                //System.out.println(linie);
                List<String> attributes = Arrays.asList(linie.split(";"));
                E entity = extractEntity(attributes);
                super.save(entity);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     *  extract entity  - template method design pattern
     *  creates an entity of type E having a specified list of @code attributes
     * @param attributes
     * @return an entity of type E
     */
    public abstract E extractEntity(List<String> attributes);


    protected abstract String createEntityAsString(E entity);

    @Override
    public E save(E entity){
        E ent = super.save(entity);
        if (ent == null)
            writeToFile(entity);
        return ent;
    }

    @Override
    public E delete(ID id) {
      if(id == null)
          throw new IllegalArgumentException("id must be not null");
      E entity = super.findOne(id);
      if(entity != null){

          super.delete(id);
          Iterable<E> entities = super.findAll();
          PrintWriter pw = null;
          try{
              pw = new PrintWriter(fileName);
          } catch (FileNotFoundException e){
              e.printStackTrace();
          }
          pw.close();
          for (E ent: entities){
              writeToFile(ent);
          }
          return entity;
      }
        return null;
    }

    protected void writeToFile(E entity){
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(fileName,true))){
            bw.write(createEntityAsString(entity));
            bw.newLine();
        }catch (IOException e)
        {
            e.printStackTrace();
        }

    }


}
