package it.giuseppeM99.map.chat.exceptions;

public class UserNotFound extends GenericError {
    public UserNotFound(String name) {
        super("The user \""+name+"\" does not exists");
    }
}
