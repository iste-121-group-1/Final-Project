package finalGame;
import java.awt.*;

public class LocationView extends GameObject {

    Color playerC;
    int playerX;
    double relativeX;
    final double COURSELENGTH = 4000;

    public LocationView(Color c) {
        playerC = c;
    }

    @Override
    public void update(GameObject o) {
        Player p;
        if (o instanceof Player) {
            p = (Player) o;
            playerX = p.getX();
            
            relativeX = (playerX / COURSELENGTH) * 100;
        }
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.drawRect(350, 50, 350, 50);
        
        g.setColor(playerC);
        g.drawLine((int) relativeX + 350, 50, (int) relativeX + 350, 100);
    }
} // end LocationView 