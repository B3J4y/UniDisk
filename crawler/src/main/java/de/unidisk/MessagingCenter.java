package de.unidisk;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.SessionScoped;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/***
 * Klasse dient der beanübergreifenden Kommunikation.
 */
@SessionScoped
public class MessagingCenter {

    private static MessagingCenter instance;

    private MessagingCenter () {
        subscriberList = new ArrayList<>();
    }

    public static MessagingCenter getInstance () {
        if (MessagingCenter.instance == null) {
            MessagingCenter.instance = new MessagingCenter ();
        }
        return MessagingCenter.instance;
    }

    private List<Subscriber<?>> subscriberList;


    public <T> void subscribe(Object subscriber,String message, Function<T,Void> callback){
        subscriberList.add(new Subscriber<T>(subscriber,message,callback));
    }

    public void unsubscribe(Object subscriber,String message){
        subscriberList.removeIf((subsc) -> subsc == subscriber && subsc.getMessage().equals(message));

    }

    public <T> void send(String message, T data){
        subscriberList.stream()
                .filter((subscriber) -> subscriber.getMessage().equals(message))
                .forEach((match) ->  ( (Function<T,Void >)match.getCallback()).apply(data) );
    }
}

class Subscriber<T>{
    private final Object subscriber;
    private final String message;
    private Function<T,Void> callback;

    public Subscriber(Object subscriber, String message,Function<T,Void> callback) {
        this.subscriber = subscriber;
        this.message = message;
        this.callback = callback;
    }

    public Object getSubscriber() {
        return subscriber;
    }

    public String getMessage() {
        return message;
    }

    public Function<T,Void> getCallback() {
        return callback;
    }
}
