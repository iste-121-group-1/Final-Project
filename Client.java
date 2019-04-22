
//Import packages from Java API 
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import java.net.*;

// Create class extending JFrame and implementing ActionListener
class Client extends JFrame implements ActionListener {
   // Nickname Info
   JLabel jlName = new JLabel("Name:");
   JTextField jtfName = new JTextField(20);
   JButton jbName = new JButton("Login");

   // IP Address Info
   JLabel jlAddress = new JLabel("IP Address");
   JTextField jtfAddress = new JTextField(20);
   JButton jbAddress = new JButton("Connect");

   // Message Info
   JLabel jlMessage = new JLabel("Message");
   JTextField jtfMessage = new JTextField(20);
   JButton jbMessage = new JButton("Send");

   // Attributes
   public static final int SERVER_PORT = 32001;
   private Socket cSocket = null;
   public String name = "";

   // Setting attributes to null
   static Scanner scan = null;
   protected PrintWriter pwt = null;
   protected Thread listener;

   // Setting the text area to static
   static JTextArea ta = new JTextArea(10, 35);

   // Default Constructor
   public Client() {

      setSize(450, 350); // Set the size of the frame
      setLocationRelativeTo(null);
      setTitle("Client"); // Set title of the frame

      JPanel jpTopOptions = new JPanel(new GridLayout(0, 1)); // Set grid layout for the panel

      // Adding Nickname info to GUI
      JPanel jpName = new JPanel();
      jpName.add(jlName);
      jpName.add(jtfName);
      jpName.add(jbName);

      // Adding Address info to GUI
      JPanel jpAddress = new JPanel();
      jpAddress.add(jlAddress);
      jpAddress.add(jtfAddress);
      jpAddress.add(jbAddress);

      // Adding Message info to GUI
      JPanel jpMessage = new JPanel();
      jpMessage.add(jlMessage);
      jpMessage.add(jtfMessage);
      jpMessage.add(jbMessage);

      // Adding Stuff to the top panel
      jpTopOptions.add(jpName);
      jpTopOptions.add(jpAddress);
      jpTopOptions.add(jpMessage);

      add(jpTopOptions, BorderLayout.SOUTH); // Add the panel to the north area of the frame

      JPanel jpTextArea = new JPanel(); // Create a panel for text area
      JLabel jlTextArea = new JLabel("Log: "); // Create a label for for text area

      // Create scroll pane object
      JScrollPane scroll = new JScrollPane(ta);
      scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
      ta.setLineWrap(true);
      ta.setEditable(false);
      jpTextArea.add(scroll);

      // Add the text area to the center of the layout
      add(jpTextArea, BorderLayout.CENTER);

      // Implement action listener for the following attributes
      jbName.addActionListener(this);
      jbAddress.addActionListener(this);
      jbMessage.addActionListener(this);

      setDefaultCloseOperation(EXIT_ON_CLOSE); // Set a 'X' button to close the frame
      setVisible(true); // Set the frame to be visible

   } // End default constructor

   /**
    * @method actionPerformed
    * @param ActionEvent ae Actions for clicking buttons
    */
   public void actionPerformed(ActionEvent ae) {
      switch (ae.getActionCommand()) {
      case "Login": // Click 'login'
         doLogin();
         break;

      case "Logout": // Click 'logout'
         doLogout();
         break;

      case "Connect": // Click 'connect'
         doConnect();
         break;

      case "Disconnect": // Click 'disconnect'
         doDisconnect();
         break;

      case "Send": // Click 'send'
         doSend();
         break;
      }
   }

   /**
    * @method doLogin
    * @param ActionEvent ae - User clicks "login" button
    */
   private void doLogin() {
      ta.append("Client name set to : " + jtfName.getText() + "\n");
      jbName.setText("Logout");
   }

   /**
    * @method doLogout
    * @param ActionEvent ae - User clicks "logout" button
    */
   private void doLogout() {
      ta.append("\n" + jtfName.getText() + "logged out");
      jbName.setText("Login");
   }

   /**
    * @method doConnect - User clicks "connect" button
    */
   private void doConnect() {
      try {
         cSocket = new Socket(jtfAddress.getText(), SERVER_PORT); // Create a socket for IP address and port number info
         scan = new Scanner(new InputStreamReader(cSocket.getInputStream())); // Create a scanner object
         pwt = new PrintWriter(new OutputStreamWriter(cSocket.getOutputStream())); // Create a print writer object

         // Create a Thread object
         // Users Recieve a message when it is sent by another user
         Thread messageBroadcast = new Thread() {
            public void run() {
               while (true) {
                  {
                     receive();
                  }

               }
            }
         };

         messageBroadcast.setDaemon(true);
         messageBroadcast.start();

      }

      catch (IOException ioe) // ioe Exception
      {
         ta.append("IO Exception: " + ioe + "\n");
         return;
      }

      ta.append("Connected!\n"); // Message when a user is connected to other users
      jbAddress.setText("Disconnect"); // When a user disconnect the session
   }

   // @method doDisconnect - User clicks "disconnect" button
   private void doDisconnect() {

      // try and catch
      try {
         cSocket.close();
         scan.close();
         pwt.close();
         ta.append("Disconnected!\n");
      }

      catch (IOException ioe) // ioe Exception
      {
         ta.append("IO Exception: " + ioe + "\n");
         return;
      }

      jbAddress.setText("Connect"); // If the user wish to re-connect the session
   }

   /**
    * @method doSend - User clicks "send" button
    * @param none
    */
   private void doSend() {
      pwt.println(jtfName.getText() + ": " + jtfMessage.getText());
      pwt.flush();
   }

   /**
    * @method receive - User get a message from other user(s)
    * @param none
    */
   private void receive() {
      String reply = "\n" + scan.nextLine();
      ta.append(reply);
   }

   // Main method
   public static void main(String[] args) {
      new Client();
   }

} // End program