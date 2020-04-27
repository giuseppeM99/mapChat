package it.giuseppeM99.map.chat.server;

import it.giuseppeM99.map.chat.exceptions.UserAlreadyRegistered;
import it.giuseppeM99.map.chat.exceptions.UserNotFound;

import java.io.*;
import java.net.Socket;

public class ConnectionThread extends Thread {
    private final Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private final Users users;
    private boolean running;

    public ConnectionThread(Socket socket, Users users) {
        this.socket = socket;
        this.users = users;
    }

    private void process(String command) throws IOException {
        String[] s = command.split("\\s");
        switch (s[0]) {
            case "#name":
                try {
                    users.registerClient(s[1], this);
                    sendOK();
                } catch (UserAlreadyRegistered e) {
                    sendError(e);
                }
                break;
            case "#send":
                try {
                    users.getClientByName(s[1]).sendMessage(command.replaceFirst("^#send\\s\\w+\\s", ""));
                } catch (UserNotFound e) {
                    sendError(e);
                }
                break;
            case "exit":
                running = false;
                socket.close();
                break;
        }
    }

    @Override
    public void run() {
        try {
            running = true;
            System.out.println("Connessione accettata: " + socket);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            while (running) {
                String str = in.readLine();
                if (str != null) {
                    //processing
                    //out.println(str);
                    System.out.println(this + " <- "+str);
                    process(str);
                } else {
                    running = false;
                }
            }
        } catch (IOException ex) {
            System.err.println(ex);
        } finally {
            System.out.println("closing...");
            try {
                socket.close();
                running = false;
            } catch (IOException ex) {
                System.err.println(ex);
            }
        }
    }


    public void sendOK() {
        sendMessage("#ok");
    }

    public void sendError(Exception e) {
        sendMessage("#error " +e.getMessage());
    }

    public void sendMessage(String message) {
        System.out.println(this + " -> "+message);
        out.println(message);
    }

    public boolean isClosed() {
        return socket.isClosed();
    }
}
