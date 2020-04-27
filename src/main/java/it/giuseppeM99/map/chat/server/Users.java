package it.giuseppeM99.map.chat.server;

import it.giuseppeM99.map.chat.exceptions.UserAlreadyRegistered;
import it.giuseppeM99.map.chat.exceptions.UserNotFound;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Users extends Thread {
    private Map<String, ConnectionThread> users;

    public Users() {
        users = new HashMap();
    }

    public ConnectionThread getClientByName(String name) throws UserNotFound {
        synchronized (this) {
            if (users.containsKey(name)) {
                return users.get(name);
            }
        }
        throw new UserNotFound(name);
    }

    public boolean isNameTaken(String name) {
        synchronized (this) {
            return users.containsKey(name);
        }
    }

    public void registerClient(String name, ConnectionThread socket) throws UserAlreadyRegistered {
        synchronized (this) {
            if (users.containsValue(socket)) {
                users.values().remove(socket);
            }
            if (users.containsKey(name)) {
                throw new UserAlreadyRegistered(name);
            }
            users.put(name, socket);
        }
    }

    public void unregisterClient(String name) throws UserNotFound {
        synchronized (this) {
            if (!users.containsKey(name)) {
                throw new UserNotFound(name);
            }
            users.remove(name);
        }
    }

    public void run() {
        while (true) {
            synchronized (this) {
                for (Iterator<String> keyIterator = users.keySet().iterator(); keyIterator.hasNext();) {
                    String key = keyIterator.next();
                    if (users.get(key).isClosed()) {
                        System.out.println("Cleaning " + key);
                        users.remove(key);
                    }
                }
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
