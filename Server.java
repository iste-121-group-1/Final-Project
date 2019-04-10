//Name Group: Ike Chukz
//Course: ISTE 121-02
//Instructor: Michael Flueser
//Homework#07 (Team): Server

// Packages from Java API
import java.util.*;
import java.net.*;
import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// Create Server class extending from JFrame 
public class Server extends JFrame {
   JPanel jpTextArea;
   JLabel jlTextArea;
   // Vector objects for Socket and PrintWriter
   Vector<Socket> SocketConnection = new Vector<Socket>();
   Vector<PrintWriter> multiClients = new Vector<PrintWriter>();

   // BufferedReader object
   BufferedReader br;

   // Text area object
   JTextArea ta = new JTextArea(20, 30);

   // Constructor Server
   public Server() {

      setSize(400, 420);
      setLocationRelativeTo(null);
      setTitle("Server");

      jpTextArea = new JPanel(); // Panel for text area
      jlTextArea = new JLabel("Server Log: "); // Paenl for label

      jpTextArea.add(jlTextArea); // Add label to the panel

      // Scroll panel object
      JScrollPane scroll = new JScrollPane(ta);
      scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
      ta.setLineWrap(true);
      ta.setEditable(false);
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

            // Both Client Socket and printwriter in arrays
            SocketConnection.add(cSocket);
            multiClients.add(pw);

            // An object for ServerThreads, then run as multithread.
            ServerThreads clientRun = new ServerThreads(cSocket);

            clientRun.start();
            ta.append("Connect Successfully\n");
         }
      }

      catch (IOException ioe) {
         ta.append("Server failed\n");
      }

   } // End of default constructor

   // Constructor
   class ServerThreads extends Thread {
      // Required attributes
      Socket cs;
      BufferedReader br;
      PrintWriter pwt;
      String msg;

      // constructor ServerThreads and param (Socket cSocket - cs
      ServerThreads(Socket cSocket) {
         cs = cSocket;
      }

      // Run method
      public void run() {
         try {
            // Objects for BufferedReader and PrintWriter
            br = new BufferedReader(new InputStreamReader(cs.getInputStream()));
            pwt = new PrintWriter(new OutputStreamWriter(cs.getOutputStream()));

            while ((msg = br.readLine()) != null) {
               ta.append("Server read: " + msg + "\n");

               // Loop for PrintWriter object from Vector list
               for (PrintWriter writer : multiClients) {
                  writer.println(msg);
                  writer.flush();
               }

            }
         }

         catch (IOException ioe) {
            System.out.println("Exception thrown  :" + ioe);
            ta.append("Bad run\n");
         }

         try {
            br.close();// close BufferedReader object
            pwt.close();// Close PrintWriter object
            cs.close();// Close the socket
            ta.append("Disconnected\n");// Send (disconnect message) to clients
         }

         catch (IOException ie) {
            ta.append("Disconnected");
         }

      } // End of run method

   } // End of ServerThreads class

   // Main method
   public static void main(String[] args) {
      new Server(); // Server constructor

   } // End of main method

} // End of this program