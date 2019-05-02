package finalGame;

import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.net.*;
import java.io.*;
import javax.swing.border.*;
import javax.swing.*;

/**
 *
 * Basic game demo. Code based off of and build from a game engine by Davis
 * Smith, another RIT student. They are reachable at dgs4349@g.rit.edu.
 *
 */

public class Game extends JFrame implements KeyListener {
   // window vars
   private final int MAX_FPS;
   private final int WIDTH;
   private final int HEIGHT;

   // game states
   public enum GAME_STATES {
      MENU, GAME, LEADERBOARD
   } // end game_states enumeration

   // List player
   public ArrayList<GameData> connectedPlayers;
   public ArrayList<Player> otherPlayers;
   private JColorChooser chipColor;
   private ArrayList<Player> playerArray;
   private Container container;

   public GAME_STATES GameState;

   JTextArea messageArea;
   // JTextField msgBox, nameField;
   // JButton send;
   JMenu file;
   JMenuItem exit;
   JPanel jpCenter;
   // JLabel msgLabel;
   JScrollPane scroll;

   // Menu UI elements
   private JMenuBar menub;
   private JPanel menu;

   // Address
   private JLabel jlAddress = new JLabel("Server IP");
   private JTextField jtfAddress = new JTextField(20);
   private static JButton jbConnect = new JButton("Connect");

   // Name Info
   JLabel jlName = new JLabel("Name:");
   JTextField jtfName = new JTextField(20);
   JButton jbName = new JButton("Login");

   // Message
   JLabel msgLabel = new JLabel("Message");
   JTextField msgBox = new JTextField(20);
   JButton send = new JButton("Send");

   private JButton jbWhoIsIn = new JButton("Who is in");
   private JButton jbColor = new JButton("Choose Color");
   private JButton jbJoin = new JButton("Join");

   // Attributes
   public static final int SERVER_PORT = 16789; // dont change this again - josh
   private Socket cSocket = null;
   public String username;
   private static Connection connect;
   private ColorChooser colorChooser;
   private BufferedReader br = null;

   // Setting attributes to null
   static Scanner scan = null;
   protected PrintWriter pw = null;
   protected Thread listener;

   // Networking
   private ObjectInputStream getServerData;
   private ObjectOutputStream sendServerData;

   // Leaderboard UI elements
   private JPanel leaderboard;
   private JPanel scores;
   private JPanel leaderboardButtons;
   private ArrayList<JLabel> jlLeaderboardArray;
   private JButton leaderboardReturn;
   private JButton leaderboardQuit;

   // double buffer
   private BufferStrategy strategy;

   // loop variables
   private boolean isRunning = true;
   private long rest = 0;

   // timing variables
   private float dt;
   private long lastFrame;
   private long startFrame;
   private int fps;

   // game object handlers
   private Player player;
   private Terrain level;
   private LocationView location;

   // Player color stuff
   private Color playerC;

   // Timing stuff
   private long startGame;
   private long endGame;
   private double gameTotal;

   public Game(int width, int height, int fps) {

      super("Cool Platformer");
      this.MAX_FPS = fps;
      this.WIDTH = width;
      this.HEIGHT = height;

   } // End constructor

