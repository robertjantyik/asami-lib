package hu.asami.services.notification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Notification {
    public enum type {
        SUCCESS,
        INFO,
        WARNING,
        ERROR
    }
    private int id;
    private String message;
    private type type;

    @Override
    public String toString(){
        return "{\"id\":" + this.id + ",\"message\":\"" + this.message + "\",\"type\":\"" + this.type + "\"}";
    }
}
