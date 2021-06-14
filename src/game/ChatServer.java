package game;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * The type Chat server.
 */
public class ChatServer {
    private int port;
    private Set<String> userNames = new HashSet<>();
    private Set<UserThread> userThreads = new HashSet<>();
    private int numberOfPlayer = 10;
    private int currentNumberOfPlayers = 0;
    private int whoSentStarts = 0;
    private int readyToVote = 0;
    private List<Roll> rolls = new ArrayList<>();
    private List<UserThread> lastNightDead = new ArrayList<>();
    private List<UserThread> wholeDead = new ArrayList<>();
    private HashMap<String, Integer> votesMap;
    private UserThread drlecteSave;
    private UserThread doctorSave;
    private UserThread whoGodKilled;
    private UserThread whoProfKilled;
    private boolean cancelVoting = false;

    /**
     * Gets whole dead.
     *
     * @return the whole dead
     */
    public List<UserThread> getWholeDead() {
        return wholeDead;
    }

    /**
     * Add whole dead.
     *
     * @param userThread the user thread
     */
    public void addWholeDead(UserThread userThread) {
        wholeDead.add(userThread);
    }

    /**
     * Is cancel voting boolean.
     *
     * @return the boolean
     */
    public boolean isCancelVoting() {
        return cancelVoting;
    }

    /**
     * Sets cancel voting.
     *
     * @param cancelVoting the cancel voting
     */
    public void setCancelVoting(boolean cancelVoting) {
        this.cancelVoting = cancelVoting;
    }

    /**
     * Gets who prof killed.
     *
     * @return the who prof killed
     */
    public UserThread getWhoProfKilled() {
        return whoProfKilled;
    }

    /**
     * Sets who prof killed.
     *
     * @param whoProfKilled the who prof killed
     */
    public void setWhoProfKilled(UserThread whoProfKilled) {
        this.whoProfKilled = whoProfKilled;
    }

    private boolean announceDeletedRolls;

    /**
     * Gets who god killed.
     *
     * @return the who god killed
     */
    public UserThread getWhoGodKilled() {
        return whoGodKilled;
    }

    /**
     * Sets who god killed.
     *
     * @param whoGodKilled the who god killed
     */
    public void setWhoGodKilled(UserThread whoGodKilled) {
        this.whoGodKilled = whoGodKilled;
    }

    /**
     * Is announce deleted rolls boolean.
     *
     * @return the boolean
     */
    public boolean isAnnounceDeletedRolls() {
        return announceDeletedRolls;
    }

    /**
     * Sets announce deleted rolls.
     *
     * @param announceDeletedRolls the announce deleted rolls
     */
    public void setAnnounceDeletedRolls(boolean announceDeletedRolls) {
        this.announceDeletedRolls = announceDeletedRolls;
    }

    /**
     * Instantiates a new Chat server.
     *
     * @param port the port
     */
    public ChatServer(int port) {
        this.port = port;
    }

