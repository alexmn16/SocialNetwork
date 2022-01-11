package retea.reteadesocializare.domain;

import java.util.Objects;

public class Event extends Entity<Long>{
    String name;
    String date;
    String location;
    String description;
    Long creator;

    public Event(String name, String date, String location, String description, Long creator) {
        this.name = name;
        this.date = date;
        this.location = location;
        this.description = description;
        this.creator = creator;
    }

    @Override
    public String toString() {
        return "Event{" +
                "name='" + name + '\'' +
                ", date='" + date + '\'' +
                ", location='" + location + '\'' +
                ", description='" + description + '\'' +
                ", creator=" + creator +
                '}';
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getCreator() {
        return creator;
    }

    public void setCreator(Long creator) {
        this.creator = creator;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return name.equals(event.name) && date.equals(event.date) && location.equals(event.location) && description.equals(event.description) && creator.equals(event.creator);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, date, location, description, creator);
    }

}
