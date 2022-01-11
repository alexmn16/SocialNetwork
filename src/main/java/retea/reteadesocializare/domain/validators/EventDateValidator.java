package retea.reteadesocializare.domain.validators;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

public class EventDateValidator {
    public void validate(String date)throws ValidationException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(date.trim());
            Date dateAux = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(date);
            Date currentDate= java.util.Calendar.getInstance().getTime();
            if(dateAux.before(currentDate))
                throw new ValidationException("Invalid date\n");

        } catch (ParseException pe) {
            throw new ValidationException("Invalid date\n");
        }

    }
}
