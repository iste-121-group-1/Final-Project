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
        
        boolean bottomC = false;
        boolean topC = false;
        boolean leftC = false;
        boolean rightC = false;
        
        // Check if collision is on player's bottom
        if (player.getY() + player.getHeight() > o.getHeight()) {
            setY(o.getHeight() - player.getHeight());
            System.out.println(getX());
            System.out.println(getY());
            player.setLocation(getX(), getY());
            bottomC = true;
            if (jump) {
                vy = -10;
                bottomC = false;
            }
        }
        
        /*// Check if collision is on player's top
        if (player.getY() > o.getHeight() + o.getY()) {
            vy = 0;
            topC = true;
        }*/
        
        // Check if collision is on the player's left
        if (player.getX() > o.getX() + o.getWidth()) {
            // setX(o.getWidth());
            // player.setLocation(getX(), getY());
            // System.out.println("Left collision");
            // leftC = true;
            // if (right) {
                // vx = 1;
            // }
            System.out.println("Collision on left");
        }
        
        // Check if collision is on the player's right
        if (player.getX() + player.getWidth() < o.getX()) {
            
            rightC = true;
            if (left) {
                vx = -1;
            }
        }
        
        if (bottomC && (!leftC || !rightC)) {
            System.out.println("Bottom Collision only");
            if (left) {
                vx = -1;
            }
            if (right) {
                vx = 1;
            }
        } else if (bottomC && leftC) {
            System.out.println("Bottom and Left Collision");
            if (left) {
                vx = 0;
            }
            if (right) {
                vx = 1;
            }
        } else if (bottomC && rightC) {
            System.out.println("Bottom and Right Collision");
            if (left) {
                vx = -1;
            }
            if (right) {
                vx = 0;
            }
        } // end checks for bottom + other collisions
        
        // If collision, check for what ways can be moved 
        if (rightC || leftC) {
            if (jump && !topC) {
                vy = -10;
            } else if (!bottomC) {
                vy = 1;
            } else if (bottomC) {
                vy = 0;
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
    
    public void setX(double x) {
        vx = (float) x;
    } // end setX
    
    public void setY(double y) {
        vy = (float) y;
    } // end setY

} // end Player