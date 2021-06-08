package game;
import java.io.*;
import java.net.*;
import java.util.*;
import  java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ChatServer {
    private int port;
    private Set<String> userNames = new HashSet<>();
    private Set<UserThread> userThreads = new HashSet<>();
    private int numberOfPlayer =10;
    private  int currentNumberOfPlayers=0;
    private int whoSentStarts=0;
    private int readyToVote =0;
    private  List<Roll> rolls=new ArrayList<>();
    private List<UserThread> lastNightDead =new ArrayList<>();
    private HashMap<String,Integer> votesMap;

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
                currentNumberOfPlayers++;
                ExecutorService pool = Executors.newCachedThreadPool();
                for (UserThread user :userThreads){
                    user.setTask(Task.START);
                    pool.execute(user);
                }
//                if (getWhoSentStarts()==getNumberOfPlayer()){
                pool.shutdown();
                try {
                    pool.awaitTermination(1, TimeUnit.DAYS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                for (UserThread user :userThreads){
                    user.setTask(Task.FIRST_NIGHT);
                    pool.execute(user);
                }
                pool.shutdown();
                try {
                    pool.awaitTermination(1, TimeUnit.DAYS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                while (!endOfGame()){
                    for (UserThread user :userThreads){
                        user.setTask(Task.DAY);
                        pool.execute(user);
                    }
                    pool.shutdown();
                    try {
                        pool.awaitTermination(5, TimeUnit.MINUTES);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    votesMap =newListForVoting();




                }


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
    void broadcastToMafias(String message, UserThread excludeUser) {
        for (UserThread aUser : userThreads) {
            if (aUser != excludeUser && aUser.getRoll() instanceof Mafia) {
                aUser.sendMessage(message);
            }
        }
    }
    public String  nameOfGodFather(){
        String name="";
        for (UserThread userThread :userThreads){
            if (userThread.getRoll() instanceof Godfather){
                name = userThread.getName();
            }
        }
        return  name;
    }
    public String  nameOfMayor(){
        String name="";
        for (UserThread userThread :userThreads){
            if (userThread.getRoll() instanceof Mayor){
                name = userThread.getName();
            }
        }
        return  name;
    }
//    public ArrayList<String>  nameOfSimpleMafias(){
//        ArrayList<String> =
//        for (UserThread userThread :userThreads){
//            if (userThread.getRoll() instanceof SimpleMafia){
//                name = userThread.getName();
//            }
//        }
//        return  name;
//    }
    void sendToSpecial(String userName,String message) {
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
    public int numberOfMafia(){
        int mafia=0;
        for (UserThread userThread :userThreads){
            if (userThread.getRoll() instanceof  Mafia){
                mafia++;
            }
        }
        return mafia;
    }
    public boolean endOfGame(){
        if (numberOfMafia()>= (userThreads.size()-numberOfMafia())){
            return  true;
        }
        return false;

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


    }
    public HashMap newListForVoting(){
        votesMap =new HashMap<>();
        for (String username : userNames){
            votesMap.put(username,0);
        }
        return  votesMap;
    }

    public HashMap<String, Integer> getVotesMap() {
        return votesMap;
    }

    public void setWhoSentStarts(int whoSentStarts) {
        this.whoSentStarts = whoSentStarts;
    }

    public int getWhoSentStarts() {
        return whoSentStarts;
    }

    public int getNumberOfPlayer() {
        return numberOfPlayer;
    }

    public void setLastNightDead() {
        this.lastNightDead = new ArrayList<>();
    }
    public void addLastNightDead(UserThread userThread) {
        this.lastNightDead.add(userThread);
    }

    public List<UserThread> getLastNightDead() {
        return lastNightDead;
    }

    public int getReadyToVote() {
        return readyToVote;
    }

    public void setReadyToVote(int readyToVote) {
        this.readyToVote = readyToVote;
    }
    public  int numberOfAlives(){
        int alive=0;
        for (UserThread user:userThreads){
            if (user.getRoll().isAlive())
            {alive++;}
        }
        return alive;
    }
}
