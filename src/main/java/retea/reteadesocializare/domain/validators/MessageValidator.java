package retea.reteadesocializare.domain.validators;

import retea.reteadesocializare.domain.Message;

public class MessageValidator implements Validator<Message> {
    @Override
    public void validate(Message entity) throws ValidationException {
        String errorMessage = "";
        if(entity.getTo().size()==0)
            errorMessage += "The list of destinators can't be empty\n";
        if(entity.getMessageText().equals(""))
            errorMessage += "Invalid message\n";

    }
}
