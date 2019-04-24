import java.awt.*;

public abstract class GameObject {

    float px; // float position x
    float py; // float position y
    
    float vx; // velocity x
    float vy; // velocity y
    
    public abstract void update(GameObject o);
    public abstract void draw(Graphics2D g);
    
    public boolean isColliding(int xPos, int yPos, int w, int h) {
        return false;
    }
    
    public int getX() {
        return (int) px;
    }
    
    public int getY() {
        return (int) py;
    } 
    
} // end GameObject