package retea.reteadesocializare.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;


public class Friendship extends Entity<Tuple<Long,Long>> {

    String date;
    String friendshipStatus;
    Long sender;

    public Friendship(Long fr1, Long fr2) {
        this.setId(new Tuple<Long,Long>(fr1,fr2));
        this.date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm")).toString();
        this.friendshipStatus = "approved";
        this.sender = fr1;
    }

    public Friendship(Long fr1, Long fr2, String date){
        this.setId(new Tuple<Long,Long>(fr1,fr2));
        this.date = date;
        this.friendshipStatus = "approved";
        this.sender = fr1;
    }

    public Friendship(Long fr1, Long fr2, String date, String friendshipStatus, Long sender) {
        this.setId(new Tuple<Long,Long>(fr1,fr2));
        this.date = date;
        this.friendshipStatus = friendshipStatus;
        this.sender = sender;
    }
    public Friendship(String friendshipStatus, Long fr1, Long fr2, Long sender) {
        this.setId(new Tuple<Long,Long>(fr1,fr2));
        this.date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm")).toString();
        this.friendshipStatus = friendshipStatus;
        this.sender = sender;
    }


    /**
     *
     * @return the date when the friendship was created
     */
    public String getDate() {

        return date;
    }

    public Long getSender() {
        return sender;
    }

    public void setSender(Long sender) {
        this.sender = sender;
    }

    public String getFriendshipStatus() {
        return friendshipStatus;
    }

    public void setFriendshipStatus(String friendshipStatus) {
        this.friendshipStatus = friendshipStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Friendship that = (Friendship) o;
        return Objects.equals(date, that.date) && Objects.equals(friendshipStatus, that.friendshipStatus) && Objects.equals(sender, that.sender);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, friendshipStatus, sender);
    }

}


