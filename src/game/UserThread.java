package game;

import java.io.*;
import java.net.*;
import java.util.*;

public class UserThread extends Thread {
    private String userName;
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

    public String getUserName() {
        return userName;
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
            String clientMessage;
            String serverMessage = "";


            //do{
            if (task == Task.REGISTER) {
                printUsers();

                String userName = reader.readLine();
                while (true) {
                    if (server.getUserNames().contains(userName)) {
                        userName = reader.readLine();
                    } else break;
                }
                server.addUserName(userName);
                serverMessage = "New user connected: " + userName;
                server.broadcast(serverMessage, this);
                serverMessage="your roll is :" + roll.getRollName();
                server.sendToSpecial(userName,serverMessage);
            } else if (task == Task.START) {

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

        } catch (IOException e) {
            e.printStackTrace();
        }
        //while (!clientMessage.equals("exit")) ;

//        server.removeUser(userName, this);
//        socket.close();
//
//        serverMessage = userName + " has quitted.";
//        server.broadcast(serverMessage, this);

        // catch (IOException ex) {
        //   System.out.println("Error in UserThread: " + ex.getMessage());
        //    ex.printStackTrace();
    }

    private void night(BufferedReader reader, String userName) throws IOException {
        String serverMessage = "\"NIGHT \"";
        String clientMessage;
        server.sendToSpecial(userName, serverMessage);

        if (this.getRoll() instanceof Mafia) {
            long start_time = System.currentTimeMillis();
            long wait_time = 1000 * 60;
            long end_time = start_time + wait_time;
            serverMessage = "\nMafias have one minute to consult.\n";
            server.sendToSpecial(userName, serverMessage);

            while (System.currentTimeMillis() < end_time) {
                try {
                    if (task == Task.NIGHT) {
                        clientMessage = reader.readLine();
                        serverMessage = "[" + userName + "]: " + clientMessage;
                    }
                    server.broadcastToMafias(serverMessage, this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        server.setLastNightDead();
        server.setWhoGodKilled(null);
        if (this.getRoll() instanceof Godfather && this.getRoll().isAlive()) {
            serverMessage = "Who do you want to kill?\n";
            server.sendToSpecial(userName, serverMessage);
            if (task == Task.NIGHT) {
                clientMessage = reader.readLine();
                UserThread userToKill = server.findUserByName(clientMessage);
//                if (userToKill.getRoll() instanceof DieHard) {
//                    if (((DieHard) userToKill.getRoll()).getNumberOfLivesLeft() > 0) {
//                        ((DieHard) userToKill.getRoll()).decreaseNumberOfivesLeft();
//                    } else {
//                        server.addLastNightDead(userToKill);
//                        userToKill.getRoll().setAlive(false);
                  //      server.setWhoGodKilled(userToKill);
//                    }
//                } else {
//                    server.addLastNightDead(userToKill);
//                    userToKill.getRoll().setAlive(false);
                    server.setWhoGodKilled(userToKill);
             //   }

            }
        }
        if (this.getRoll() instanceof DrLecter && this.getRoll().isAlive()) {
            serverMessage = "\nDr.lecter save a mafia.\n";
            server.sendToSpecial(userName, serverMessage);
            if (task == Task.NIGHT) {
                clientMessage = reader.readLine();
                if (getUserName().equalsIgnoreCase(clientMessage)) {
                    if (((DrLecter) getRoll()).isSaveHimself() == true) {
                        serverMessage = "You have already saved yourself once. Choose someone else.\n";
                        clientMessage = reader.readLine();
                        if (!getUserName().equalsIgnoreCase(clientMessage)) {
                            server.setDrlecteSave(server.findUserByName(clientMessage));
                        }
                    } else {
                        ((DrLecter) getRoll()).setSaveHimself(true);
                        server.setDrlecteSave(server.findUserByName(clientMessage));
                    }
                } else {
                    server.setDrlecteSave(server.findUserByName(clientMessage));
                }
            }
        }


        if (this.getRoll() instanceof Doctor && this.getRoll().isAlive()) {
            serverMessage = "\nDoctor save a citizen.\n";
            server.sendToSpecial(userName, serverMessage);
            if (task == Task.NIGHT) {
                clientMessage = reader.readLine();
                if (getUserName().equalsIgnoreCase(clientMessage)) {
                    if (((Doctor) getRoll()).isSaveHimself() == true) {
                        serverMessage = "You have already saved yourself once. Choose someone else.\n";
                        clientMessage = reader.readLine();
                        if (!getUserName().equalsIgnoreCase(clientMessage)) {
                            server.setDoctorSave(server.findUserByName(clientMessage));
                        }
                    } else {
                        ((Doctor) getRoll()).setSaveHimself(true);
                        server.setDoctorSave(server.findUserByName(clientMessage));
//                        server.findUserByName(clientMessage).getRoll().setAlive(true);
                    }
                } else {
                    server.setDoctorSave(server.findUserByName(clientMessage));
//                    server.findUserByName(clientMessage).getRoll().setAlive(true);
                }
            }
        }

        if (this.getRoll() instanceof Detective && this.getRoll().isAlive()) {
            serverMessage = "Detective".toUpperCase() + " Who do you want to inquire about, Detective?\n";
            server.sendToSpecial(userName, serverMessage);
            if (task == Task.NIGHT) {
                clientMessage = reader.readLine();
                UserThread userToInquire = server.findUserByName(clientMessage);
                if (userToInquire.getRoll() instanceof Citizen) {
                    serverMessage = "This player is a citizen";
                } else if (userToInquire.getRoll() instanceof Godfather) {
                    serverMessage = "This player is a CITIZEN";
                } else if (userToInquire.getRoll() instanceof Mafia && !(userToInquire.getRoll() instanceof Godfather)) {
                    serverMessage = "This player is a MAFIA";
                }
                server.sendToSpecial(userName, serverMessage);
            }
        }
        if (this.getRoll() instanceof Professional && this.getRoll().isAlive()) {
            serverMessage = "Professional".toUpperCase() + " Do you want to kill someone?\n 1)Yes 2)No\n";
            if (task == Task.NIGHT) {
                clientMessage = reader.readLine();
                if ("yes".equalsIgnoreCase(clientMessage) || "1".equalsIgnoreCase(clientMessage)) {
                    serverMessage = "Who do you want to kill?\n";
                    server.sendToSpecial(userName, serverMessage);
                    clientMessage = reader.readLine();
                    UserThread userToKill = server.findUserByName(clientMessage);
                    //&& !userToKill.equals(server.getDrlecteSave())
                    if (userToKill.getRoll() instanceof Mafia ) {
//                        userToKill.getRoll().setAlive(false);
//                        server.addLastNightDead(userToKill);
                        server.setWhoProfKilled(userToKill);
                    } else if (userToKill.getRoll() instanceof Citizen) {
                      //  this.getRoll().setAlive(false);
                        server.setWhoProfKilled(this);

                    }
                }
            }

        }
        if (this.getRoll() instanceof Psychologist && this.getRoll().isAlive()) {
            serverMessage = "Psychologist".toUpperCase() + " Do you want someone to be quiet during the day?\n 1)Yes 2)No\n";
            if (task == Task.NIGHT) {
                clientMessage = reader.readLine();
                if ("yes".equalsIgnoreCase(clientMessage) || "1".equalsIgnoreCase(clientMessage)) {
                    serverMessage = "Who do you want to keep quiet during the day? \n";
                    server.sendToSpecial(userName, serverMessage);
                    clientMessage = reader.readLine();
                    UserThread userToBeQuiet = server.findUserByName(clientMessage);
                    userToBeQuiet.getRoll().setBeQuietduringTheDay(true);
                    serverMessage = "Done!\n";
                    server.sendToSpecial(userName, serverMessage);
                }
            }
        }

        if (this.getRoll() instanceof DieHard && this.getRoll().isAlive()) {
            if (((DieHard) roll).getNumberOfQueriesLeft() <= 0) {
                serverMessage = "DIE HARD You have used all your opportunities.\n";
                server.sendToSpecial(userName, serverMessage);
            } else if (((DieHard) roll).getNumberOfQueriesLeft() > 0) {

                serverMessage = "DIE HARD Do you want the narrator to announce the deleted roles tomorrow? \n";
                server.sendToSpecial(userName, serverMessage);
                if (task == Task.NIGHT) {
                    clientMessage = reader.readLine();
                    server.setAnnounceDeletedRolls(false);
                    if ("yes".equalsIgnoreCase(clientMessage) || "1".equalsIgnoreCase(clientMessage)) {
                        server.setAnnounceDeletedRolls(true);
                        ((DieHard) roll).decreaseNumberOfQueriesLeft();
                    }
                    serverMessage = "Done!\n";
                    server.sendToSpecial(userName, serverMessage);
                }
            }
        }

    }



    private void voting(BufferedReader reader, String userName) throws IOException {
        String clientMessage;
        String serverMessage = " \" VOTING \" \n Enter a player's name : ";
        server.sendToSpecial(this.getName(), serverMessage);
        if (task==Task.VOTING){
        clientMessage = reader.readLine();
        server.getVotesMap().put(clientMessage, server.getVotesMap().get(clientMessage) + 1);}
    }

    private void day(BufferedReader reader, String userName) throws IOException {
        List<UserThread> deads = server.getLastNightDead();
        String clientMessage;
        String serverMessage = "players who dead last night:";
        if (deads != null) {
            for (UserThread user : deads) {
                serverMessage += user.getName() + " ";

                serverMessage = "[" + userName + "]: " + serverMessage;
                server.sendToSpecial(this.getName(), serverMessage);
            }
        }
        serverMessage += "\nMORNING\n";
        server.sendToSpecial(this.getName(), serverMessage);
        long start_time = System.currentTimeMillis();
        long wait_time = 1000 * 60 * 5;
        long end_time = start_time + wait_time;
        server.setReadyToVote(0);

        while ((System.currentTimeMillis() < end_time || server.getReadyToVote() == server.numberOfAlives())&& task==Task.DAY) {

            clientMessage = reader.readLine();
            if ("ready".equalsIgnoreCase(clientMessage)) {
                server.setReadyToVote(server.getReadyToVote() + 1);
            }
            if (this.getRoll().isBeQuietduringTheDay()==true && !"ready".equalsIgnoreCase(clientMessage)){}
            else {
            serverMessage = "[" + userName + "]: " + clientMessage;
            server.writeToFile(serverMessage);
            server.broadcast(serverMessage, this);}}


    }

    private void startGame(BufferedReader reader, String userName) {
        String serverMessage = "Time to start game!\n" + "Enter start if you are ready:\n";
        String clientMessage;
        server.sendToSpecial(userName, serverMessage);
        try {
            if (task==Task.START){
            clientMessage = reader.readLine();
            if ("start".equalsIgnoreCase(clientMessage)) {
                server.setWhoSentStarts(server.getWhoSentStarts() + 1);}
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
                if (task==Task.FIRST_NIGHT){
                clientMessage = reader.readLine();
                serverMessage = "[" + userName + "]: " + clientMessage;}
                server.broadcastToMafias(serverMessage, this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        serverMessage = "doctor introduce yourself to the mayor:\n";
        server.sendToSpecial(userName, serverMessage);
        if (this.getRoll() instanceof Doctor) {
            try {
                if (task==Task.FIRST_NIGHT){
                clientMessage = reader.readLine();
                serverMessage = "[" + userName + "]: " + clientMessage;}
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
