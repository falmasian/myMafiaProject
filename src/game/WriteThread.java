package game;


import java.io.*;
import java.net.*;
import java.util.Scanner;

/**
 * This thread is responsible for reading user's input and send it
 * to the server.
 * It runs in an infinite loop until the user types 'bye' to quit.
 *
 * @author www.codejava.net
 */
public class WriteThread extends Thread {
    private PrintWriter writer;
    private Socket socket;
    private ChatClient client;

    /**
     * Instantiates a new Write thread.
     *
     * @param socket the socket
     * @param client the client
     */
    public WriteThread(Socket socket, ChatClient client) {
        this.socket = socket;
        this.client = client;

        try {
            OutputStream output = socket.getOutputStream();
            writer = new PrintWriter(output, true);
        } catch (IOException ex) {
            System.out.println("Error getting output stream: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void run() {

        Scanner scanner =new  Scanner(System.in);

//       String userName = console.readLine("\nEnter your name: ");
//       client.setUserName(userName);
//        writer.println(userName);
//
   String text;

        do {
          //  "[" + userName + "]: "
            text = scanner.nextLine();
            writer.println(text);

        } while (!text.equalsIgnoreCase("exit"));

        try {
            socket.close();
        } catch (IOException ex) {

            System.out.println("Error writing to server: " + ex.getMessage());
        }
    }
}