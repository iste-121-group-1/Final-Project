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
   public ArrayList<GameData> otherPlayers;
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
   private JButton jbJoin = new JButton("Join Game");

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
   private JButton leaderboardReturn;
   private JButton leaderboardQuit;
   private JFrame leaderboardScores;
   private JTextArea leaderboardArea;

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

      jbJoin.setEnabled(false);
      jbWhoIsIn.setEnabled(false);

      // Add it to ActionListener
      send.addActionListener(ae -> {
         connect.sendMessage(username, msgBox.getText());
         msgBox.setText("");
      });

      jbConnect.addActionListener(ae -> {
         if (jbConnect.getText() == "Connect") {
            try {
               cSocket = new Socket(jtfAddress.getText(), SERVER_PORT);
               sendServerData = new ObjectOutputStream(cSocket.getOutputStream());
               getServerData = new ObjectInputStream(cSocket.getInputStream());
               connect = new Connection(cSocket, sendServerData, getServerData);
               jbConnect.setText("Disconnect");
               jtfAddress.setEnabled(false);
               jbJoin.setEnabled(true);
               jbWhoIsIn.setEnabled(true);
            } catch (ConnectException ce) {
               JOptionPane.showMessageDialog(null, "Connection refused.");
            } catch (UnknownHostException uhe) {
               JOptionPane.showMessageDialog(null, "Unknown host while connecting.");
            } catch (IOException ioe) {
               JOptionPane.showMessageDialog(null, "Input/output exception while connecting.");
            }
         } else if (jbConnect.getText() == "Disconnect") {
            jbConnect.setText("Connect");
            jtfAddress.setEnabled(true);
            try {
               cSocket.close();
            } catch (IOException e) {
               JOptionPane.showMessageDialog(null, "Input/output exception while disconnecting.");
            }
         }
      });

      // "Login", set the client's username
      jbName.addActionListener(ae -> {
         if (jbName.getText() == "Login") {
            if (jtfName.getText().equals("")) {
               username = "Player";
            } else {
               username = jtfName.getText();
            }
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
         try {
            String playerlist = "Connected: ";
            for (GameData player : otherPlayers) {
               playerlist += player.getName() + " ";
            }
            JOptionPane.showMessageDialog(null, playerlist);
         } catch (NullPointerException npe) {
            JOptionPane.showMessageDialog(null, "No list of players available.");
         }
      });

      jbColor.addActionListener(colorChooser);

      jbJoin.addActionListener(ae -> {
         connect.sendState(username, GameState);
         gameStart();
      });

      menu.setVisible(true);
      this.getContentPane().add(menu, BorderLayout.CENTER);
      // END MENU/CHAT UI //

      // LEADERBOARD UI //
      leaderboard = new JPanel(new GridLayout(3, 1));
      scores = new JPanel(new GridLayout(0, 1));
      JPanel blank = new JPanel();
      leaderboardButtons = new JPanel(new GridLayout(1, 2));

      // JFrame that holds the scores. I know it's bad to have two JFrames
      // in a program. I don't care
      leaderboardScores = new JFrame();
      leaderboardScores.setDefaultCloseOperation(EXIT_ON_CLOSE);
      leaderboardArea = new JTextArea(20, 40);
      leaderboardScores.add(new JScrollPane(leaderboardArea));
      leaderboardArea.setEditable(false);
      leaderboardScores.pack();
      leaderboardScores.setLocationRelativeTo(null);
      
      /*
       *
       * READING SCORES FROM SERVER: PSEUDOCODE OR WHATEVER
       *
       * get each line from the server (server sends it as a string?)
       * leaderboardArea.append(line)
       * this should loop until the server doesnt send anything else
       * fuck with the textarea stuff to make it correct width/whatever
       * sry i cant do more ur really the mvp of this project but i dont
       * wanna fuck with the shit youre gonna rework. 
       * things to note: this can technically happen anytime bc textarea
       * is a global variable
       * i doubt the cod ewill actually happen here lol
       * dont worry about making it format nice. just get username and
       * seconds. who cares. none of us
       *
       */

      leaderboardReturn = new JButton("Play Again");
      leaderboardReturn.addActionListener(ae -> {
         stateChange(GAME_STATES.MENU);
         menu.setVisible(true);
         leaderboard.setVisible(false);
         // UNSURE HOW TO PROPERLY IMPLEMENT THIS BUT MAKE THE SCORES
         // GO AWAY WHEN YOU GO TO MENU
         if (leaderboardScores != null) {
            leaderboardScores.setVisible(false);
         }
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
      this.getContentPane().add(leaderboard, BorderLayout.SOUTH);
      // END LEADERBOARD UI //

      // finish initializing the frame
      setResizable(false);
      setVisible(true);

      setIgnoreRepaint(true);

      this.pack();

      // add key listener
      this.getContentPane().addKeyListener(this);
      this.getContentPane().setFocusable(true);

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
            stateChange(GAME_STATES.LEADERBOARD);
            leaderboard.setVisible(true);
            leaderboardScores.setVisible(true);
            endGame = System.nanoTime();
            menub.setVisible(true);
            menu.setVisible(false);
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
         // send the position data to the server every time it changes
         connect.sendPos(username, player.getX(), player.getY());
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
      if (playerC == null) {
         playerC = new Color((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255));
      }
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
      } // end while
   } // end run

   public void keyPressed(KeyEvent ke) {
      if (GameState == GAME_STATES.GAME) {
         switch (ke.getKeyCode()) {
         case KeyEvent.VK_LEFT:
            player.left = true;
            break;
         case KeyEvent.VK_RIGHT:
            player.right = true;
            break;
         }
      }
   } // end keyPressed

   public void keyReleased(KeyEvent ke) {
      if (GameState == GAME_STATES.GAME) {
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
      }
   } // end keyReleased

   public void keyTyped(KeyEvent ke) {
   } // end keyTyped

   public void stateChange(GAME_STATES state) {
      GameState = state;
      connect.sendState(username, GameState);
   }

   public void gameStart() {
      ResetGame();
      GameState = GAME_STATES.GAME;
      startGame = System.nanoTime();
      menub.setVisible(false);
      menu.setVisible(false);
   }

   public static void main(String[] args) {
      Game game = new Game(1080, 720, 30);
      game.run();
   } // end main

   // Connection Constructor with ActionListener
   class Connection extends Thread {
      ObjectOutputStream send;
      ObjectInputStream get;

      /**
       * Creates a new connection object
       * 
       * @param ss   The server socket
       * @param send A stream to send data to the client
       * @param get  A stream to recieve data from the client
       */
      public Connection(Socket ss, ObjectOutputStream send, ObjectInputStream get) {
         this.send = send;
         this.get = get;
      }

      public void run() {
         while (true) {

            // getClientData will always recieve a TextData, GameData, or will die
            // this whole block is "gonna somewhat get copied to the client once it works"
            try {
               System.out.println("this helpful message means that the getting data loop is in fact working");
               Object tempObj = get.readObject(); // create tempobj to allow typecasting
               if (tempObj instanceof TextData) {
                  System.out.println("message,, get got");
                  // DONT NEED -> String localUser = ((TextData) tempObj).username;
                  String localMessage = ((TextData) tempObj).message;
                  messageArea.append(localMessage + "\n");

               } else if (tempObj instanceof GameData) {
                  System.out.println("does it start the GameData switch?");// it does not
                  switch (((GameData) tempObj).DataType) {
                  case GAME:
                     System.out.println("this one's a game data");
                     // TODO udpate the OtherPlayers and also
                     if (((GameData) tempObj).name == username) {
                        // do nothing
                     } else {
                        // int index =
                        if (otherPlayers.contains(((GameData) tempObj).name)) {
                           int index = otherPlayers.indexOf(((GameData) tempObj).name);
                           otherPlayers.add(index, ((GameData) tempObj));
                        } else {
                           otherPlayers.add((GameData) tempObj);
                        }
                        // pass that good shit to OtherPlayers
                        // state = ((GameData) tempObj).state;
                        // color = ((GameData) tempObj).color;
                        // xpos = ((GameData) tempObj).xpos;
                        // ypos = ((GameData) tempObj).ypos;
                     }
                     break;
                  // gets a pos, compares usernames to make sure its an OtherPlayer's, updates
                  // that player
                  case POS:
                     System.out.println("this one's a pos data");
                     // TODO this needs to pass the positions to respective OtherPlayers
                     break;
                  case STATE:
                     System.out.println("this one's a state data");
                     GameState = ((GameData) tempObj).state;
                     if (GameState == GAME_STATES.GAME) {
                        System.out.println("does this ever happen");  // narrarator: it did not
                        gameStart();
                     }
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

      /**
       * Sends a message to the server
       * 
       * @param username The client's username
       * @param message  The message being sent
       */
      public void sendMessage(String username, String message) {
         try {
            send.writeObject(new TextData(username, message));
         } catch (IOException e) {
            e.printStackTrace(); // also die?
         }
      }

      /**
       * Sends the position data of the player to the server
       * 
       * @param username The username of the client whos data is being sent
       * @param xpos     The xpos being sent
       * @param ypos     The ypos being sent
       */
      public void sendPos(String username, int xpos, int ypos) {
         try {
            send.writeObject(new GameData(username, xpos, ypos));
         } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
      }

      public void sendState(String username, GAME_STATES state) {
         try {
            System.out.println("Sending the state");
            send.writeObject(new GameData(state));
            System.out.println("state sent");
         } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
      }

   } // End Connection constructor

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
         playerC = color;
      }

   }

} // end class Game
