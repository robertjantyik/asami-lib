package hu.asami.services.notification;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.*;

@Service
public class NotificationService {

    private final SimpMessagingTemplate template;

    private int nextId = 0;

    private Set<String> listeners = new HashSet<>();

    private List<Notification> notifList = new ArrayList<>();

    public NotificationService(SimpMessagingTemplate template) {
        this.template = template;
    }

    public void add(String sessionId) {
        listeners.add(sessionId);
    }

    public void remove(String sessionId) {
        listeners.remove(sessionId);
    }

    private Notification addNotif(String message, Notification.type type) {
        Notification notif = new Notification();
        notif.setId(nextId);
        notif.setMessage(message);
        notif.setType(type);
        notifList.add(notif);
        nextId++;
        return notif;
    }

    public void success(String message) {
        for (String listener : listeners) {
            SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
            headerAccessor.setSessionId(listener);
            headerAccessor.setLeaveMutable(true);
            Notification n = addNotif(message, Notification.type.SUCCESS);
            template.convertAndSendToUser(
                    listener,
                    "/notification",
                    n,
                    headerAccessor.getMessageHeaders());
        }
    }

    public void info(String message) {
        for (String listener : listeners) {
            SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
            headerAccessor.setSessionId(listener);
            headerAccessor.setLeaveMutable(true);
            Notification n = addNotif(message, Notification.type.INFO);
            template.convertAndSendToUser(
                    listener,
                    "/notification",
                    n,
                    headerAccessor.getMessageHeaders());
        }
    }

    public void warning(String message) {
        for (String listener : listeners) {
            SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
            headerAccessor.setSessionId(listener);
            headerAccessor.setLeaveMutable(true);
            Notification n = addNotif(message, Notification.type.WARNING);
            template.convertAndSendToUser(
                    listener,
                    "/notification",
                    n,
                    headerAccessor.getMessageHeaders());
        }
    }

    public void error(String message) {
        for (String listener : listeners) {
            SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
            headerAccessor.setSessionId(listener);
            headerAccessor.setLeaveMutable(true);
            Notification n = addNotif(message, Notification.type.ERROR);
            template.convertAndSendToUser(
                    listener,
                    "/notification",
                    n,
                    headerAccessor.getMessageHeaders());
        }
    }

    public void remove(int id){
        Optional<Notification> n = notifList.stream().filter(n1 -> n1.getId() == id).findFirst();
        n.ifPresent(notification -> notifList.remove(notification));
    }

    public void notifList(){
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (Notification n : notifList) {
            sb.append(n.toString());
            sb.append(",");
        }
        sb.append("]");
        String notiflist = sb.toString().replace(",]", "]");
        for (String listener : listeners) {
            SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
            headerAccessor.setSessionId(listener);
            headerAccessor.setLeaveMutable(true);

            template.convertAndSendToUser(
                    listener,
                    "/notification",
                    notiflist,
                    headerAccessor.getMessageHeaders());
        }
    }

    @EventListener
    public void sessionDisconnectionHandler(SessionDisconnectEvent event) {
        String sessionId = event.getSessionId();
        remove(sessionId);
    }
}