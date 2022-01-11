package retea.reteadesocializare.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Message extends Entity<Long>{
    User from;
    List<User> to;
    String messageText;
    LocalDateTime date;
    List<Message> reply;

    public Message(User from, List<User> to, String messageText) {
        this.from = from;
        this.to = to;
        this.messageText = messageText;
        this.date = LocalDateTime.now();
        this.reply=null;
    }

    public Message(User from, List<User> to, String messageText,LocalDateTime date) {
        this.from = from;
        this.to = to;
        this.messageText = messageText;
        this.date = date;
        this.reply=null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return Objects.equals(from, message.from) && Objects.equals(to, message.to) && Objects.equals(messageText, message.messageText) && Objects.equals(date, message.date) && Objects.equals(reply, message.reply);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, messageText, date, reply);
    }

    public User getFrom() {
        return from;
    }

    public void setFrom(User from) {
        this.from = from;
    }

    public List<User> getTo() {
        return to;
    }

    public void setTo(List<User> to) {
        this.to = to;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String message) {
        this.messageText = message;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    @Override
    public String toString() {
        if(messageText.length()>12)
            return date.toString().substring(0,10) + " " + date.toString().substring(11,16) + " "+from +
                ": " + messageText.substring(0,11)+" ..." ;
        else
            return date.toString().substring(0,10) + " " + date.toString().substring(11,16) + " "+from +
                    ": " + messageText;

    }


    public List<Message> getReply() {
        return reply;
    }

    public void setReply(List<Message> reply) {
        this.reply = reply;
    }

    public void addNewReplyMessage(Message newReplyMessage){
        if(this.reply==null)
            this.reply=new ArrayList<>();
        this.reply.add(newReplyMessage);
    }
}
