package finalGame;

import java.awt.*;

public class OtherPlayer extends GameObject {

    // location player appears on screen
    private float spawnX;
    private float spawnY;

    private float winPos = 1000;

    private Color c;
    // changed to constants to simplify constructor
    int h = 50;
    int w = 50;

    public boolean win = false; // winner

    private GameData data;

    /**
     * Constructor for an "OtherPlayer", used to display other players in the same game that are not controled by the client
     * @param data
     */
    public OtherPlayer(GameData _data) {
        data = _data;
        this.c = data.getColor();
        spawn();
    } // end constructor

    /**
     * Spawns the new player on the screen
     */
    private void spawn() {
        this.px = spawnX;
        this.py = spawnY;
    }

    /**
     * Updates the player to check if they have won
     */
    public void update(GameObject o) {
        win = checkWin();
    } // end update

    /** 
     * Draws the rectangle on the screen
     */
    public void draw(Graphics2D g) {
        // g.translate(this.getX(), this.getY());
        g.setColor(c);
        g.fillRect(data.getXpos(), data.getYpos(), h, w);
    } // end draw

    public boolean checkWin() {
        if (data.getXpos() >= winPos) {
            return true;
        } else {
            return false;
        }
    } // end checkWin

} // end Player