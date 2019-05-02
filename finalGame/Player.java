package finalGame;
import java.awt.*;
import java.util.*;

public class Player extends GameObject {

    // location player appears on screen
    private float spawnX = 100;
    private float spawnY = 570;
    
    // x coordinate player has to reach to win
    private float winPos = 150;
    
    // player info
    private Color c;
    int h;
    int w;
    
    // max left and right speed
    private final int MAXLEFT = -5;
    private final int MAXRIGHT = 5;
    
    // for collision
    private Rectangle player;
    private Rectangle intersection;
    private boolean grounded = false;
    
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
        
        if (jump) {
            vy = -150;
        } else if (!grounded) {
            vy = 1;
        } else {
            vy = 0;
        }
            
        if (left) {
            vx = -1;
        }
            
        if (right) {
            vx = 1;
        }
        
        
        if (jump) {
            this.py += vy;
            // Check to make sure you don't go off the screen
            if (this.py < 30) {
                this.py = 30;
            }
        } else {
            this.py += vy;
        }
            
        if (right) {
            this.px += vx;
            // Check to make you don't go off the screen, though it shouldn't be possible
            if (this.px > 4000) {
                this.px = 4000;
            }
        }
        
        if (left) {
            this.px += vx;
            // Check to make sure you don't go off the screen
            if (this.px < 8) {
                this.px = 8;    
            }
        }
        
        Terrain t = null;
        
        if (o instanceof Terrain) {
            t = (Terrain) o;
            ground = t.getTerrain();
        }
        
        for (Rectangle r : ground) {
            if (r.intersects(player)) {
                intersection = r.intersection(player);
                
                if (intersection.getHeight() == 1 && intersection.getWidth() == 1) {
                    grounded = false;
                    return;
                }
                
                if (intersection.getHeight() == 1) {
                    setY(intersection.getY());
                    
                    if (jump && !grounded) {
                        setY(player.getY() + intersection.getHeight());
                        System.out.println("You jumped and hit the top");
                    } else {
                        setY(player.getY() - intersection.getHeight());
                        grounded = true;
                        if (jump) {
                            setY(player.getY() - 150);
                            grounded = false;
                            jump = false;
                        }
                    }
                }
                
                if (intersection.getWidth() == 1) {
                    // setX(intersection.getX());
                    
                    if (left) {
                        setX(player.getX() + intersection.getWidth());
                        this.px -= vx;
                    } else if (right) {
                        setX(player.getX() - intersection.getWidth());
                        this.px += vx;
                    }
                }
                
            } else if (grounded) {
                setY(player.getY() + 1);
                if (r.intersects(player)) {
                    grounded = false;
                } else {
                    setY(player.getY());
                }
            }
        }
        
        if (!left && !right) {
            vx = 0;
        }
        
        jump = false;
        
        // t.offsetCamera((int) vx * 4);
        
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