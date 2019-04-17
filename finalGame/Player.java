package finalGame;
import java.awt.*;
import java.util.*;

public class Player extends GameObject {

    // location player appears on screen
    private float spawnX;
    private float spawnY;
    
    private boolean jumping;
    
    private Color c;
    int h;
    int w;
    
    private Rectangle player;
    private boolean colliding = false;
    
    public boolean jump = false; // jumping
    public boolean left = false; // moving left
    public boolean right = false; // moving right

    public Player(int h, int w, Color c) {
        this.h = h;
        this.w = w;
        this.c = c;
        spawn();
    } // end constructor
    
    private void spawn() {
        this.px = spawnX;
        this.py = spawnY;
        // this.px = 50;
        // this.py = 620;
    } 
    
    public void update(GameObject o) {
        Terrain check = null;
        
        if (o instanceof Terrain) {
            check = (Terrain) o;
            colliding = check.checkCollision(player);
        }
        
        Rectangle collidedWith = null;
        if (colliding || check != null) {
            collidedWith = check.playerStop(check.getCollision());
        }
        if (colliding) {
            playerStop(collidedWith);
        } else {
            if (jump) {
                vy = -10;
            } else {
                vy = 1;
            }
            if (left) {
                vx = -1;
            }
            if (right) {
                vx = 1;
            }
        }
        
        if (jump) {
            this.py += vy;
            jump = false;
        } else {
            this.py += vy;
        }
            
        if (right) {
            this.px += vx;
        }
        
        if (left) {
            this.px += vx;
        }
    } // end update
    
    public void draw(Graphics2D g) {
        // g.translate(this.getX(), this.getY());
        g.setColor(c);
        g.fillRect(this.getX(), this.getY(), h, w);
        player = new Rectangle(this.getX(), this.getY(), h, w);
    } // end draw
    
    public void playerStop(Rectangle o) {
        
        boolean downC = false;
        boolean upC = false;
        boolean leftC = false;
        boolean rightC = false;
        
        // Check if collision is on player's bottom
        if (player.getY() + player.getHeight() > o.getHeight()) {
            vy = 0;
            downC = true;
            if (jump) {
                vy = -10;
                downC = false;
            }
        }
        
        // Check if collision is on player's top
        if (player.getY() > o.getHeight() + o.getY()) {
            vy = 0;
            upC = true;
        }
        
        // Check if collision is on the player's left
        if (player.getX() > o.getX() + o.getWidth()) {
            vx = 0;
            leftC = true;
            if (right) {
                vx = 1;
            }    
        }
        
        // Check if collision is on the player's right
        if (player.getX() + player.getWidth() < o.getX()) {
            vx = 0;
            rightC = true;
            if (left) {
                vx = -1;
            }
        }
        
        // If no collision, do regular movement
        if (!(rightC && leftC)) {
            if (right) {
                vx = 1;
            } else if (left) {
                vx = -1;
            } else {
                vx = 0;    
            }
        }
    } // end playerStop

} // end Player