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


import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

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
   
   JTextArea area;
   //JTextField msgBox, nameField;
   //JButton send;
   JMenu file;
   JMenuItem exit;
   JPanel jp, ip, jpCenter, jpSouthBorder, jpSNorth, jpSSouth;
   //JLabel msgLabel;
   JScrollPane scroll;
   
   // Menu UI elements
   private JPanel menu;
   
   //Address
   private JLabel jlAddress = new JLabel("Server IP");
   private JTextField jtfAddress = new JTextField(20);
   private JButton jbConnect = new JButton("Connect");

   // Name Info
   JLabel jlName = new JLabel("Name:");
   JTextField jtfName = new JTextField(20);
   JButton jbName = new JButton("Login");
   
   //Message
   JLabel msgLabel = new JLabel("Message");
   JTextField msgBox = new JTextField(20);
   JButton send = new JButton("Send");

   //private JButton jbJoin;// = new JButton("Join Game");
   private JButton jbWhoIsIn = new JButton("Who is in");
   private JButton jbColor = new JButton("Choose Color");
   private JButton jbJoin = new JButton("Join");
   

   // Text Area
   private JLabel jlaArea;
   private JTextArea jtaArea = new JTextArea(20, 30);

   // Attributes
   public static final int SERVER_PORT = 32001;
   private Socket cSocket = null;
   public String name = "";
   private SendMessage sendMsg;
   private WHOISIN whoIsIn;
   private Connection connect;
   private Login login;
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

   public Game(int width, int height, int fps) {
   
      super("Race Demo");
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
   
      //MENU/CHAT UI //
      //menu = new JPanel();
      JPanel BoarderjpPanel = new JPanel(new GridLayout(0, 1));
      JPanel jpPanel = new JPanel();
   
      // MenuBar object and details
      JMenuBar menu = new JMenuBar();
      JMenu help = new JMenu("Help");
      menu.add(help);
      setJMenuBar(menu);
      JMenuItem about = new JMenuItem("About");
      JMenuItem exit = new JMenuItem("Exit");
      help.add(about);
      help.add(exit);
      
      
   
      //MenuItem for object and details
      //JMenuItem about = new JMenuItem("About");
      //JMenuItem exit = new JMenuItem("Exit");
      //help.add(about);
      //help.add(exit);
   
      // ActionListener method for exit only
      exit.addActionListener(
         new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
            // close();
               System.exit(0);
            }
         });
   
      about.addActionListener(
         new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
            // close();
               JOptionPane.showMessageDialog(null, "You can write something :)");
            }
         });
      
      
      
      // Create panels for the frame
      jp = new JPanel(new GridLayout(0,1));
   
      // Add information related to IP address and connection
      // jlAddress = new JLabel("IP Address");
   //       jtfAddress = new JTextField(20);
   //       jbConnect = new JButton("Connect");
      
      // Set up label, field and button objects for messages
      
      
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
   
      // Add the following objects to the panel
      
      
      //Message
      JPanel jpMessage = new JPanel();
      jpMessage.add(msgLabel);
      jpMessage.add(msgBox);
      jpMessage.add(send);
      
      JPanel jpSSouth = new JPanel(new GridLayout(0,1));
      jpSSouth.add(jpAddress);
      jpSSouth.add(jpName);
      jpSSouth.add(jpMessage);
     
   
      // Add the south part of the panel to the south border
      add(jpSSouth, BorderLayout.SOUTH);
      
      // Join
      JPanel jpWhoIsIn = new JPanel();
      jpWhoIsIn.add(jbWhoIsIn);
      
      
   
      // Create a panel for the center of the frame
      jpCenter = new JPanel();
      // Set up TextArea
      area = new JTextArea(10, 20);
      // Scroll pane object details
      scroll = new JScrollPane(area);
      scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
      area.setEditable(false);
      area.setLineWrap(true);
      area.setWrapStyleWord(true);
      jpCenter.add(scroll); // Add scroll pane to the panel
      jp.add(jpCenter, BorderLayout.CENTER);
   
      // More panels
      jpSouthBorder = new JPanel(new BorderLayout(0,1));
      jpSNorth = new JPanel();
   
   
      // Another panel
      jpSSouth = new JPanel();
   
      // Start
      JPanel jpStart = new JPanel();
      jpStart.add(jbColor);
   
      //BoarderjpPanel.add(jpAddress);
      //BoarderjpPanel.add(jpName);
      //BoarderjpPanel.add(jpStart);
      //BoarderjpPanel.add(jbWhoIsIn);
   
      //menu.add(BoarderjpPanel, BorderLayout.CENTER);
   
      // Panel button
      JPanel jpButton = new JPanel(new FlowLayout());
   
      //jpAddress.add(jbConnect);
      //jpButton.add(jbJoin);
      jpButton.add(jbColor);
      jpButton.add(jbWhoIsIn);
      jpButton.add(jbJoin);
      add(jpButton, BorderLayout.NORTH);
      
      //An object 
      sendMsg = new SendMessage();
      connect = new Connection();
      whoIsIn = new WHOISIN();
      login = new Login();
      
      
      //Add it to ActionListener
      send.addActionListener(sendMsg);
      jbConnect.addActionListener(connect);
      jbName.addActionListener(login);
      jbWhoIsIn.addActionListener(whoIsIn);
      jbName.addKeyListener(this);
      
      
      jbJoin.addActionListener(
         ae -> {
            ResetGame();
            menu.setVisible(false);
            GameState = GAME_STATES.GAME;
            jpButton.setVisible(false);
            jp.setVisible(false);
            
         });
      // Add the south border area to the full panel
      //add(jpSouthBorder, BorderLayout.SOUTH);
      jp.add(menu);
   
      //setLocationRelativeTo(null);
      // pack();
      setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
   
      JPanel jpNorth = new JPanel();
      JScrollPane scroll = new JScrollPane(jtaArea);
      scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
      jtaArea.setLineWrap(true);
      jtaArea.setEditable(false);
      jpNorth.add(scroll);
      add(jpNorth, BorderLayout.CENTER);
   
   
      setVisible(true);
      //menu.setVisible(true);
      //jp.setVisible(true);
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
   
      leaderboardReturn = 
         new JButton("Play Again") {
            {
               addActionListener(
                  new ActionListener() {
                     @Override
                     public void actionPerformed(ActionEvent e) {
                        leaderboard.setVisible(false);
                        GameState = GAME_STATES.MENU;
                     }
                  });
            }
         };
   
      leaderboardQuit = 
         new JButton("Quit") {
            {
               addActionListener(
                  new ActionListener() {
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


//Connection Constructor with ActionListener
   class Connection implements ActionListener
   {
     
      //ActionPerformed method
      public void actionPerformed(ActionEvent ae)
      {
         if(ae.getActionCommand().equals("Connect"))//If the 'Connect' button is pressed 
         {
            try
            {
               cSocket = new Socket(jtfAddress.getText(), 32001);//Create a client socket with IP address and port number
               br = new BufferedReader(new InputStreamReader(cSocket.getInputStream()));
               pw = new PrintWriter(new OutputStreamWriter(cSocket.getOutputStream()));
               System.out.println("Connecting to Server");
            
            //Start threading for message
               Thread multiThread = new Thread(new SendReceiveMsg());
               multiThread.start();
            }
            
            
            //Exceptions
            catch(IOException ioe)
            {
               System.out.println("Error connection");
            }
            
            catch(Exception e)
            {
               System.out.println("Error connection");
            }
         
            jbConnect.setText("Disconnect");
         }
         
         else if(ae.getActionCommand().equals("Disconnect")) // Check If the 'Disconnect' button is pressed 
         {
            try {
               cSocket.close();
               pw.close();
               area.append("Disconnected!\n");
            }
            catch(IOException ioe) {
               area.append("IO Exception: " + ioe + "\n");
               return;
            }
            jbConnect.setText("Connect");
         }
      
      } //End actionPerformed method
   
   } //End Connection constructor

   
    class Login implements ActionListener
    {
      public void actionPerformed(ActionEvent ae){
         if(ae.getActionCommand().equals("Login"))
            {
            //ActionPerformed method  
            
               area.append("Client name set to : " + jtfName.getText() + "\n");
               jbName.setText("Logout");
            
             //End of actionPerformed method
            }
            
            else if(ae.getActionCommand().equals("Logout"))
            {
            //ActionPerformed method  
            
               area.append("\n" + jtfName.getText() + "logged out");
               jbName.setText("Login");
            
             //End of actionPerformed method
            }
   }
   }
   
   
   //SendMessage Constructor with ActionListener
   class SendMessage implements ActionListener
   {
     
      //ActionPerformed method  
      public void actionPerformed(ActionEvent ae)
      {
         try 
         {
            pw.println(jtfName.getText() + ": " + msgBox.getText());
            pw.flush();
            msgBox.setText("");
         }
         
         catch(NullPointerException npe)
         {
            System.out.println("Not connected to server"); //Printing message says that client is not conencted to server yet
         }
      
      } //End of actionPerformed method
     
   } //End of sendMessage constructor
   
   
   //Who is in
   class WHOISIN implements ActionListener
   {
     
      //ActionPerformed method  
      public void actionPerformed(ActionEvent ae)
      {            
         area.append(jtfName.getText() + "joined game\n");
      
      } //End of actionPerformed method
     
   } //End of WhoIsIn constructor
   
   
   //SendReceiveMsg constructor for Runnable Thread
   class SendReceiveMsg implements Runnable
   {   
      //Run method 
      public void run()
      {      
         String msg;
         
         try
         {
            while((msg = br.readLine()) != null)
            {
               area.append(msg + "\n");
            }
         }
         catch(IOException ioe)
         {
            area.append("Server disconnected\n");
         }
         catch(Exception e)
         {
            System.out.println("Not connected to Server\n");
         }
       
      } //End of run method 
     
   } //End of SendReceiveMsg constructor
   
   
   public void close() 
   {
      try
      {
         cSocket.close(); //Close client socket 
      }
      
      
      catch(IOException ioe)
      {
         area.append("Unable to disconnect\n");        
      }
      catch(Exception e)
      {
         area.append("Unable to disconnect\n");        
      }
      
     
   } // End of close method
   
   // class ButtonListener implements ActionListener {
//     public void actionPerformed(ActionEvent e) {
//       Color color = jbColor.showDialog(null, "Choose a Color", jbColor.getForeground());
//       if (color != null)
//         jbColor.setForeground(c);
//     }
//   }
   
} // end class Game

