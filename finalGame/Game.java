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
      MENU, CREATE, GAME, LEADERBOARD
   } // end game_states enumeration

   public GAME_STATES GameState;

   // player time
   public static long TIME;

   // pause
   public boolean isPaused = false;

   // Menu UI elements
   private JPanel menu;
   private JLabel jlAddress = new JLabel("Server IP");
   private JTextField jtfAddress = new JTextField(20);
   private JButton jbConnect = new JButton("Connect");

   JTextArea area;
   JTextField msgBox, nameField;
   JButton send;
   JMenu file;
   JMenuItem exit;
   JPanel jp, ip, jpCenter, jpSouthBorder, jpSNorth, jpSSouth;
   JLabel msgLabel;
   JScrollPane scroll;
   JoinGame Join;

   // Name
   // Nickname Info
   JLabel jlName = new JLabel("Name:");
   JTextField jtfName = new JTextField(20);

   private JButton jbJoin = new JButton("Join Game");
   private JButton jbWhoIsIn = new JButton("Who is in");
   private JButton jbStart = new JButton("Start Game");

   // Text Area
   private JLabel jlaArea;
   private JTextArea jtaArea = new JTextArea(20, 30);

   // Attributes
   public static final int SERVER_PORT = 32001;
   private Socket cSocket = null;
   public String name = "";

   // Setting attributes to null
   static Scanner scan = null;
   protected PrintWriter pwt = null;
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

   public Game(int width, int height, int fps) {

      super("Race Demo");
      this.MAX_FPS = fps;
      this.WIDTH = width;
      this.HEIGHT = height;

   } // end constructor

   void init() {
      // initialized JFrame
      setPreferredSize(new Dimension(WIDTH, HEIGHT));
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      getContentPane().setLayout(new BorderLayout());
      setBounds(0, 0, WIDTH, HEIGHT);

      // set initial last frame value
      lastFrame = System.currentTimeMillis();

      // MENU/CHAT UI //
      menu = new JPanel(new GridLayout(3, 1));
      JPanel BoarderjpPanel = new JPanel(new GridLayout(0, 1));
      JPanel jpPanel = new JPanel();

      // MenuBar object and details
      JMenuBar menu = new JMenuBar();
      JMenu help = new JMenu("Help");
      menu.add(help);
      setJMenuBar(menu);

      // MenuItem for object and details
      JMenuItem about = new JMenuItem("About");
      JMenuItem exit = new JMenuItem("Exit");
      help.add(about);
      help.add(exit);

      // ActionListener method for exit only
      exit.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent ae) {
            // close();
            System.exit(0);
         }
      });

      about.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent ae) {
            // close();
            System.exit(0);
         }
      });

      // Create panels for the frame
      jp = new JPanel();
      ip = new JPanel();

      // Add information related to IP address and connection
      jlAddress = new JLabel("IP Address");
      jtfAddress = new JTextField(20);
      jbConnect = new JButton("Connect");

      ip.add(jlAddress);
      ip.add(jtfAddress);
      ip.add(jbConnect);
      jp.add(ip);

      // Address
      JPanel jpAddress = new JPanel();
      jpAddress.add(jlAddress);
      jpAddress.add(jtfAddress);
      jpAddress.add(jbConnect);

      // Adding Name info to GUI
      JPanel jpName = new JPanel();
      jpName.add(jlName);
      jpName.add(jtfName);

      // Join
      JPanel jpWhoIsIn = new JPanel();
      jpWhoIsIn.add(jbWhoIsIn);

      // Create a panel for the center of the frame
      jpCenter = new JPanel();

      // Set up TextArea
      area = new JTextArea(20, 30);

      // Scroll pane object details
      scroll = new JScrollPane(area);
      scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
      area.setEditable(false);
      area.setLineWrap(true);
      area.setWrapStyleWord(true);
      jpCenter.add(scroll); // Add scroll pane to the panel
      jp.add(jpCenter, BorderLayout.CENTER);

      // More panels
      jpSouthBorder = new JPanel(new BorderLayout());
      jpSNorth = new JPanel();

      // Set up Name with label and text field
      nameField = new JTextField(25);
      jpSNorth.add(nameField);
      jpSouthBorder.add(jpSNorth, BorderLayout.NORTH);

      // Another panel
      jpSSouth = new JPanel();

      JPanel jpJoin = new JPanel();
      jpJoin.add(jbJoin);

      // Start
      JPanel jpStart = new JPanel();
      jpStart.add(jbStart);

      BoarderjpPanel.add(jpAddress);
      BoarderjpPanel.add(jpName);
      BoarderjpPanel.add(jpJoin);
      BoarderjpPanel.add(jpStart);
      BoarderjpPanel.add(jbWhoIsIn);

      menu.add(BoarderjpPanel, BorderLayout.CENTER);

      // Panel button
      JPanel jpButton = new JPanel(new FlowLayout());

      jpAddress.add(jbConnect);
      jpButton.add(jbJoin);
      jpButton.add(jbStart);
      jpButton.add(jbWhoIsIn);

      menu.add(jpButton, BorderLayout.SOUTH);

      // Set up label, field and button objects for messages
      msgLabel = new JLabel("Message");
      msgBox = new JTextField(20);
      send = new JButton("Send");
      jbJoin = new JButton("Join");

      // Add the following objects to the panel
      jpSSouth.add(msgLabel);
      jpSSouth.add(msgBox);
      jpSSouth.add(send);
      jpSSouth.add(jbJoin);

      // Add the south part of the panel to the south border
      jpSouthBorder.add(jpSSouth, BorderLayout.SOUTH);
      jbJoin.addActionListener(Join);
      // Add the south border area to the full panel
      jp.add(jpSouthBorder, BorderLayout.SOUTH);
      add(jp);

      setLocationRelativeTo(null);
      // pack();
      setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

      JPanel jpNorth = new JPanel();
      JScrollPane scroll = new JScrollPane(jtaArea);
      scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
      jtaArea.setLineWrap(true);
      jtaArea.setEditable(false);
      jpNorth.add(scroll);
      menu.add(jpNorth, BorderLayout.NORTH);

      jbConnect.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent ae) {
            switch (ae.getActionCommand()) {
            case "Connect":
               doConnect();
               break;
            case "Disconnect":
               doDisconnect();
               break;
            }
         }
      });

      //jbJoin.addActionListener(new ActionListener() {
      //   public void actionPerformed(ActionEvent ae) {
      //      doJoin();
      //   }
      //});
      // jbStart.addActionListener();
      // jbWhoIsIn.addActionListener();

      setVisible(true);
      menu.setVisible(true);
      // menu.setVisible(false);
      jp.setVisible(true);
      this.getContentPane().add(menu, BorderLayout.CENTER);
      // END MENU/CHAT UI //

      // CREATE UI //

      // END CREATE UI //

      // LEADERBOARD UI //
      leaderboard = new JPanel(new GridLayout(3, 1));
      scores = new JPanel(new GridLayout(0, 1));
      JPanel blank = new JPanel();
      leaderboardButtons = new JPanel(new GridLayout(1, 2));

      jlLeaderboardArray = new ArrayList<JLabel>();

      JLabel test = new JLabel("Test");

      scores.add(test);

      // TO IMPLEMENT -- LISTING SCORES PROPERLY

      leaderboardReturn = new JButton("Play Again") {
         {
            addActionListener(new ActionListener() {
               @Override
               public void actionPerformed(ActionEvent e) {
                  leaderboard.setVisible(false);
                  GameState = GAME_STATES.MENU;
               }
            });
         }
      };

      leaderboardQuit = new JButton("Quit") {
         {
            addActionListener(new ActionListener() {
               @Override
               public void actionPerformed(ActionEvent e) {
                  System.exit(0);
               }
            });
         }
      };

      leaderboardButtons.add(leaderboardReturn);
      leaderboardButtons.add(leaderboardQuit);

      leaderboard.add(scores);
      leaderboard.add(blank);
      leaderboard.add(leaderboardButtons);

      leaderboard.setVisible(false);
      this.getContentPane().add(leaderboard, BorderLayout.CENTER);
      // END LEADERBOARD UI //

      // finish initializing the frame
      setResizable(true);
      setVisible(true);

      this.pack();
      setIgnoreRepaint(true);

      // add key listener
      addKeyListener(this);
      setFocusable(true);

      // create double buffer strategy
      createBufferStrategy(2);
      strategy = getBufferStrategy();

      // initialize game components
      ResetGame();

      GameState = GAME_STATES.MENU;
      setLocationRelativeTo(null);
   } // end initialization

   private void update() {
      fps = (int) (1f / dt);
      switch (GameState) {
      case MENU:
         break;
      case CREATE:
         break;
      case GAME:
         player.update(level);
         level.update(player);
         location.update(player);
         if (player.win) {
            GameState = GAME_STATES.LEADERBOARD;
            leaderboard.setVisible(true);
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
      case CREATE:
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
      Color playerC = new Color(255, 0, 0);
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
      switch (ke.getKeyCode()) {
      case KeyEvent.VK_LEFT:
         player.left = true;
         break;
      case KeyEvent.VK_RIGHT:
         player.right = true;
         break;
      }
   } // end keyPressed

   public void keyReleased(KeyEvent ke) {
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
   } // end keyReleased

   public void keyTyped(KeyEvent ke) {
   } // end keyTyped

   public static void main(String[] args) {
      Game game = new Game(1080, 720, 30);
      game.run();
   } // end main

   // Connect to Server
   private void doConnect() {
      try {
         cSocket = new Socket(jtfAddress.getText(), SERVER_PORT); // Create a socket for IP address and port number info
         getServerData = new ObjectInputStream(cSocket.getInputStream());
         sendServerData = new ObjectOutputStream(cSocket.getOutputStream());
         // scan = new Scanner(new InputStreamReader(cSocket.getInputStream())); //
         // Create a scanner object
         // pwt = new PrintWriter(new OutputStreamWriter(cSocket.getOutputStream())); //
         // Create a print writer object

      }

      catch (IOException ioe) {
         jtaArea.append("IO Exception: " + ioe + "\n");
         return;
      }

      jtaArea.append("Connected!\n"); // Message when a user is connected to other users
      jbConnect.setText("Disconnect"); // When a user disconnect the session
   } // end doConnect

   /**
    * Disconnects from the server
    */
   private void doDisconnect() {

      // try and catch
      try {
         cSocket.close();
         scan.close();
         pwt.close();
         jtaArea.append("Disconnected!\n");
      } catch (IOException ioe) {
         jtaArea.append("IO Exception: " + ioe + "\n");
         return;
      }
      jbConnect.setText("Connect"); // If the user wish to re-connect the session
   } // end doDisconnect

   // Send message to Server
   //private void doJoin() {
   //   pwt.println(jtfName.getText() + " is here ");
   //   pwt.flush();
   //} // end doJoin

   // Receive message from server
   private void receive() {
      String reply = "\n" + scan.nextLine();
      jtaArea.append(reply);
   } // end receive

   class JoinGame implements ActionListener {

      // ActionPerformed method
      public void actionPerformed(ActionEvent ae) {
         if (ae.getActionCommand().equals("Join")) // Check If the 'Disconnect' button is pressed
         {
            menu.setVisible(false);
            GameState = GAME_STATES.GAME;
         }

      } // End of actionPerformed method

   }

} // end class Game