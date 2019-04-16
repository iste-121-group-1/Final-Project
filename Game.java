import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

/**
  *
  * Basic game demo. Code based off of and build from a game engine
  * by Davis Smith, another RIT student. They are reachable at 
  * dgs4349@g.rit.edu.
  *
  */

public class Game extends JFrame implements KeyListener {

    // window vars
    private final int MAX_FPS;
    private final int WIDTH;
    private final int HEIGHT;
    
    // game states
    public enum GAME_STATES {
        MENU,
        CREATE,
        GAME,
        CHAT,
        LEADERBOARD,
        QUIT
    } // end game_states enumeration
    public GAME_STATES GameState;
    
    // player time
    public static long TIME;
    
    // pause
    public boolean isPaused = false;
    
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
        
        GameState = GAME_STATES.GAME;
        setLocationRelativeTo(null);
    } // end initialization
    
    private void update() {
        fps = (int) (1f / dt);
        switch(GameState) {
        case MENU:
            break;
        case GAME:
            player.update(level);
            level.update(player);
        case LEADERBOARD:
            break;
        } // end switch
    } // end update
    
    public void draw() {
        switch(GameState) {
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
            // location.draw(g);
            
            // release resourcecs, show the buffer
            g.dispose();
            strategy.show();
            break;
        case LEADERBOARD:
            break;
        } // end switch
    } // end draw
    
    public void ResetGame() {
        Color playerC = new Color(255, 0, 0);
        player = new Player(50, 50, playerC);
        level = new Terrain();
        location = new LocationView();
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
            update();
            draw();
            
            // dynamic thread sleep, only sleep the time we need to cap framerate
            rest = (1000 / MAX_FPS) - (System.currentTimeMillis() - startFrame);
            if (rest > 0) {
                try { Thread.sleep(rest); }
                catch (InterruptedException ie) { ie.printStackTrace(); } // end try-catch
            } // end if
        } // end while
    } // end run

    public void keyPressed(KeyEvent ke) {
        switch(ke.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                player.left = true;
                break;
            case KeyEvent.VK_RIGHT:
                player.right = true;
                break;
        }
    } // end keyPressed
    
    public void keyReleased(KeyEvent ke) {
        switch(ke.getKeyCode()) {
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
    
    public void keyTyped(KeyEvent ke) {} // end keyTyped
    
    public static void main(String[] args) {
        Game game = new Game(1080, 720, 60);
        game.run();
    } // end main

} // end class Game