    /**
     * Execute.
     * handle game
     */
    public void execute() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {

            System.out.println("Chat Server is listening on port " + port);

            createRolls();
            while (currentNumberOfPlayers < numberOfPlayer) {
                Socket socket = serverSocket.accept();
                System.out.println("New user connected");
                UserThread newUser = new UserThread(socket, this, rolls.get(0));
                rolls.remove(0);
                userThreads.add(newUser);
                newUser.setTask(Task.REGISTER);
                newUser.start();
              ++currentNumberOfPlayers;
            }
//            ExecutorService pool = Executors.newCachedThreadPool();
//            for (UserThread user : userThreads) {
//                user.setTask(Task.REGISTER);
//                pool.execute(user);
//            }
////                if (getWhoSentStarts()==getNumberOfPlayer()){
//            pool.shutdown();
//            try {
//                pool.awaitTermination(1, TimeUnit.DAYS);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
                ExecutorService pool1 = Executors.newCachedThreadPool();
                for (UserThread user : userThreads) {
                    user.setTask(Task.START);
                    pool1.execute(user);
                }
//                if (getWhoSentStarts()==getNumberOfPlayer()){
                pool1.shutdown();
                try {
                    pool1.awaitTermination(1, TimeUnit.DAYS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (whoSentStarts == numberOfPlayer) {
                    ExecutorService pool2 = Executors.newCachedThreadPool();
                    for (UserThread user : userThreads) {
                        user.setTask(Task.FIRST_NIGHT);
                        pool2.execute(user);
                    }
                    pool2.shutdown();
                    try {
                        pool2.awaitTermination(1, TimeUnit.DAYS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                while (!endOfGame()) {
                    ExecutorService pool3 = Executors.newCachedThreadPool();
                    for (UserThread user : userThreads) {
                        user.setTask(Task.DAY);
                        pool3.execute(user);
                    }
                    pool3.shutdown();
                    try {
                        pool3.awaitTermination(5, TimeUnit.MINUTES);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    for (UserThread user : userThreads) {
                        if (user.getRoll().isBeQuietDuringTheDay() == true) {
                            user.getRoll().setBeQuietduringTheDay(false);
                        }
                    }
                    votesMap = newListForVoting();
                    ExecutorService pool4 = Executors.newCachedThreadPool();
                    for (UserThread user : userThreads) {
                        user.setTask(Task.VOTING);
                        pool4.execute(user);
                    }
                    pool4.shutdown();
                    try {
                        pool4.awaitTermination(60, TimeUnit.SECONDS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (cancelVoting == true) {
                        Map.Entry<String, Integer> maxEntry = null;

                        for (Map.Entry<String, Integer> entry : votesMap.entrySet()) {
                            if (maxEntry == null || entry.getValue() > (maxEntry.getValue())) {
                                maxEntry = entry;
                            }
                        }
                        UserThread user = findUserByName(maxEntry.getKey());
                        removeUser(maxEntry.getKey(), user);
                    }
                    for (UserThread userThread : userThreads) {
                        userThread.getRoll().setBeQuietduringTheDay(false);
                    }

                    ExecutorService pool5 = Executors.newCachedThreadPool();
                    for (UserThread user1 : userThreads) {
                        user1.setTask(Task.NIGHT);
                        pool5.execute(user1);
                    }
                    pool5.shutdown();
                    try {
                        pool5.awaitTermination(1, TimeUnit.DAYS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (!whoGodKilled.equals(doctorSave)) {
                        if (whoGodKilled.getRoll() instanceof DieHard) {
                            if (((DieHard) whoGodKilled.getRoll()).getNumberOfLivesLeft() > 0) {
                                ((DieHard) whoGodKilled.getRoll()).decreaseNumberOfivesLeft();
                            } else {
                                addLastNightDead(whoGodKilled);
                                whoGodKilled.getRoll().setAlive(false);
                            }
                        } else {
                            addLastNightDead(whoGodKilled);
                            whoGodKilled.getRoll().setAlive(false);
                        }

                    }
                    if (!whoProfKilled.equals(drlecteSave)) {
                        addLastNightDead(whoProfKilled);
                        whoProfKilled.getRoll().setAlive(false);
                    }
                    System.out.println(endOfGame());

                }

        } catch (IOException ex) {
            System.out.println("Error in the server: " + ex.getMessage());
            ex.printStackTrace();
        }
        announceEndOGame();
    }

    /**
     * Broadcast.
     *
     * @param message     the message
     * @param excludeUser the exclude user
     */
    void broadcast(String message, UserThread excludeUser) {
        for (UserThread aUser : userThreads) {
            if (aUser != excludeUser) {
                aUser.sendMessage(message);
            }
        }
    }

    /**
     * Broadcast to mafias.
     *
     * @param message     the message
     * @param excludeUser the exclude user
     */
    void broadcastToMafias(String message, UserThread excludeUser) {
        for (UserThread aUser : userThreads) {
            if (aUser != excludeUser && aUser.getRoll() instanceof Mafia) {
                aUser.sendMessage(message);
            }
        }
    }

    /**
     * Name of god father string.
     *
     * @return the string
     */
    public String nameOfGodFather() {
        String name = "";
        for (UserThread userThread : userThreads) {
            if (userThread.getRoll() instanceof Godfather) {
                name = userThread.getName();
            }
        }
        return name;
    }

    /**
     * Name of mayor string.
     *
     * @return the string
     */
    public String nameOfMayor() {
        String name = "";
        for (UserThread userThread : userThreads) {
            if (userThread.getRoll() instanceof Mayor) {
                name = userThread.getName();
            }
        }
        return name;
    }

    /**
     * Find user by name user thread.
     *
     * @param name the name
     * @return the user thread
     */
    public UserThread findUserByName(String name) {
        UserThread user = null;
        for (UserThread userThread : userThreads) {

            if (userThread.getUserName().equalsIgnoreCase(name)) {
                user = userThread;
            }
        }
        return user;
    }

    /**
     * Send to special.
     *
     * @param userName the user name
     * @param message  the message
     */
//    public ArrayList<String>  nameOfSimpleMafias(){
//        ArrayList<String> =
//        for (UserThread userThread :userThreads){
//            if (userThread.getRoll() instanceof SimpleMafia){
//                name = userThread.getName();
//            }
//        }
//        return  name;
//    }
    void sendToSpecial(String userName, String message) {
        for (UserThread aUser : userThreads) {
            if (userName.equalsIgnoreCase(aUser.getName())) {
                aUser.sendMessage(message);
            }
        }
    }

    /**
     * Add user name.
     *
     * @param userName the user name
     */
    void addUserName(String userName) {
//        if (userNames.contains(userName)) {
        // }
        userNames.add(userName);
    }

    /**
     * Remove user.
     *
     * @param userName the user name
     * @param aUser    the a user
     */
    void removeUser(String userName, UserThread aUser) {
        boolean removed = userNames.remove(userName);
        if (removed) {
            userThreads.remove(aUser);
            System.out.println("The user " + userName + " quitted");
        }
    }

    /**
     * Number of mafia int.
     *
     * @return the int
     */
    public int numberOfMafia() {
        int mafia = 0;
        for (UserThread userThread : userThreads) {
            if (userThread.getRoll() instanceof Mafia) {
                mafia++;
            }
        }
        return mafia;
    }

    /**
     * End of game boolean.
     *
     * @return the boolean
     */
    public boolean endOfGame() {
        if (numberOfMafia() >= (userThreads.size() - numberOfMafia())) {
            return true;
        }
        if (numberOfMafia() == 0) {
            return true;
        }
        return false;

    }

    /**
     * Gets user names.
     *
     * @return the user names
     */
    Set<String> getUserNames() {
        return this.userNames;
    }

    /**
     * Has users boolean.
     *
     * @return the boolean
     */
    boolean hasUsers() {
        return !this.userNames.isEmpty();
    }

    /**
     * Create rolls.
     */
    public void createRolls() {
        int remainPlayers = numberOfPlayer;
        rolls.add(new DrLecter("Dr.Lecter"));
        remainPlayers--;
        rolls.add(new Professional("Professional"));
        remainPlayers--;
        rolls.add(new Psychologist("Psychologist"));
        remainPlayers--;
        rolls.add(new Godfather("Godfather"));
        remainPlayers--;
        rolls.add(new Detective("Detective"));
        remainPlayers--;
        rolls.add(new DieHard("DieHard"));
        remainPlayers--;
        rolls.add(new Doctor("Doctor"));
        remainPlayers--;
        rolls.add(new Mayor("Mayor"));
        remainPlayers--;
        if (remainPlayers >= 3) {
            int num = remainPlayers / 3;
            for (int i = 0; i < num; i++) {
                rolls.add(new SimpleMafia("Simple Mafia"));
                remainPlayers--;
            }
            for (int i = remainPlayers; i >= 0; i--) {
                rolls.add(new SimpleCitizen("Simple Citizen"));
            }
        } else if (remainPlayers == 2) {
            rolls.add(new SimpleCitizen("Simple Citizen"));
            rolls.add(new SimpleMafia("Simple Mafia"));
        } else if (remainPlayers == 1) {
            rolls.add(new SimpleCitizen("Simple Citizen"));
        }
        Collections.shuffle(rolls);
    }

    /**
     * Sets number of player.
     *
     * @param numberOfPlayer the number of player
     */
    public void setNumberOfPlayer(int numberOfPlayer) {
        if (numberOfPlayer >= 7) {
            this.numberOfPlayer = numberOfPlayer;
        }
    }

    /**
     * Announce end o game.
     */
    public void announceEndOGame() {
        if (numberOfMafia() >= (userThreads.size() - numberOfMafia())) {
            broadcast("Mafia won the game.\n", null);
        }
        if (numberOfMafia() == 0) {
            broadcast("the City won the game.\n", null);

        }
    }

    /**
     * New list for voting hash map.
     *
     * @return the hash map
     */
    public HashMap newListForVoting() {
        votesMap = new HashMap<>();
        for (String username : userNames) {
            votesMap.put(username, 0);
        }
        return votesMap;
    }

    /**
     * Gets votes map.
     *
     * @return the votes map
     */
    public HashMap<String, Integer> getVotesMap() {
        return votesMap;
    }

    /**
     * Sets who sent starts.
     *
     * @param whoSentStarts the who sent starts
     */
    public void setWhoSentStarts(int whoSentStarts) {
        this.whoSentStarts = whoSentStarts;
    }

    /**
     * Gets who sent starts.
     *
     * @return the who sent starts
     */
    public int getWhoSentStarts() {
        return whoSentStarts;
    }

    /**
     * Gets number of player.
     *
     * @return the number of player
     */
    public int getNumberOfPlayer() {
        return numberOfPlayer;
    }

    /**
     * Sets last night dead.
     */
    public void setLastNightDead() {
        this.lastNightDead = new ArrayList<>();
    }

    /**
     * Add last night dead.
     *
     * @param userThread the user thread
     */
    public void addLastNightDead(UserThread userThread) {
        this.lastNightDead.add(userThread);
        addWholeDead(userThread);
    }

    /**
     * Gets last night dead.
     *
     * @return the last night dead
     */
    public List<UserThread> getLastNightDead() {
        return lastNightDead;
    }

    /**
     * Gets ready to vote.
     *
     * @return the ready to vote
     */
    public int getReadyToVote() {
        return readyToVote;
    }

    /**
     * Sets ready to vote.
     *
     * @param readyToVote the ready to vote
     */
    public void setReadyToVote(int readyToVote) {
        this.readyToVote = readyToVote;
    }

    /**
     * Number of alives int.
     *
     * @return the int
     */
    public int numberOfAlives() {
        int alive = 0;
        for (UserThread user : userThreads) {
            if (user.getRoll().isAlive()) {
                alive++;
            }
        }
        return alive;
    }

    /**
     * Gets drlecte save.
     *
     * @return the drlecte save
     */
    public UserThread getDrlecteSave() {
        return drlecteSave;
    }

    /**
     * Sets drlecte save.
     *
     * @param drlecteSave the drlecte save
     */
    public void setDrlecteSave(UserThread drlecteSave) {
        this.drlecteSave = null;
        this.drlecteSave = drlecteSave;
    }

    /**
     * Gets doctor save.
     *
     * @return the doctor save
     */
    public UserThread getDoctorSave() {
        this.drlecteSave = null;
        return doctorSave;
    }

    /**
     * Sets doctor save.
     *
     * @param doctorSave the doctor save
     */
    public void setDoctorSave(UserThread doctorSave) {
        doctorSave = doctorSave;
    }

    /**
     * Write to file.
     *
     * @param text the text
     */
    public void writeToFile(String text) {
        try (FileWriter fw = new FileWriter("dailyChatFile.txt", true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.println(text);
        } catch (IOException e) {
            //exception handling left as an exercise for the reader
        }
    }


    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        int port = 5005;
        ChatServer chatServer = new ChatServer(port);
        chatServer.execute();


    }
}