   void init() {
      // initialized JFrame
      setPreferredSize(new Dimension(WIDTH, HEIGHT));
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      getContentPane().setLayout(new BorderLayout());
      setBounds(0, 0, WIDTH, HEIGHT);

      // set initial last frame value
      lastFrame = System.currentTimeMillis();

      // MENU/CHAT UI //

      // MenuBar object and details
      menub = new JMenuBar();
      JMenu help = new JMenu("Help");
      menub.add(help);
      setJMenuBar(menub);
      JMenuItem about = new JMenuItem("About");
      JMenuItem exit = new JMenuItem("Exit");
      help.add(about);
      help.add(exit);

      // ActionListener method for exit only
      exit.addActionListener(ae -> {
         System.exit(0);
      });

      about.addActionListener(ae -> {
         JOptionPane.showMessageDialog(null, "Cool Platformer by Cool Team.");
      });

      menu = new JPanel();

      // Address
      JPanel jpAddress = new JPanel();
      jpAddress.add(jlAddress);
      jpAddress.add(jtfAddress);
      jpAddress.add(jbConnect);

      // Adding Name info to GUI
      JPanel jpName = new JPanel();
      jpName.add(jlName);
      jpName.add(jtfName);
      jpName.add(jbName);

      // Message
      JPanel jpMessage = new JPanel();
      jpMessage.add(msgLabel);
      jpMessage.add(msgBox);
      jpMessage.add(send);

      JPanel jpSSouth = new JPanel(new GridLayout(0, 1));
      jpSSouth.add(jpAddress);
      jpSSouth.add(jpName);
      jpSSouth.add(jpMessage);

      // Create a panel for the center of the frame
      jpCenter = new JPanel(new FlowLayout());
      // Set up TextArea
      messageArea = new JTextArea(30, 70);
      // Scroll pane object details
      scroll = new JScrollPane(messageArea);
      scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
      messageArea.setEditable(false);
      messageArea.setLineWrap(true);
      messageArea.setWrapStyleWord(true);
      jpCenter.add(scroll); // Add scroll pane to the panel

      // Join
      JPanel jpWhoIsIn = new JPanel();
      jpWhoIsIn.add(jbWhoIsIn);

      // Panel button
      JPanel jpButton = new JPanel(new FlowLayout());

      jpButton.add(jbColor);
      jpButton.add(jbWhoIsIn);
      jpButton.add(jbJoin);

      menu.add(jpButton);
      menu.add(jpCenter);
      menu.add(jpSSouth);

      // An object
      colorChooser = new ColorChooser();

      send.setEnabled(false);
      msgBox.setEnabled(false);

      // Add it to ActionListener
      send.addActionListener(ae -> {
         // send a message
         try {
            connect.sendMessage(username, msgBox.getText());
            msgBox.setText("");
         } catch (Exception e) {
            // die?
         }
      });

      jbConnect.addActionListener(ae -> {
         if (jbConnect.getText() == "Connect") {
            jtfAddress.setEnabled(false);
            jbConnect.setText("Disconnect");
            try {
               cSocket = new Socket(jtfAddress.getText(), SERVER_PORT);
               sendServerData = new ObjectOutputStream(cSocket.getOutputStream());
               getServerData = new ObjectInputStream(cSocket.getInputStream());
               connect = new Connection(cSocket, sendServerData, getServerData);
            } catch (Exception e) {
               e.printStackTrace();// die?
            }
         } else if (jbConnect.getText() == "Disconnect") {
            jbConnect.setText("Connect");
            jtfAddress.setEnabled(true);
            try {
               cSocket.close();
            } catch (IOException e) {
               e.printStackTrace(); // do we even want this?
            }
         }
      });

      // "Login", set the client's username
      jbName.addActionListener(ae -> {
         if (jbName.getText() == "Login") {
            username = jtfName.getText();
            messageArea.append("Client name set to : " + username + "\n");
            jbName.setText("Logout");
            jtfName.setEnabled(false);
            // able to send messages after connected and logged in
            send.setEnabled(true);
            msgBox.setEnabled(true);
         } else if (jbName.getText() == "Logout") {
            messageArea.append(username + " disconnected");
            jbName.setText("Login");
            jtfName.setEnabled(true);
            // no longer logged in, no longer able to send messages
            send.setEnabled(false);
            msgBox.setEnabled(false);
         } else {
            System.exit(0);
         }
      });

      jbWhoIsIn.addActionListener(ae -> {
         String playerlist = "Connected: ";
         for (GameData player : connectedPlayers) {
            playerlist += player.getName() + " ";
         }
         JOptionPane.showMessageDialog(null, playerlist);
      });

      jbColor.addActionListener(colorChooser);

      jbJoin.addActionListener(ae -> {
         ResetGame();
         GameState = GAME_STATES.LEADERBOARD;
         this.getContentPane().remove(menu);
         this.getContentPane().add(leaderboard);
         /*
          * GameState = GAME_STATES.GAME; startGame = System.nanoTime();
          * menub.setVisible(false); menu.setVisible(false);
          */

      });

      menu.setVisible(true);
      this.getContentPane().add(menu);
      // END MENU/CHAT UI //

      // LEADERBOARD UI //
      leaderboard = new JPanel(new GridLayout(3, 1));
      scores = new JPanel(new GridLayout(0, 1));
      JPanel blank = new JPanel();
      leaderboardButtons = new JPanel(new GridLayout(1, 2));

      jlLeaderboardArray = new ArrayList<JLabel>();

      JLabel test = new JLabel("Test");

      scores.add(test);

      // TO IMPLEMENT -- LISTING SCORES PROPERLY

      leaderboardReturn = new JButton("Play Again");
      leaderboardReturn.addActionListener(ae -> {
         GameState = GAME_STATES.MENU;
         this.getContentPane().add(menu);
         this.getContentPane().remove(leaderboard);
      });

      leaderboardQuit = new JButton("Quit");
      leaderboardQuit.addActionListener(ae -> {
         System.exit(0);
      });

      leaderboardButtons.add(leaderboardReturn);
      leaderboardButtons.add(leaderboardQuit);

      leaderboard.add(scores);
      leaderboard.add(blank);
      leaderboard.add(leaderboardButtons);

      leaderboard.setVisible(false);
      // END LEADERBOARD UI //

      // finish initializing the frame
      setResizable(false);
      setVisible(true);

      setIgnoreRepaint(true);

      this.pack();

      // add key listener
      addKeyListener(this);

      // create double buffer strategy
      createBufferStrategy(2);
      strategy = getBufferStrategy();

      GameState = GAME_STATES.MENU;
      setLocationRelativeTo(null);
   } // end initialization

