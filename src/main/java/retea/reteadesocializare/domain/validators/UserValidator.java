package retea.reteadesocializare.domain.validators;

import retea.reteadesocializare.domain.User;

/**
 * @throws ValidationException if user is not valid
 */
public class UserValidator implements Validator<User> {
    @Override
    public void validate(User entity) throws ValidationException {
        String errorMessage = "";
        String firstName = entity.getFirstName().trim();
        String lastName = entity.getLastName().trim();

        if (firstName.equals("") || firstName.isEmpty() || firstName.charAt(0) < 'A' || firstName.charAt(0) > 'Z')
            errorMessage+="first name error\n";
        if (lastName.equals("") || lastName.isEmpty() || lastName.charAt(0) < 'A' || lastName.charAt(0) > 'Z')
            errorMessage+="last name error\n";
        for (int i=1; i<firstName.length(); i++)
        {
            if (firstName.charAt(i)<'a' || firstName.charAt(i)>'z') {
                errorMessage += "first name error\n";
                break;
            }
        }
        for (int i=1; i<lastName.length(); i++)
        {
            if (lastName.charAt(i)<'a' || lastName.charAt(i)>'z') {
                errorMessage += "last name error\n";
                break;
            }
        }

        if(errorMessage != "")
            throw new ValidationException(errorMessage);

    }
}
