package retea.reteadesocializare.domain;

import java.util.Objects;

public class User extends Entity<Long>{
    private String firstName;
    private String lastName;

    public User(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }
    /**
     *
     * @return first name of user
     */
    public String getFirstName() {

        return firstName;
    }

    public void setFirstName(String firstName) {

        this.firstName = firstName;
    }
    /**
     *
     * @return lastName of user
     */
    public String getLastName() {

        return lastName;
    }

    public void setLastName(String lastName) {

        this.lastName = lastName;
    }



    @Override
    public String toString() {
        return  getId() + " " +
                 firstName + " "
                 + lastName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User that = (User) o;
        return getFirstName().equals(that.getFirstName()) &&
                getLastName().equals(that.getLastName());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getFirstName(), getLastName());
    }
}