   private void update() {
      fps = (int) (1f / dt);
      switch (GameState) {
      case MENU:
         break;
      case GAME:
         player.update(level);
         level.update(player);
         location.update(player);
         if (player.win) {
            GameState = GAME_STATES.LEADERBOARD;
            this.getContentPane().add(leaderboard);
            endGame = System.nanoTime();
            menub.setVisible(false);
            this.getContentPane().remove(menu);
            long elapsedTime = endGame - startGame;
            gameTotal = (double) elapsedTime / 1000000000.0;
            System.out.println(gameTotal);
         } // end if checking if player has won
         break;
      case LEADERBOARD:
         break;
      } // end switch
   } // end update

   public void draw() {
      switch (GameState) {
      case MENU:
         break;
      case GAME:
         // get canvas
         Graphics2D g = (Graphics2D) strategy.getDrawGraphics();

         // clear screen
         g.setColor(Color.white);
         g.fillRect(0, 0, WIDTH, HEIGHT);

         // draw images
         player.draw(g);
         level.draw(g);
         g.setColor(Color.WHITE);
         g.fillRect(350, 50, 350, 50);
         location.draw(g);

         // release resourcecs, show the buffer
         g.dispose();
         strategy.show();
         break;
      case LEADERBOARD:
         break;
      } // end switch
   } // end draw

   public void ResetGame() {
      level = new Terrain();
      playerC = new Color(255, 0, 0);
      player = new Player(50, 50, playerC);
      location = new LocationView(playerC);
   } // end ResetGame

   public void run() {

      init();

      while (isRunning) {
         // new loop, clock the start
         startFrame = System.currentTimeMillis();
         // calculate delta time
         dt = (float) (startFrame - lastFrame) / 1000;
         // log the current time
         lastFrame = startFrame;

         // call update and draw methods
         draw();
         update();

         // dynamic thread sleep, only sleep the time we need to cap framerate
         rest = (1000 / MAX_FPS) - (System.currentTimeMillis() - startFrame);
         if (rest > 0) {
            try {
               Thread.sleep(rest);
            } catch (InterruptedException ie) {
               ie.printStackTrace();
            } // end try-catch
         } // end if

         // do some checks to get some server comms working
         System.out.println("main loop test");
         if (!(connect == null)) {
            connect.run();
         }

      } // end while
   } // end run

