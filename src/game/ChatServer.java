package game;
import java.io.*;
import java.net.*;
import java.util.*;
import  java.util.Set;
public class ChatServer {
    private int port;
    private Set<String> userNames = new HashSet<>();
    private Set<UserThread> userThreads = new HashSet<>();
    private int numberOfPlayer =10;
    private  int currentNumberOfPlayers=0;
    private  List<Roll> rolls=new ArrayList<>();

    public ChatServer(int port) {
        this.port = port;
    }
    public void execute() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {

            System.out.println("Chat Server is listening on port " + port);

            while (currentNumberOfPlayers<numberOfPlayer) {
                Socket socket = serverSocket.accept();
                System.out.println("New user connected");

                UserThread newUser = new UserThread(socket, this,rolls.get(0));
                rolls.remove(0);
                userThreads.add(newUser);
                newUser.start();
                currentNumberOfPlayers++;

            }

        } catch (IOException ex) {
            System.out.println("Error in the server: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    void broadcast(String message, UserThread excludeUser) {
        for (UserThread aUser : userThreads) {
            if (aUser != excludeUser) {
                aUser.sendMessage(message);
            }
        }
    }
    void sendToSpecial(String userName,String message, UserThread excludeUser) {
        for (UserThread aUser : userThreads) {
            if (userName.equalsIgnoreCase(aUser.getName())) {
                aUser.sendMessage(message);
            }
        }
    }
    void addUserName(String userName) {
//        if (userNames.contains(userName)) {
       // }
        userNames.add(userName);
    }
    void removeUser(String userName, UserThread aUser) {
        boolean removed = userNames.remove(userName);
        if (removed) {
            userThreads.remove(aUser);
            System.out.println("The user " + userName + " quitted");
        }
    }

    Set<String> getUserNames() {
        return this.userNames;
    }
    boolean hasUsers() {
        return !this.userNames.isEmpty();
    }

    public  void createRolls(){
        int remainPlayers =numberOfPlayer;
         rolls.add(new DrLecter());
         remainPlayers--;
         rolls.add(new Professional());
         remainPlayers--;
         rolls.add(new Psychologist());
         remainPlayers--;
        rolls.add(new Godfather());
        remainPlayers--;
        rolls.add(new Detective());
        remainPlayers--;
        rolls.add(new DieHard());
        remainPlayers--;
        rolls.add(new Doctor());
        rolls.add(new Mayor());
        remainPlayers--;
        int num=remainPlayers/3;
        for (int i=0;i<num;i++){
            rolls.add(new SimpleMafia());
            remainPlayers--;
        }
        for (int i=remainPlayers;i>=0;i--){
            rolls.add(new SimpleCitizen());
        }
        Collections.shuffle(rolls);
    }
    public void setNumberOfPlayer(int numberOfPlayer) {
        if (numberOfPlayer>=7){
        this.numberOfPlayer = numberOfPlayer;}
    }
    public static void main(String[] args) {
        int port =5000;
        ChatServer chatServer=new ChatServer(port);
        ServerGame serverGame=new ServerGame( chatServer);

    }

}
