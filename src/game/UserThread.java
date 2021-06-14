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
                serverMessage = "Enter your user name:";
                sendMessage(serverMessage);

                userName = reader.readLine();
                while (true) {
                    if (server.getUserNames().contains(userName)) {
                        serverMessage = "The name you selected is a duplicate. Enter another name ";
                        sendMessage(serverMessage);
                        userName = reader.readLine();
                    } else break;
                }
                server.addUserName(userName);
                serverMessage = "New user connected: " + userName;
                server.broadcast(serverMessage, this);
                serverMessage = "your roll is :" + roll.getRollName();
                //server.sendToSpecial(userName,serverMessage);
                sendMessage(serverMessage);
            } else if (task == Task.START) {

                startGame(reader);
            } else if (task == Task.FIRST_NIGHT) {
                introduceNight(reader);
            } else if (task == Task.DAY) {
                day(reader);
            } else if (task == Task.VOTING) {
                voting(reader);
            } else if (task == Task.NIGHT) {
                night(reader);
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

    private void night(BufferedReader reader) throws IOException {
        String serverMessage = "\"NIGHT \"";
        String clientMessage;
        // server.sendToSpecial(userName, serverMessage);
        sendMessage(serverMessage);
//        if (this.getRoll() instanceof Mafia) {
//            long start_time = System.currentTimeMillis();
//            long wait_time = 1000 * 60;
//            long end_time = start_time + wait_time;
//            serverMessage = "\nMafias have one minute to consult.\n";
//            //   server.sendToSpecial(userName, serverMessage);
//            sendMessage(serverMessage);
//            while (System.currentTimeMillis() < end_time) {
//                if (task == Task.NIGHT) {
//                    clientMessage = readFromClient(reader);
//                    serverMessage = "[" + userName + "]: " + clientMessage;
//                }
//                server.broadcastToMafias(serverMessage, this);
//            }
//        }
//        try {
//            sleep(60000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        server.setLastNightDead();
        server.setWhoGodKilled(null);
        if (this.getRoll() instanceof Godfather && this.getRoll().isAlive()) {
            serverMessage = "Who do you want to kill?\n";
            // server.sendToSpecial(userName, serverMessage);
            sendMessage(serverMessage);
            if (task == Task.NIGHT) {
                clientMessage = readFromClient(reader);
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
            serverMessage="Who do you want him to kill?\n";
            sendMessage(serverMessage);
            clientMessage= readFromClient(reader);
            server.broadcastToMafias(clientMessage,this);
            serverMessage = "\nDr.lecter save a mafia.\n";
            //  server.sendToSpecial(userName, serverMessage);
            sendMessage(serverMessage);
            if (task == Task.NIGHT) {
                clientMessage = readFromClient(reader);
                if (getUserName().equalsIgnoreCase(clientMessage)) {
                    if (((DrLecter) getRoll()).isSaveHimself() == true) {
                        serverMessage = "You have already saved yourself once. Choose someone else.\n";
                        clientMessage = readFromClient(reader);
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

        if (this.getRoll() instanceof SimpleMafia && this.getRoll().isAlive()){
            serverMessage="Who do you want him to kill?\n";
            sendMessage(serverMessage);
            clientMessage= readFromClient(reader);
            server.broadcastToMafias(clientMessage,this);
        }


        if (this.getRoll() instanceof Doctor && this.getRoll().isAlive()) {
            serverMessage = "\nDoctor save a citizen.\n";
            // server.sendToSpecial(userName, serverMessage);
            sendMessage(serverMessage);
            if (task == Task.NIGHT) {
                clientMessage = readFromClient(reader);
                if (getUserName().equalsIgnoreCase(clientMessage)) {
                    if (((Doctor) getRoll()).isSaveHimself() == true) {
                        serverMessage = "You have already saved yourself once. Choose someone else.\n";
                        sendMessage(serverMessage);
                        clientMessage = readFromClient(reader);
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
            // server.sendToSpecial(userName, serverMessage);
            sendMessage(serverMessage);
            if (task == Task.NIGHT) {
                clientMessage = readFromClient(reader);
                UserThread userToInquire = server.findUserByName(clientMessage);
                if (userToInquire.getRoll() instanceof Citizen) {
                    serverMessage = "This player is a citizen";
                } else if (userToInquire.getRoll() instanceof Godfather) {
                    serverMessage = "This player is a CITIZEN";
                } else if (userToInquire.getRoll() instanceof Mafia && !(userToInquire.getRoll() instanceof Godfather)) {
                    serverMessage = "This player is a MAFIA";
                }
                // server.sendToSpecial(userName, serverMessage);
                sendMessage(serverMessage);
            }
        }
        if (this.getRoll() instanceof Professional && this.getRoll().isAlive()) {
            serverMessage = "Professional".toUpperCase() + " Do you want to kill someone?\n 1)Yes 2)No\n";
            sendMessage(serverMessage);
            if (task == Task.NIGHT) {
                clientMessage = readFromClient(reader);
                if ("yes".equalsIgnoreCase(clientMessage) || "1".equalsIgnoreCase(clientMessage)) {
                    serverMessage = "Who do you want to kill?\n";
                    // server.sendToSpecial(userName, serverMessage);
                    sendMessage(serverMessage);
                    clientMessage = readFromClient(reader);
                    UserThread userToKill = server.findUserByName(clientMessage);
                    //&& !userToKill.equals(server.getDrlecteSave())
                    if (userToKill.getRoll() instanceof Mafia) {
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
            sendMessage(serverMessage);
            if (task == Task.NIGHT) {
                clientMessage = readFromClient(reader);
                if ("yes".equalsIgnoreCase(clientMessage) || "1".equalsIgnoreCase(clientMessage)) {
                    serverMessage = "Who do you want to keep quiet during the day? \n";
                    // server.sendToSpecial(userName, serverMessage);
                    sendMessage(serverMessage);
                    clientMessage = readFromClient(reader);
                    UserThread userToBeQuiet = server.findUserByName(clientMessage);
                    userToBeQuiet.getRoll().setBeQuietduringTheDay(true);
                    serverMessage = "Done!\n";
                    // server.sendToSpecial(userName, serverMessage);
                    sendMessage(serverMessage);
                }
            }
        }

        if (this.getRoll() instanceof DieHard && this.getRoll().isAlive()) {
            if (((DieHard) roll).getNumberOfQueriesLeft() <= 0) {
                serverMessage = "DIE HARD You have used all your opportunities.\n";
                //  server.sendToSpecial(userName, serverMessage);
                sendMessage(serverMessage);
            } else if (((DieHard) roll).getNumberOfQueriesLeft() > 0) {

                serverMessage = "DIE HARD Do you want the narrator to announce the deleted roles tomorrow? \n1)yes\n2)No\n";
                //  server.sendToSpecial(userName, serverMessage);
                sendMessage(serverMessage);
                if (task == Task.NIGHT) {
                    clientMessage = readFromClient(reader);
                    server.setAnnounceDeletedRolls(false);
                    if ("yes".equalsIgnoreCase(clientMessage) || "1".equalsIgnoreCase(clientMessage)) {
                        server.setAnnounceDeletedRolls(true);
                        ((DieHard) roll).decreaseNumberOfQueriesLeft();
                    }
                    serverMessage = "Done!\n";
                    // server.sendToSpecial(userName, serverMessage);
                    sendMessage(serverMessage);
                }
            }
        }

    }


    private void voting(BufferedReader reader) throws IOException {
        String clientMessage;
        String serverMessage = " \" VOTING \" \n Enter a player's name : ";
        // server.sendToSpecial(this.getName(), serverMessage);
        sendMessage(serverMessage);
        if (task == Task.VOTING) {
            clientMessage = readFromClient(reader);
            server.getVotesMap().put(clientMessage, server.getVotesMap().get(clientMessage) + 1);
        }
        server.setCancelVoting(false);
        if (this.getRoll() instanceof Mayor) {
            serverMessage = "MAYOR do you want to cancel voting?\n1)Yes 2)No\n";
            sendMessage(serverMessage);
            if (task == Task.VOTING) {
                clientMessage = readFromClient(reader);
                if ("yes".equalsIgnoreCase(clientMessage) || "1".equalsIgnoreCase(clientMessage)) {
                    server.setCancelVoting(true);
                }
            }
        }
    }

    private void day(BufferedReader reader) throws IOException {
        List<UserThread> deads = server.getLastNightDead();
        String clientMessage;
        String serverMessage = "players who dead last night:";
        sendMessage(serverMessage);
        if (deads != null) {
            for (UserThread user : deads) {
                serverMessage += user.getName() + " ";

                serverMessage = "[" + userName + "]: " + serverMessage;
                //   server.sendToSpecial(this.getName(), serverMessage);
                sendMessage(serverMessage);
            }
        }
        for (UserThread user : deads) {
            if (user.equals(this)) {
                serverMessage = "You are dead.\n1) Do you want to leave the game?\n Or\n 2) Stay in the game as a spectator?\n";
                sendMessage(serverMessage);
                clientMessage = readFromClient(reader);
                if ("1".equalsIgnoreCase(clientMessage)) {
                    serverMessage = "Bye!\n";
                    sendMessage(serverMessage);
                    userQiutting();
                }
            }
        }
        if (server.isAnnounceDeletedRolls()) {
            serverMessage = "Die hard inquired last night\n";
            sendMessage(serverMessage);
            Collections.shuffle(server.getWholeDead());
            for (UserThread user : server.getWholeDead()) {
                serverMessage = user.getRoll().getRollName() + "\n";
                sendMessage(serverMessage);
            }
        }
        serverMessage += "\nMORNING\n";
        // server.sendToSpecial(this.getName(), serverMessage);
        sendMessage(serverMessage);
        long start_time = System.currentTimeMillis();
        long wait_time = 1000 * 60 * 5;
        long end_time = start_time + wait_time;
        server.setReadyToVote(0);

        while (System.currentTimeMillis() < end_time) {
            if (!task.equals(Task.DAY)) {
                break;
            }


            //  if (server.getReadyToVote()==server.numberOfAlives()){break;}

            if (this.getRoll().isAlive() && this.getRoll().isBeQuietduringTheDay() == false ) {
                clientMessage = readFromClient(reader);
//            if ("ready".equalsIgnoreCase(clientMessage)) {
//                server.setReadyToVote(server.getReadyToVote() + 1);
//            }
                if ("ready".equalsIgnoreCase(clientMessage)){
                    break;
                }
                    serverMessage = "[" + userName + "]: " + clientMessage;
                    server.writeToFile(serverMessage);
                    server.broadcast(serverMessage, this);

            }
        }
    }




    private void startGame(BufferedReader reader) {
        String serverMessage = "Time to start game!\n" + "Enter start if you are ready:\n";
        String clientMessage;
       // server.sendToSpecial(userName, serverMessage);
        sendMessage(serverMessage);
        if (task==Task.START){
            clientMessage = readFromClient(reader);
        if ("start".equalsIgnoreCase(clientMessage)) {
            server.setWhoSentStarts(server.getWhoSentStarts() + 1);}
        }
    }

    private void introduceNight(BufferedReader reader) {
        String serverMessage = "Introduction".toUpperCase(Locale.ROOT) + "NIGHT\n" + "Mafias introduce your rolls:\n";
        String clientMessage;
      //  server.sendToSpecial(userName, serverMessage);

        if (this.getRoll() instanceof Mafia) {
            if (task==Task.FIRST_NIGHT){
        sendMessage(serverMessage);
                clientMessage = readFromClient(reader);
            serverMessage = "[" + userName + "]: " + clientMessage;}
            server.broadcastToMafias(serverMessage, this);
        }
        serverMessage = "doctor introduce yourself to the mayor:\n";
       // server.sendToSpecial(userName, serverMessage);
        if (this.getRoll() instanceof Doctor) {
            if (task==Task.FIRST_NIGHT){
        sendMessage(serverMessage);
                clientMessage = readFromClient(reader);
            serverMessage = "[" + userName + "]: " + clientMessage;}
            // server.sendToSpecial(server.nameOfMayor(), serverMessage);
            sendMessage(serverMessage);
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
    public void userQiutting(){
        String serverMessage;
        server.removeUser(userName, this);
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        serverMessage = userName + " has quitted.";
      server.broadcast(serverMessage, this);}


      public void readDailyChatFile(){
          try {
              File myObj = new File("dailyChatFile.txt");
              Scanner myReader = new Scanner(myObj);
              while (myReader.hasNextLine()) {
                  String data = myReader.nextLine();
                  sendMessage(data);
              }
              myReader.close();
          } catch (FileNotFoundException e) {
              System.out.println("An error occurred.");
              e.printStackTrace();
          }
      }

      public String readFromClient(BufferedReader reader) {
          String clientmessage = null;
          try {
              clientmessage = reader.readLine();
          } catch (IOException e) {
              e.printStackTrace();
          }
          if ("history".equalsIgnoreCase(clientmessage)){
                  readDailyChatFile();
              String clientmessage1= null;
              try {
                  clientmessage1 = reader.readLine();
              } catch (IOException e) {
                  e.printStackTrace();
              }
              return clientmessage1;
              }
              else if ("exit".equalsIgnoreCase(clientmessage)){
                  userQiutting();
                  return null;
              }
              else {
                  return clientmessage;
              }
      }
}
