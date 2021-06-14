package game;

import java.net.*;
import java.io.*;
import java.util.Scanner;

/**
 * The type Chat client.
 */
public class ChatClient {
    private String hostname;
    private int port;
//    private String userName;

    /**
     * Instantiates a new Chat client.
     *
     * @param hostname the hostname
     */
    public ChatClient(String hostname) {
        this.hostname = hostname;

    }

    /**
     * Execute.
     */
    public void execute() {
        try {
            System.out.println("Enter port of game:");
            Scanner scanner = new Scanner(System.in);
            int port = scanner.nextInt();
            Socket socket = new Socket(hostname, port);

            System.out.println("Connected to the chat server");

            new ReadThread(socket, this).start();
            new WriteThread(socket, this).start();

        } catch (UnknownHostException ex) {
            System.out.println("Server not found: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("I/O Error: " + ex.getMessage());
        }

    }

//    void setUserName(String userName) {
//        this.userName = userName;
//    }
//
//    String getUserName() {
//        return this.userName;
//    }


    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
//        if (args.length < 2) return;

//        String hostname = args[0];

        String hostname = "127.0.0.1";
        ChatClient client = new ChatClient(hostname);
        client.execute();
    }
}
