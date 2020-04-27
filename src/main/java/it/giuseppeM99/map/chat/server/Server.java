package it.giuseppeM99.map.chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Server extends Thread {
    private static List<Thread> threads;
    private static ServerSocket s;
    private static Thread running;

    public static void main(String[] args) throws IOException {
        ServerSocket s = new ServerSocket(6066);
        threads = new LinkedList<>();
        System.out.println("Started: " + s);
        Users users = new Users();
        users.start();
        running = new Thread(new Server());
        running.start();
        try {
            while (true) {
                System.out.println("Try connect");
                Socket socket = s.accept();
                Thread t = new ConnectionThread(socket, users);
                t.start();
                threads.add(t);
            }
        } finally {
            s.close();
        }
    }

    public void run() {
        while (true) {
            for (Iterator<Thread> it = threads.iterator(); it.hasNext();) {
                Thread t = it.next();
                if (!t.isAlive()) {
                    System.out.println("Cleaning thread "+t);

                    threads.remove(t);
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
