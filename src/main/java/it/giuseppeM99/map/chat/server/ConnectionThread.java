package it.giuseppeM99.map.chat.server;

import it.giuseppeM99.map.chat.exceptions.GenericError;
import it.giuseppeM99.map.chat.exceptions.UserAlreadyRegistered;
import it.giuseppeM99.map.chat.exceptions.UserNotFound;
import it.giuseppeM99.map.chat.exceptions.UserNotRegistered;

import java.io.*;
import java.net.Socket;

public class ConnectionThread extends Thread {
    private final Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private final Users users;
    private boolean running;
    private String name;

    public ConnectionThread(Socket socket, Users users) {
        this.socket = socket;
        this.users = users;
    }

    private void process(String command) throws IOException {
        String[] s = command.split("\\s+", 3);
        switch (s[0]) {
            case "#name":
                if (s.length == 1 || s[1].equals("")) {
                    sendError(new GenericError("No name given"));
                    break;
                }
                try {
                    System.out.println(s[1]);
                    users.registerClient(s[1], this);
                    name = s[1];
                    sendOK();
                } catch (UserAlreadyRegistered e) {
                    sendError(e);
                }
                break;
            case "#send":
                if (name == null) {
                    sendError(new UserNotRegistered());
                    break;
                }
                try {
                    users.getClientByName(s[1]).sendPeerMessage(name, s[2]);
                    sendOK();
                } catch (UserNotFound e) {
                    sendError(e);
                }
                break;
            /*
            case "#file":
                if (name == null) {
                    sendError(new UserNotRegistered());
                    break;
                }
                try {
                    int lenght = Integer.parseInt(s[2]);
                    char[] buffer = new char[128];
                    users.getClientByName(s[1]).sendMessage("#file "+lenght+" ");
                    while (lenght > 0) {
                        in.read(buffer);
                        out.write(buffer);
                        lenght -= 128;
                    }
                    //out.flush();
                    sendOK();
                } catch (UserNotFound e) {
                    sendError(e);
                }

            */
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

    public void sendPeerMessage(String peer, String message) {
        sendMessage("#msg "+peer+" "+message);
    }

    public void sendOK() {
        sendMessage("#ok");
    }

    public void sendError(Exception e) {
        sendMessage("#error " +e);
    }

    public void sendMessage(String message) {
        System.out.println(this + " -> "+message);
        out.println(message);
    }

    public boolean isClosed() {
        return socket.isClosed();
    }
}
