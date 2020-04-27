package it.giuseppeM99.map.chat.client;

import it.giuseppeM99.map.chat.exceptions.GenericError;
import it.giuseppeM99.map.chat.exceptions.UserAlreadyRegistered;
import it.giuseppeM99.map.chat.exceptions.UserNotFound;
import it.giuseppeM99.map.chat.exceptions.UserNotRegistered;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                    } else {
                        processResponse(line);
                    }
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
        String ip = "127.0.0.1";
        int port = 6066;

        if (args.length == 1) {
            ip = args[0];
        }
        if (args.length == 2) {
            port = Integer.parseInt(args[1]);
        }
        try {
            Socket s = new Socket(ip, port);
            Client c = new Client(s);
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public void processError(String error) {
        Pattern pattern = Pattern.compile("([\\w\\.]+):?(?:\\s(.*))*");
        Matcher matcher = pattern.matcher(error);

        if (!matcher.find()) {
            System.err.println("Unrecognized error "+error);
            return;
        }


        try {
            Class exClass = Class.forName(matcher.group(1));

            if (UserNotFound.class.getName().equals(exClass.getName())) {
                System.out.println(matcher.group(2));
            } else if (UserNotRegistered.class.getName().equals(exClass.getName())) {
                System.out.println("You are not registered\nUse <#name username> to register yourself");
            } else if (UserAlreadyRegistered.class.getName().equals(exClass.getName())) {
                System.out.println(matcher.group(2));
            } else if (GenericError.class.getName().equals(exClass.getName())) {
                System.out.println(matcher.groupCount() == 1 ? "An error has occurred" : matcher.group(2));
            } else {
                System.out.println("Could not handle error " + exClass.getName());
            }

        } catch (ClassNotFoundException e) {
            System.err.println("Unknown error "+error);
        }
    }

    public void processMessage(String messageResponse) {
        String[] subs = messageResponse.split("\\s+", 2);
        System.out.println(subs[0]+ ": "+subs[1]);
    }

    public void processResponse(String command) {
        //soon TM
        String[] subs = command.split("\\s+", 2);
        if (subs[0].equals("#ok")) return;
        if (subs[0].equals("#msg")) {
            processMessage(subs[1]);
            return;
        }
        if (subs[0].equals("#error")) {
            processError(subs[1]);
            return;
        }
        System.out.println(command);
    }

}
