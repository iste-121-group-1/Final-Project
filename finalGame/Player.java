package finalGame;
import java.awt.*;
import java.util.*;

public class Player extends GameObject {

    // location player appears on screen
    private float spawnX = 100;
    private float spawnY = 570;
    
    // x coordinate player has to reach to win
    private float winPos = 3800;
    
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
    
    private boolean cameraMove = false;
    private float distanceTravelled = 0;
    private boolean leftC;
    private boolean rightC;

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
          
            if (left) {
                vx = -2;
            }
            
            if (right) {
                vx = 2;
            }
        } else if (!grounded) {
            vy = 1;
          
            if (left) {
                vx = -2;
            }
            
            if (right) {
                vx = 2;
            }
        } else {
            if (left) {
                vx = -4;
            }
            
            if (right) {
                vx = 4;
            }
        }
        
        
        if (jump) {
            this.py += vy;
            // Check to make sure you don't go off the screen from the top
            if (this.py < 30) {
                this.py = 30;
            }
        } else {
            this.py += vy;
            // Check to make sure you don't go off the screen from the bottom
            if (this.py > 669) {
                this.py = 669;
            }
        }
            
        if (right) {
            this.px += vx;
            distanceTravelled += vx;
            // Check to make you don't go off the screen from the right
            if (this.px > 1027) {
                this.px = 1027;
            }
        }
        
        if (left) {
            this.px += vx;
            distanceTravelled += vx;
            // Check to make sure you don't go off the screen from the left
            if (this.px < 3) {
                this.px = 3;    
            }
        }
        
        Terrain t = null;
        
        if (o instanceof Terrain) {
            t = (Terrain) o;
            cameraMove = t.offsetCamera((int) vx, (int) getDistance());
            ground = t.getTerrain();
        }
        
        if (cameraMove && distanceTravelled > 0) {
            this.px -= vx;
        }
        
        for (Rectangle r : ground) {
            if (r.intersects(player)) {
                intersection = r.intersection(player);
                
                if (intersection.getHeight() == 1 && intersection.getWidth() - Math.abs(vx) <= 0) {
                    grounded = false;
                    
                } else if (intersection.getHeight() == 1) {
                    if (jump && !grounded) {
                        setY(intersection.getHeight() + intersection.getY());
                    } else {
                        setY(intersection.getHeight() - h);
                        grounded = true;
                        if (jump) {
                            setY(player.getY() - 150);
                            grounded = false;
                            jump = false;
                        }
                    }
                } else {
                    if (left) {
                        setX(intersection.getWidth() + intersection.getX());
                        rightC = true;
                    }
                    
                    if (right) {
                        setX(intersection.getX() - w);
                        leftC = true;
                    }
                }
                
            } else if (grounded) {
                setY(player.getY() + vy);
                if (r.intersects(player)) {
                    grounded = false;
                } else {
                    setY(player.getY());
                }
            }
        }
        
        // Make sure camera doesn't move when player isn't moving
        if (!left && !right) {
            vx = 0;
        }
        
        jump = false;
        
        // Makes jittering happen on outside, not inside
        if (leftC && !cameraMove) {
            leftC = false;
        } else if (leftC && cameraMove) {
            distanceTravelled -= vx;
            leftC = false;
        }
        if (rightC && !cameraMove) {
            rightC = false;
        } else if (rightC && cameraMove) {
            distanceTravelled -= vx;
            rightC = false;
        }
        
        // Check to see if the player has won
        win = checkWin();
    } // end update
    
    public void draw(Graphics2D g) {
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
    
    public float getDistance() {
        return distanceTravelled;
    }
    
    public boolean checkWin() {
        if (getDistance() >= winPos) {
            return true;
        } else {
            return false;
        }
    } // end checkWin

} // end Player