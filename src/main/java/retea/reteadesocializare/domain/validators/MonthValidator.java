package retea.reteadesocializare.domain.validators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MonthValidator{
    static public void validate(String month)throws ValidationException{
        List<String> months = Arrays.asList("01","02","03","04","05","06","07","08","09","10","11","12");
        if(!months.contains(month))
            throw new ValidationException("Invalid month\n");
    }
}
