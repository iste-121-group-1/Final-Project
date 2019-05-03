package finalGame;

import finalGame.Game.*;
import java.io.Serializable;
import java.awt.Color;
/**
 * PosObject
 */
public class PosObject extends DataObject implements Serializable {

    public DataType DataType;
    public GAME_STATES state;
    public String name;
    public Color color;
    public int xpos;
    public int ypos;

    /**
     * PosObject
     * @param name the username
     * @param xpos the xpos
     * @param ypos the ypos
     */
    public PosObject(String name, int xpos, int ypos) {
        this.DataType = DataType.POS;
        this.name = name;
        this.xpos = xpos;
        this.xpos = xpos;
    }

    /**
     * PosObject
     * @param xpos the xpos
     * @param ypos the ypos
     */
    public PosObject(int xpos, int ypos) {
        this.DataType = DataType.POS;
        this.xpos = xpos;
        this.xpos = xpos;
    }
     
}