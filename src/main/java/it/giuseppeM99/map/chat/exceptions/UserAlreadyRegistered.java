package it.giuseppeM99.map.chat.exceptions;

public class UserAlreadyRegistered extends Exception {
    public UserAlreadyRegistered(String user) {
        super("The user "+user+" is already registered");
    }
}
