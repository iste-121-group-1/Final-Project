package finalGame;

import java.util.*;
import java.net.*;
import java.io.*;
import javax.swing.*;

import finalGame.Game.GAME_STATES;
import finalGame.GameData;

import java.awt.*;

public class GameServer extends JFrame {

   String lock = "";

   JPanel jpTextArea;
   JLabel jlTextArea;
   JPanel ipPanel;
   JLabel ipLabel;
   // Vector objects for Socket and PrintWriter
   Vector<Socket> SocketConnection = new Vector<Socket>();
   Vector<ObjectOutputStream> clientWriters = new Vector<ObjectOutputStream>();
   Vector<ObjectInputStream> clientReaders = new Vector<ObjectInputStream>();

   // ArrayLists for client data
   ArrayList<GameData> clientData = new ArrayList<GameData>();
   // ArrayList<GAME_STATES> clientState = new ArrayList<GAME_STATES>();

   // store usernames and colors seperately?
   ArrayList<String> clientNames = new ArrayList<String>();
   ArrayList<Color> clientColors = new ArrayList<Color>();

   JTextArea textArea = new JTextArea(20, 30);
   ServerSocket sSocket;

   public GameServer() {

      // GUI
      setSize(400, 420);
      setLocationRelativeTo(null);
      setTitle("Server");

      jpTextArea = new JPanel(); // Panel for text area
      jlTextArea = new JLabel("Server Log: "); // Panel for label

      jpTextArea.add(jlTextArea); // Add label to the panel

      ipPanel = new JPanel();
      ipLabel = new JLabel();
      ipPanel.add(ipLabel);

      // Scroll panel object
      JScrollPane scroll = new JScrollPane(textArea);
      scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
      textArea.setLineWrap(true);
      textArea.setEditable(false);
      jpTextArea.add(scroll);

      add(ipPanel, BorderLayout.NORTH);
      add(jpTextArea, BorderLayout.CENTER);

      setDefaultCloseOperation(EXIT_ON_CLOSE);
      setVisible(true);
      // end GUI
      try {

         sSocket = new ServerSocket(16789);
         ipLabel.setText(InetAddress.getLocalHost().toString());
         ObjectInputStream getClientData;
         ObjectOutputStream sendClientData;

         while (true) {
            Socket cSocket = sSocket.accept();
            // object io
            getClientData = new ObjectInputStream(cSocket.getInputStream());
            sendClientData = new ObjectOutputStream(cSocket.getOutputStream());

            // add client sockets and writers to their respective vectors
            SocketConnection.add(cSocket);
            clientWriters.add(sendClientData);

            ServerThreads clientRun = new ServerThreads(cSocket, getClientData, sendClientData);
            clientRun.start();

            writeToClient("Connected Successfully");
         }
      } catch (IOException ioe) {
         writeToClient("Server failed");
         System.exit(1); // Exit with error code 1 (not sure how this works on windows)
      }

   }

   class ServerThreads extends Thread {
      Socket cs;
      ObjectInputStream getClientData;
      ObjectOutputStream sendClientData;
      String message;

      /**
       * ServerThreads is a Thread class that contains the main loop for server io.
       * 
       * @param cSocket The client socket that has connected.
       */
      ServerThreads(Socket cSocket, ObjectInputStream get, ObjectOutputStream send) {
         cs = cSocket;
         getClientData = get;
         sendClientData = send;
      }

