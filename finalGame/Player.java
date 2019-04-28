package finalGame;
import java.awt.*;
import java.util.*;

public class Player extends GameObject {

    // location player appears on screen
    private float spawnX = 420;
    //private float spawnY = 620;
    private float spawnY = 100;
    
    private float winPos = 3950;
    
    private boolean jumping;
    
    private Color c;
    int h;
    int w;
    
    private Rectangle player;
    private boolean colliding = false;
    
    public boolean jump = false; // jumping
    public boolean left = false; // moving left
    public boolean right = false; // moving right
    public boolean win = false; // winner

    public Player(int h, int w, Color c) {
        this.h = h;
        this.w = w;
        this.c = c;
        spawn();
    } // end constructor
    
    private void spawn() {
        this.px = spawnX;
        this.py = spawnY;
    } 
    
    public void update(GameObject o) {
        ArrayList<Rectangle> ground = null;
        
        if (o instanceof Terrain) {
            Terrain t = (Terrain) o;
            ground = t.getTerrain();
        }
        
        for (Rectangle r : ground) {
            if (r.intersects(player)) {
                Rectangle intersection = r.intersection(player);
                System.out.println(intersection);
                setX(intersection.getX());
                setY(intersection.getY());
                
                if (jump) {
                    setY(player.getY() + intersection.getHeight());
                    System.out.println("You jumped and hit the top");
                } else {
                    System.out.println(player.getY());
                    System.out.println(intersection.getY());
                    setY(player.getY() - intersection.getHeight());
                    System.out.println("You're touching the bottom");
                }
            
                if (left && intersection.getWidth() > 1) {
                    setX(player.getX() + intersection.getWidth());
                    System.out.println("You're hitting on your left");
                }
            
                if (right && intersection.getWidth() > 1) {
                    setX(player.getX() - intersection.getWidth());
                    System.out.println("You're hitting on your right");
                }
            }
        }
        
        
        if (jump) {
            vy = -150;
        } else {
            vy = 1;
        }
            
        if (left) {
            vx = -1;
        }
            
        if (right) {
            vx = 1;    
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
        
        win = checkWin();
    } // end update
    
    public void draw(Graphics2D g) {
        // g.translate(this.getX(), this.getY());
        g.setColor(c);
        g.fillRect(this.getX(), this.getY(), h, w);
        player = new Rectangle(this.getX(), this.getY(), h, w);
    } // end draw
    
    public void setX(double x) {
        px = (float) x;
    } // end setX
    
    public void setY(double y) {
        py = (float) y;
    } // end setY
    
    public boolean checkWin() {
        if (getX() >= winPos) {
            return true;
        } else {
            return false;
        }
    } // end checkWin

} // end Player