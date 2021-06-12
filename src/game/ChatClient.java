package game;
import java.net.*;
import java.io.*;
import java.util.Scanner;

public class ChatClient {
    private String hostname;
    private int port;
    private String userName;

    public ChatClient(String hostname) {
        this.hostname = hostname;

    }

    public void execute() {
        try {
            System.out.println("Enter port of game:");
            Scanner scanner=new Scanner(System.in);
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

    void setUserName(String userName) {
        this.userName = userName;
    }

    String getUserName() {
        return this.userName;
    }


    public static void main(String[] args) {
//        if (args.length < 2) return;

//        String hostname = args[0];

        String hostname ="127.0.0.1";
        ChatClient client = new ChatClient(hostname);
        client.execute();
    }
}
