import java.awt.*;
import java.util.*;

public class Terrain extends GameObject {

    private ArrayList<TerrainComponents> myGround;
    private int activeCollision;
    
    public Terrain() {
        myGround = new ArrayList<TerrainComponents>();
        myGround.add(new TerrainComponents(0, 670, 1080, 50));
        myGround.add(new TerrainComponents(360, 0, 50, 400));
        myGround.add(new TerrainComponents(500, 400, 50, 50));
    } //
    
    public void update(GameObject o) {
        for (TerrainComponents ground : myGround) {
            ground.update(o);
        }
    }
    
    public void draw(Graphics2D g) {
        for (TerrainComponents ground : myGround) {
            ground.draw(g);
        }
    }
    
    public boolean checkCollision(Rectangle o) {
        for (TerrainComponents ground : myGround) {
            if (ground.checkCollision(o)) {
                activeCollision = myGround.indexOf(ground);
                return true;
            }
        }
        return false;
    }
    
    public Rectangle playerStop(int activeCollision) {
        TerrainComponents colliding = myGround.get(activeCollision);
        Rectangle o = new Rectangle(colliding.getX(), colliding.getY(), colliding.getW(), colliding.getH());
        return colliding.playerStop(o);
    } 
    
    public int getCollision() {
        return activeCollision;
    }
    
    class TerrainComponents extends GameObject {
    
        private int w;
        private int h;
        private Rectangle obstacle;
    
        public TerrainComponents(int xPos, int yPos, int _w, int _h) {
            this.px = xPos;
            this.py = yPos;
            w = _w;
            h = _h;
        }
        
        public void update(GameObject o) {
        }
        
        public void draw(Graphics2D g) {
            g.setColor(Color.BLACK);
            g.fillRect(getX(), getY(), w, h);
            obstacle = new Rectangle(getX(), getY(), w, h);
        }
        
        public int getW() {
            return w;
        } 
        
        public int getH() {
            return h;
        }
        
        public boolean checkCollision(Rectangle o) {
            if (obstacle.intersects(o)) {
                return true;
            } else {
                return false;
            }
        }
        
        public Rectangle playerStop(Rectangle o) {
            return o;
        } 
        
    }

} // end Terrain