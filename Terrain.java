import java.awt.*;
import java.util.*;

public class Terrain extends GameObject {

    private ArrayList<TerrainComponents> myGround;
    
    public Terrain() {
        myGround = new ArrayList<TerrainComponents>();
        myGround.add(new TerrainComponents(0, 670, 1080, 50));
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
    
    class TerrainComponents extends GameObject {
    
        private int w;
        private int h;
    
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
        }
        
    }

} // end Terrain