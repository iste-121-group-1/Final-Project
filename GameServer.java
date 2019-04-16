import java.util.*;
import java.net.*;
import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// Create Server class extending from JFrame 
public class GameServer extends JFrame {
   JPanel jpTextArea;
   JLabel jlTextArea;
   // Vector objects for Socket and PrintWriter
   Vector<Socket> SocketConnection = new Vector<Socket>();
   Vector<PrintWriter> multiClients = new Vector<PrintWriter>();

   // BufferedReader object
   BufferedReader bufferedReader;

   // Text area object
   JTextArea textArea = new JTextArea(20, 30);

   // Constructor Server
   public GameServer() {

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

      add(jpTextArea, BorderLayout.CENTER); // Add text area panel to the center of borderlayout

      setDefaultCloseOperation(EXIT_ON_CLOSE);
      setVisible(true);

      try {

         // An object for ServerSocket that is using localhost as port number
         ServerSocket sSocket = new ServerSocket(16789);

         while (true) {
            // Accept from client
            Socket cSocket = sSocket.accept();
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(cSocket.getOutputStream()));

            // Both Client Socket and printwriter in vectors
            SocketConnection.add(cSocket);
            multiClients.add(pw);

            // An object for ServerThreads, then run as multithread.
            ServerThreads clientRun = new ServerThreads(cSocket);

            clientRun.start();
            writeToClient("Connect Successfully");
         }
      }

      catch (IOException ioe) {
         writeToClient("Server failed");
      }

   } // End of default constructor

   class ServerThreads extends Thread {
      // Required attributes
      Socket cs;
      BufferedReader inReader;
      PrintWriter outWriter;
      String message;

      /**
       * ServerThreads is a Thread class that contains the main loop for server io.
       * @param cSocket The client socket that has connected.
       */
      ServerThreads(Socket cSocket) {
         cs = cSocket;
      }

      public void run() {
         try {

            // The server needs to both send and recieve a Pair() of x and y locations CONSTANTLY
            // This will let the client both send where its player is and recieve where the other players are.
            // seperate socket?
            // switch statements for recieved data types?
            // something else?

            // Objects for BufferedReader and PrintWriter
            inReader = new BufferedReader(new InputStreamReader(cs.getInputStream()));
            outWriter = new PrintWriter(new OutputStreamWriter(cs.getOutputStream()));

            while ((message = inReader.readLine()) != null) {
               textArea.append("Server read: " + message + "\n");

               // Loop for PrintWriter object from Vector list
               for (PrintWriter writer : multiClients) {
                  writer.println(message);
                  writer.flush();
               }

            }
         } catch (IOException ioe) {
            System.out.println("Exception thrown  :" + ioe);
            writeToClient("Bad run");
         }

         try {
            writeToClient("Disconnected");// Send (disconnect message) to clients before sockets are closed
            inReader.close();// close BufferedReader object
            outWriter.close();// Close PrintWriter object
            cs.close();// Close the socket
         } catch (IOException ie) {
            writeToClient("Disconnected");
         }

      } // End of run method

   } // End of ServerThreads class

   public static void main(String[] args) {
      new GameServer();

   } // End of main method

   /**
    * writeToClient This method takes the provided message, appends it to the local
    * text area and sends it to all connected clients.
    * 
    * @param _message The string that the is to be sent to both the client and the log.
    */
   public void writeToClient(String _message) {
      for (PrintWriter writer : multiClients) {
         writer.println(_message);
         writer.flush();
      }
      textArea.append(_message + "\n");
   }

   /**
    * writeToLog is a convience method for writing to the server log.
    * @param _message The string that is logged by the server.  
    */
   public void writeToLog(String _message) {
      textArea.append(_message + "\n");
   }

}