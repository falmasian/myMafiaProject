package game;

import java.io.*;
import java.net.*;
import java.util.*;

public class UserThread extends Thread {
    private Socket socket;
    private ChatServer server;
    private PrintWriter writer;
    private Roll roll;
    private Task task;

    public UserThread(Socket socket, ChatServer server, Roll roll) {
        this.socket = socket;
        this.server = server;
        this.roll = roll;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public Roll getRoll() {
        return roll;
    }

    public void run() {
        try {
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            OutputStream output = socket.getOutputStream();
            writer = new PrintWriter(output, true);

            printUsers();

            String userName = reader.readLine();
            while (true) {
                if (server.getUserNames().contains(userName)) {
                    userName = reader.readLine();
                } else break;
            }
            server.addUserName(userName);
            String serverMessage = "";
            // serverMessage = "New user connected: " + userName;
            //server.broadcast(serverMessage, this);

            String clientMessage;

            //do{
                if (task == Task.START) {

                    startGame(reader, userName);
                } else if (task == Task.FIRST_NIGHT) {
                    introduceNight(reader, userName);
                } else if (task == Task.DAY) {
                    day(reader, userName);
                } else if (task == Task.VOTING) {
                    voting(reader, userName);
                } else if (task == Task.NIGHT) {
                    night(reader, userName);
                }


//
//                                clientMessage = reader.readLine();
//                serverMessage = "[" + userName + "]: " + clientMessage;
//                server.broadcast(serverMessage, this);

            } catch(IOException e){
                e.printStackTrace();
            }
            //while (!clientMessage.equals("exit")) ;

            server.removeUser(userName, this);
            socket.close();

            serverMessage = userName + " has quitted.";
            server.broadcast(serverMessage, this);

        // catch (IOException ex) {
         //   System.out.println("Error in UserThread: " + ex.getMessage());
        //    ex.printStackTrace();
        }
  //  }

    private void voting(BufferedReader reader, String userName) throws IOException {
        String clientMessage;
        String serverMessage = " \" VOTING \" \n Enter a player's name : ";
        server.sendToSpecial(this.getName(), serverMessage);
        clientMessage =reader.readLine();
        server.getVotesMap().put(clientMessage, server.getVotesMap().get(clientMessage) + 1);
    }

    private void day(BufferedReader reader, String userName) throws IOException {
        List<UserThread> deads = server.getLastNightDead();
        String clientMessage;
        String serverMessage = "players who dead last night:";
        if (deads != null) {
            for (UserThread user : deads) {
                serverMessage += user.getName() + " ";

                serverMessage = "[" + userName + "]: " + clientMessage;
                server.broadcast(serverMessage, this);
            }
        }
        serverMessage += "\nMORNING\n";
        server.sendToSpecial(this.getName(), serverMessage);
        long start_time = System.currentTimeMillis();
        long wait_time = 1000 * 60 * 5;
        long end_time = start_time + wait_time;
        server.setReadyToVote(0);

        while (System.currentTimeMillis() < end_time || server.getReadyToVote() == server.numberOfAlives()) {

            clientMessage = reader.readLine();
            if ("ready".equalsIgnoreCase(clientMessage)) {
                server.setReadyToVote(server.getReadyToVote() + 1);
            }
            serverMessage = "[" + userName + "]: " + clientMessage;
            server.broadcast(serverMessage, this);

        }
    }

    private void startGame(BufferedReader reader, String userName) {
        String serverMessage = "Time to start game!\n" + "Enter start if you are ready:\n";
        String clientMessage;
        server.sendToSpecial(userName, serverMessage);
        try {
            clientMessage = reader.readLine();
            if ("start".equalsIgnoreCase(clientMessage)) {
                server.setWhoSentStarts(server.getWhoSentStarts() + 1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void introduceNight(BufferedReader reader, String userName) {
        String serverMessage = "Introduction".toUpperCase(Locale.ROOT) + "NIGHT\n" + "Mafias introduce your rolls:\n";
        String clientMessage;
        server.sendToSpecial(userName, serverMessage);
        if (this.getRoll() instanceof Mafia) {
            try {
                clientMessage = reader.readLine();
                serverMessage = "[" + userName + "]: " + clientMessage;
                server.broadcastToMafias(serverMessage, this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        serverMessage = "doctor introduce yourself to the mayor:\n";
        server.sendToSpecial(userName, serverMessage);
        if (this.getRoll() instanceof Doctor) {
            try {
                clientMessage = reader.readLine();
                serverMessage = "[" + userName + "]: " + clientMessage;
                server.sendToSpecial(server.nameOfMayor(), serverMessage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Sends a list of online users to the newly connected user.
     */
    void printUsers() {
        if (server.hasUsers()) {
            writer.println("Connected users: " + server.getUserNames());
        } else {
            writer.println("No other users connected");
        }
    }

    /**
     * Sends a message to the client.
     */
    void sendMessage(String message) {
        writer.println(message);
    }
}
