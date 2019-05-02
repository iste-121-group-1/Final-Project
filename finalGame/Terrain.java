package finalGame;
import java.awt.*;
import java.util.*;

public class Terrain extends GameObject {

    private ArrayList<TerrainComponents> myGround;
    private ArrayList<Rectangle> myRects;
    private int finishLine;
    private boolean offsetCamera = true;
    
    public Terrain() {
        myRects = new ArrayList<Rectangle>();
        myGround = new ArrayList<TerrainComponents>();
        myGround.add(new TerrainComponents(0, 620, 4050, 100));
        myGround.add(new TerrainComponents(500, 420, 50, 50));
        myGround.add(new TerrainComponents(750, 420, 250, 50));
        myGround.add(new TerrainComponents(850, 270, 50, 50));
        myGround.add(new TerrainComponents(1100, 520, 100, 100));
        myGround.add(new TerrainComponents(1350, 470, 100, 150));
        myGround.add(new TerrainComponents(1600, 420, 100, 200));
        myGround.add(new TerrainComponents(2400, 420, 200, 50));
        myGround.add(new TerrainComponents(3000, 520, 100, 100));
        myGround.add(new TerrainComponents(3350, 570, 450, 50));
        myGround.add(new TerrainComponents(3400, 520, 400, 50));
        myGround.add(new TerrainComponents(3450, 470, 350, 50));
        myGround.add(new TerrainComponents(3500, 420, 300, 50));
        myGround.add(new TerrainComponents(3550, 370, 250, 50));
        myGround.add(new TerrainComponents(3600, 320, 200, 50));
        myGround.add(new TerrainComponents(3650, 270, 150, 50));
        myGround.add(new TerrainComponents(3700, 220, 100, 50));
        myGround.add(new TerrainComponents(3750, 170, 50, 50));
    } // end Terrain constructor
    
    public void update(GameObject o) {
        for (TerrainComponents ground : myGround) {
            ground.update(o);
        }
    }
    
    public void draw(Graphics2D g) {
        for (TerrainComponents ground : myGround) {
            ground.draw(g);
        }
        if (!offsetCamera) {
            g.setColor(Color.BLUE);
            g.drawLine(1030, 0, 1030, 619);
        }
    }
    
    public ArrayList<Rectangle> getTerrain() {
        return myRects;
    } // end getTerrain
    
    public boolean offsetCamera(int offsetPos, int disTravelled) {
        if (disTravelled > 0 && offsetCamera) {
            for (TerrainComponents comp : myGround) {
                comp.setX(comp.getX() - offsetPos);
            }
            for (Rectangle rect : myRects) {
                rect.setLocation((int) rect.getX() - offsetPos, (int) rect.getY());
            }
        
            if (disTravelled >= 2920) {
                offsetCamera = false;
            }
        }
        return offsetCamera;
    } // end offsetCamera()
    
    class TerrainComponents extends GameObject {
    
        private int w;
        private int h;
    
        public TerrainComponents(int xPos, int yPos, int _w, int _h) {
            this.px = xPos;
            this.py = yPos;
            w = _w;
            h = _h;
            myRects.add(new Rectangle(xPos, yPos, _w, _h));
        }
        
        public void update(GameObject o) {
        }
        
        public void setX(int newX) {
            this.px = newX;
        }
        
        public void draw(Graphics2D g) {
            g.setColor(Color.BLACK);
            g.fillRect(getX(), getY(), w, h);
        }
        
    }

} // end Terrain