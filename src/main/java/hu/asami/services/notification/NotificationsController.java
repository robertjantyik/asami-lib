package hu.asami.services.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
public class NotificationsController {
    private final NotificationService notif;

    @Autowired
    public NotificationsController(NotificationService dispatcher) {
        this.notif = dispatcher;
    }

    @MessageMapping("/start")
    public void start(StompHeaderAccessor stompHeaderAccessor) {
        notif.add(stompHeaderAccessor.getSessionId());
    }

    @MessageMapping("/success")
    public void success(String message){
        notif.success(message);
    }

    @MessageMapping("/info")
    public void info(String message){
        notif.info(message);
    }

    @MessageMapping("/warning")
    public void warning(String message){
        notif.warning(message);
    }

    @MessageMapping("/error")
    public void error(String message){
        notif.error(message);
    }

    @MessageMapping("/list")
    public void notifList(){
        notif.notifList();
    }

    @MessageMapping("/remove")
    public void remove(int id){
        notif.remove(id);
    }
}