      public void run() {

         // TODO The server also needs to keep track of the GameState of each connected
         // player, as well as their username. <- it does - josh

         // PLAN TODO
         // client will send Objects,
         // a TextData for the messaging client
         // and
         // a GameData for the game client
         // TextData will contain Type: Text and a String with the message
         // GameData will contain Type: Game
         // Player GameState
         // Player Name
         // Player Color
         // Player Position
         // thats it?

         // player data not final anymore, works now for some reason
         GAME_STATES state;
         String username;
         Color color;
         int xpos;
         int ypos;

         while (true) {

            // getClientData will always recieve a TextData, GameData, or will die
            // this whole block is gonna somewhat get copied to the client once it works
            try {
               System.out.println("recursion recursion");
               Object tempObj = getClientData.readObject(); // create tempobj to allow typecasting
               if (tempObj instanceof TextData) {
                  System.out.println("message got sent");
                  synchronized (lock) {
                     username = ((TextData) tempObj).username;
                     String message = ((TextData) tempObj).message;
                     clientMessage(username, message);
                  }
               } else if (tempObj instanceof GameData) {
                  System.out.println("gamedata getting sent");
                  state = ((GameData) tempObj).state;
                  username = ((GameData) tempObj).name;
                  clientNames.add(username); // not sure if this even remotely matters tbh
                  color = ((GameData) tempObj).color;
                  /*
                   * if (clientColors.contains(color)) { // tell client to pick a different color
                   * writeToClient("Pick a different color " + username + "!"); } else {
                   * clientColors.add(color); }
                   */
                  xpos = ((GameData) tempObj).xpos;
                  ypos = ((GameData) tempObj).ypos;
                  for (ObjectOutputStream sender : clientWriters) {
                     try {
                        sender.writeObject(new GameData(state, username, color, xpos, ypos));
                     } catch (IOException ioe) {
                        // TODO: handle io exception
                     }
                  }
               } else if (tempObj instanceof PosObject) {
                  // if a POS object is recieved, the username and pos data is stored and
                  // immediately sent to all connected clients
                  username = ((PosObject) tempObj).name;
                  xpos = ((PosObject) tempObj).xpos;
                  ypos = ((PosObject) tempObj).ypos;
                  writeToLog("updating pos, x: " + xpos + ", y: " + ypos);
                  synchronized (lock) {
                     updateClientPos(username, xpos, ypos);
                  }
               } else if (tempObj instanceof StateObject) {
                  state = ((StateObject) tempObj).state;
                  // username = ((GameData) tempObj).name;
                  writeToLog("updating game state");
                  synchronized (lock) {
                     updateClientState(state);
                  }
               } else if (tempObj instanceof LeaderboardData) {
                  writeToLog("updating leaderboard");
                  synchronized (lock) {
                     FileWriter writer = new FileWriter("file.txt");
                     String time = ((LeaderboardData) tempObj).data;
                     writer.append(time + "\n");
                     writer.close();
                     writeToLog("done writing to file.txt, reading now");
                     BufferedReader reader = new BufferedReader(new FileReader("file.txt"));
                     String scoreLine = reader.readLine(); // read line that contains scores
                     String scores = "test player: 60.982345483 \n";
                     writeToLog("start looping through scores");
                     // attempting to loop just makes it not work at all for some reason
                     scores = scores += (scoreLine + "\n");
                     writeToLog(scores);
                     updateLeaderboard(scores);
                     reader.close();
                  }
               }
            } catch (Exception e) {
               // TODO: handle exception
            }
         }

      }

   }

   /**
    * Main event loop
    * 
    * @param args array of command line argument Strings
    */
   public static void main(String[] args) {
      new GameServer();
   }

   /**
    * This method takes the provided message, appends it to the local text area and
    * sends it to all connected clients as a TextData
    * 
    * @param _message The string that the is to be sent to both the client and the
    *                 log.
    */
   public void writeToClient(String _message) {
      for (ObjectOutputStream sendClientData : clientWriters) {
         String builderString = "SERVER: " + _message;
         try {
            sendClientData.writeObject(new TextData(builderString));
         } catch (IOException ioe) {
            // TODO: handle io exception
         }
      }
      textArea.append("SERVER: " + _message + "\n");
   }

   /**
    * writeToLog is a convenience method for writing to the server log.
    * 
    * @param _message The string that is logged by the server.
    */
   public void writeToLog(String _message) {
      textArea.append("LOG: " + _message + "\n");
   }

   /**
    * Method to take a username and text message and send to all clients
    * 
    * @param _username the username of the client sending the messsage
    * @param _message  the message that is being sent
    */
   public void clientMessage(String _username, String _message) {
      String builderString = _username + ": " + _message;
      textArea.append(builderString + "\n");
      for (ObjectOutputStream sender : clientWriters) {
         try {
            sender.writeObject(new TextData(builderString));
         } catch (IOException ioe) {
            // TODO: handle io exception
         }
      }
   }

   /**
    * Method to update each clients position
    * 
    * @param username the username of the player whos position is being updated
    * @param _xpos    the xpos to update the player xpos to
    * @param _ypos    the ypos to update the player ypos to
    */
   public void updateClientPos(String username, int _xpos, int _ypos) {
      for (ObjectOutputStream sender : clientWriters) {
         // ObjectOutputStream sender = clientWriters.get(clientId);
         try {
            sender.writeObject(new PosObject(username, _xpos, _ypos));
         } catch (IOException ioe) {
            // TODO: handle io exception
         }
      }
   }

   /**
    * Method to update each client's Game State
    *
    * @param username the username of the player who is being updated
    * @param state    the GAME_STATES to update the clients to
    */
   public void updateClientState(GAME_STATES state) {
      for (ObjectOutputStream sender : clientWriters) {
         // ObjectOutputStream sender = clientWriters.get(clientId);
         try {
            sender.writeObject(new StateObject(state));
         } catch (IOException ioe) {
            // TODO: handle io exception
         }
      }
   }

   /**
    * Method to update the leaderboard data
    *
    * @param data the username and time in seconds of the latest winner
    */
   public void updateLeaderboard(String data) {
      for (ObjectOutputStream sender : clientWriters) {
         // ObjectOutputStream sender = clientWriters.get(clientId);
         try {
            sender.writeObject(new LeaderboardData(data));
         } catch (IOException ioe) {
            // TODO: handle io exception
         }
      }
   }
}

// TODO game only startable once 4 players connected
// TODO game cannot have more than 4 players connected at once