package finalGame;

import java.util.*;
import java.net.*;
import java.io.*;
import javax.swing.*;

import finalGame.Game.GAME_STATES;
import finalGame.GameData;

import java.awt.*;

public class GameServer extends JFrame {
   JPanel jpTextArea;
   JLabel jlTextArea;
   // Vector objects for Socket and PrintWriter
   Vector<Socket> SocketConnection = new Vector<Socket>();
   Vector<ObjectOutputStream> clientWriters = new Vector<ObjectOutputStream>();
   Vector<ObjectInputStream> clientReaders = new Vector<ObjectInputStream>();

   // ArrayLists for client data
   ArrayList<GameData> clientData = new ArrayList<GameData>();
   // ArrayList<GAME_STATES> clientState = new ArrayList<GAME_STATES>();

   // store usernames and colors seperately, since they are only used once
   ArrayList<String> clientNames = new ArrayList<String>();
   ArrayList<Color> clientColors = new ArrayList<Color>();
   // ArrayList<Integer> clientXpos = new ArrayList<Integer>();
   // ArrayList<Integer> clientYpos = new ArrayList<Integer>();
   
   // ArrayList to hold available colors
   ArrayList<Color> availableColors = new ArrayList<Color>();


   JTextArea textArea = new JTextArea(20, 30);

   public GameServer() {

      // GUI
      setSize(400, 420);
      setLocationRelativeTo(null);
      setTitle("Server");

      jpTextArea = new JPanel(); // Panel for text area
      jlTextArea = new JLabel("Server Log: "); // Panel for label

      jpTextArea.add(jlTextArea); // Add label to the panel

      // Scroll panel object
      JScrollPane scroll = new JScrollPane(textArea);
      scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
      textArea.setLineWrap(true);
      textArea.setEditable(false);
      jpTextArea.add(scroll);

      add(jpTextArea, BorderLayout.CENTER);

      setDefaultCloseOperation(EXIT_ON_CLOSE);
      setVisible(true);
      // end GUI

      try {

         ServerSocket sSocket = new ServerSocket(16789);
         ObjectInputStream getClientData;
         ObjectOutputStream sendClientData;

         while (true) {
            Socket cSocket = sSocket.accept();
            // PrintWriter printWriter = new PrintWriter(new
            // OutputStreamWriter(cSocket.getOutputStream()));
            // switched to ObjectOutputStreams
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

         // TODO The server needs to both send and recieve a Pair() of x and y locations
         // CONSTANTLY
         // This will let the client both send where its player is and recieve where the
         // other players are.
         // TODO The server also needs to keep track of the GameState of each connected
         // player, as well as their username.
         // seperate socket?
         // switch statements for recieved data types
         // something else?

         // PLAN TODO
         // for now, assume client will send Objects,
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

         // getClientData // = new ObjectInputStream(cs.getInputStream()); these are now
         // passed in to prevent duplication
         // sendClientData// = new ObjectOutputStream(cs.getOutputStream());

         // player data <- not sure why these have to be final, might cause problems?
         final GAME_STATES state;
         final String name;
         final Color color;
         final int xpos;
         final int ypos;

         // getClientData will always recieve a TextData, GameData, or will die
         try {
            // most of this switch statement should be considered a war crime, be warned
            DataObject tempObject = (DataObject) getClientData.readObject();
            switch (tempObject.DataType) {
            case TEXT:
               synchronized ("lock") {
                  writeToClient(((TextData) tempObject).message);
               }
               break;

            case GAME:
               state = ((GameData) tempObject).state;
               name = ((GameData) tempObject).name;
               color = ((GameData) tempObject).color;
               if (clientColors.contains(color)) {
                  // tell client to pick a different color
                  writeToClient("Pick a different color " + name + "!");
               }
               xpos = ((GameData) tempObject).xpos;
               ypos = ((GameData) tempObject).ypos;
               break;
            default:
               // do nothing at all
               break;
            }

         } catch (Exception e) {
            // TODO: handle exception
         }

         // TODO change this block to send username and message to client, as part of the
         // chat program
         // TODO change this whole block to use the object io bits that are passed in
         // not really sure if I will end up using this ↓ <- wont
         // inReader = new BufferedReader(new InputStreamReader(cs.getInputStream()));
         // outWriter = new PrintWriter(new OutputStreamWriter(cs.getOutputStream()));
         /*
          * while ((message = inReader.readLine()) != null) {
          * textArea.append("Server read: " + message + "\n");
          * 
          * // Loop for PrintWriter object from Vector list for (PrintWriter writer :
          * clientWriters) { writer.println(message); writer.flush(); }
          * 
          * }
          */

         try { // TODO wait this will happen every time wont it
            writeToClient("Disconnected");// Send (disconnect message) to clients before sockets are closed
            getClientData.close();
            sendClientData.close();
            cs.close();
         } catch (IOException ie) {
            writeToClient("Disconnected");
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
      for (ObjectOutputStream sender : clientWriters) {
         String builderString = _username + ": " + _message;
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
    * @param _xpos the xpos to update the player xpos to
    * @param _ypos the ypos to update the player ypos to
    */
   public void updateClientPos(int _xpos, int _ypos) {
      for (ObjectOutputStream sender : clientWriters) {
         // ObjectOutputStream sender = clientWriters.get(clientId);
         try {
            sender.writeObject(new GameData(_xpos, _ypos));
         } catch (IOException ioe) {
            // TODO: handle io exception
         }
      }
   }

   /**
    * Method to update each client's Game State
    *
    * @param state the GAME_STATES to update the clients to
    */
   public void updateClientState(GAME_STATES state) {
      for (ObjectOutputStream sender : clientWriters) {
         // ObjectOutputStream sender = clientWriters.get(clientId);
         try {
            sender.writeObject(new GameData(state));
         } catch (IOException ioe) {
            // TODO: handle io exception
         }
      }
   }
}

// TODO game only startable once 4 players connected
// TODO game cannot have more than 4 players connected at once