   public void keyPressed(KeyEvent ke) {
      switch (GameState) {
      case GAME:
         switch (ke.getKeyCode()) {
         case KeyEvent.VK_LEFT:
            player.left = true;
            break;
         case KeyEvent.VK_RIGHT:
            player.right = true;
            break;
         }
         break;
      }
   } // end keyPressed

   public void keyReleased(KeyEvent ke) {
      switch (GameState) {
      case GAME:
         switch (ke.getKeyCode()) {
         case KeyEvent.VK_LEFT:
            player.left = false;
            break;
         case KeyEvent.VK_RIGHT:
            player.right = false;
            break;
         case KeyEvent.VK_SPACE:
            player.jump = true;
            break;
         }
         break;
      }
   } // end keyReleased

   public void keyTyped(KeyEvent ke) {
   } // end keyTyped

   public static void main(String[] args) {
      Game game = new Game(1080, 720, 30);
      game.run();
   } // end main

   // Connection Constructor with ActionListener
   class Connection extends Thread {
      ObjectOutputStream send;
      ObjectInputStream get;

      public Connection(Socket ss, ObjectOutputStream send, ObjectInputStream get) {
         this.send = send;
         this.get = get;
      }

      public void run() {
         while (true) {

            // getClientData will always recieve a TextData, GameData, or will die
            // this whole block is "gonna somewhat get copied to the client once it works"
            // now in the client idiot
            try {
               System.out.println("this helpful message means that the getting data loop is in fact working");
               Object tempObj = get.readObject(); // create tempobj to allow typecasting
               if (tempObj instanceof TextData) {
                  System.out.println("message,, get got");
                  //DONT NEED -> String localUser = ((TextData) tempObj).username;
                  String localMessage = ((TextData) tempObj).message;
                  messageArea.append(localMessage + "\n");

               } else if (tempObj instanceof GameData) {
                  switch (((GameData) tempObj).DataType) {
                  case GAME:
                     System.out.println("how about this shit motherfucker");
                     /*
                      * state = ((GameData) tempObj).state; username = ((GameData) tempObj).name;
                      * color = ((GameData) tempObj).color; if (clientColors.contains(color)) { //
                      * tell client to pick a different color writeToClient("Pick a different color "
                      * + username + "!"); } else { clientColors.add(color); } xpos = ((GameData)
                      * tempObj).xpos; ypos = ((GameData) tempObj).ypos;
                      */
                     break;
                  // if a POS object is recieved, the username and pos data is stored and
                  // immediately sent to all connected clients
                  case POS:
                     /*
                      * username = ((GameData) tempObj).name; xpos = ((GameData) tempObj).xpos; ypos
                      * = ((GameData) tempObj).ypos; updateClientPos(username, xpos, ypos);
                      */
                     break;
                  case STATE:
                     break;
                  default:
                     break;
                  }
               }

            } catch (Exception e) {
               // TODO: handle exception
            }
         }
      }

      public void sendMessage(String username, String message) {
         try {
            send.writeObject(new TextData(username, message));
         } catch (IOException e) {
            e.printStackTrace(); // also die?
         }
      }
   } // End Connection constructor

} // end class Game

class ColorChooser extends JFrame implements ActionListener {
   JButton jbColor;
   Container container;

   ColorChooser() {
      container = getContentPane();
      container.setLayout(new FlowLayout());
      jbColor = new JButton("Choose color");
      jbColor.addActionListener(this);
      container.add(jbColor);
   }

   public void actionPerformed(ActionEvent e) {
      Color initialcolor = Color.RED;
      Color color = JColorChooser.showDialog(this, "Select a color", initialcolor);
      container.setBackground(color);
   }

}