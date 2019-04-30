package finalGame;
import java.awt.*;

public class LocationView extends GameObject {

    Color playerC;
    int playerX;
    double relativeX;
    final double COURSELENGTH = 950;

    public LocationView(Color c) {
        playerC = c;
    }

    @Override
    public void update(GameObject o) {
        Player p;
        if (o instanceof Player) {
            p = (Player) o;
            playerX = p.getX();
            
            relativeX = (playerX / COURSELENGTH) * 350;
        }
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.drawRect(350, 50, 350, 50);
        
        int finalX;
        
        if (relativeX < 0) {
            finalX = 350;
        } else if (relativeX > 350) {
            finalX = 700;
        } else {
            finalX = (int) relativeX + 350;
        }
        
        g.setColor(playerC);
        g.drawLine(finalX, 51, finalX, 99);
    }
} // end LocationView 