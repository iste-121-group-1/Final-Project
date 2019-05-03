package finalGame;

import finalGame.Game.*;
import java.io.Serializable;
import java.awt.Color;
/**
 * StateObject
 */
public class StateObject extends DataObject implements Serializable {

    public DataType DataType;
    public GAME_STATES state;
    public String name;
    public Color color;
    public int xpos;
    public int ypos;

    /**
     * StateObject 
     * @param name the username the state is tied to 
     * @param state the game state
     */
    public StateObject(String name, GAME_STATES state) {
        this.DataType = DataType.STATE;
        this.name = name;
        this.state = state;
    }

    /**
     * StateObject
     * @param state the game state
     */
    public StateObject(GAME_STATES state) {
        this.DataType = DataType.STATE;
        this.state = state;
    }
}