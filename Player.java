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
    
    private char collisionType;
    
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
        collisionType = isColliding(getX(), getY(), h, w, o);
        if (jump) {
            vy = -10;
            this.py += vy;
            jump = false;
        } else if ( !(jump) && !(collisionType == 'b') ) {
            vy = 1;
            this.py += vy;
        }
        
        if (right && !(collisionType == 'r')) {
            vx = 1;
            this.px += vx;
        }
        
        if (left && !(collisionType == 'l')) {
            vx = -1;
            this.px += vx;
        }
        
        if (collisionType != ' ') {
            collisionResolution();
        }
    } // end update
    
    public void draw(Graphics2D g) {
        // g.translate(this.getX(), this.getY());
        g.setColor(c);
        g.fillRect(this.getX(), this.getY(), h, w);
    } // end draw
    
    public char isColliding(int xPos, int yPos, int pHeight, int pWidth, GameObject o) {
       // if () { // bottom collision
         //   return 'b';
        //} else if () { // left collision
          //  return 'l';
        //} else if () { // right collision
          //  return 'r';
        //} else if () { // top collision
          //  return 't';
        //} else { // no collision
            return ' ';
        //}
    } // end isColliding
    
    public void collisionResolution() {
    } // end collisionResolution

} // end Player