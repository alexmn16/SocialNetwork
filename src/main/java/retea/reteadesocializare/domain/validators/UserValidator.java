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
            errorMessage += "first name error\n";
        if (lastName.equals("") || lastName.isEmpty() || lastName.charAt(0) < 'A' || lastName.charAt(0) > 'Z')
            errorMessage += "last name error\n";
        for (int i = 1; i < firstName.length(); i++) {
            if (firstName.charAt(i) < 'a' || firstName.charAt(i) > 'z') {
                errorMessage += "first name error\n";
                break;
            }
        }
        for (int i = 1; i < lastName.length(); i++) {
            if (lastName.charAt(i) < 'a' || lastName.charAt(i) > 'z') {
                errorMessage += "last name error\n";
                break;
            }
        }

        if (errorMessage != "")
            throw new ValidationException(errorMessage);

    }

    public void validateFirstName(String firstName) throws ValidationException {
        if (firstName.equals("") || firstName.isEmpty() || firstName.charAt(0) < 'A' || firstName.charAt(0) > 'Z')
            throw new ValidationException("Doesn't start with a capital letter");

        for (int i = 1; i < firstName.length(); i++) {
            if (firstName.charAt(i) < 'a' || firstName.charAt(i) > 'z')
                throw new ValidationException("First name should contain only letters");
        }
    }

    public void validateLastName(String lastName) throws ValidationException{
        if (lastName.equals("") || lastName.isEmpty() || lastName.charAt(0) < 'A' || lastName.charAt(0) > 'Z')
            throw new ValidationException("Doesn't start with a capital letter");

        for (int i = 1; i < lastName.length(); i++) {
            if (lastName.charAt(i) < 'a' || lastName.charAt(i) > 'z')
                throw new ValidationException("Last name should contain only letters");

        }
    }

    public void validateUsername(String username) throws ValidationException{
        if(username.length()<4)
            throw new ValidationException("Username should have at least 4 characters");
    }

    public void validatePassword(String password) throws ValidationException{
        if(password.length()<6)
            throw new ValidationException("Password should have at least 6 characters");
    }
}