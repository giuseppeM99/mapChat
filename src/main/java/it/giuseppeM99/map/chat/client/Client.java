package it.giuseppeM99.map.chat.client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private final Socket socket;
    private final BufferedReader in;
    private final PrintWriter out;
    private boolean running;

    public Client(Socket s) throws IOException {
        socket = s;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
        running = true;
        Thread input = new Thread(new Input(), "input");
        Thread output = new Thread(new Output(), "input");
        input.start();
        output.start();
        /*
        try {
            output.join();
            input.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

         */
    }

    private class Input implements Runnable {

        @Override
        public void run() {
            Scanner scanner = new Scanner(System.in);
            while (running) {
                String line = scanner.nextLine();
                if (line == null) {
                    running = false;
                } else {
                    out.println(line);
                }
            }
        }
    }


    private class Output implements Runnable {

        @Override
        public void run() {
            try {
                while (running) {
                    String line = in.readLine();
                    if (line == null) {
                        running = false;
                    }
                    System.out.println(line);
                }
            } catch (IOException e) {
                running = false;
                e.printStackTrace();
            }
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args)  {
        try {
            Socket s = new Socket("127.0.0.1", 6066);
            Client c = new Client(s);
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public void process(String[] command) {
        //soon TM
    }

}
