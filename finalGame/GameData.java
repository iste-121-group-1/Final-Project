package finalGame;

import finalGame.Game.*;
//import static finalGame.DataObject.DataType.GAME;
import java.awt.Color;

/**
 * GameData
 */
public class GameData extends DataObject {

    // GameData will contain Type: Game
    // Player GameState
    // Player Name
    // Player Color
    // Player Position -> seperated as xpos and ypos
    public DataType type;
    public GAME_STATES state;
    public String name;
    public Color color;
    public int xpos;
    public int ypos;

    /**
     * GameData consturctor
     * 
     * @param _state the GAME_STATES passed from the client
     * @param _name  the client's username
     * @param _color the client player's color
     * @param _xpos  player x position
     * @param _ypos  player y position
     */
    public GameData(GAME_STATES _state, String _name, Color _color, int _xpos, int _ypos) {
        type = DataType.GAME;
        state = _state;
        name = _name;
        color = _color;
        xpos = _xpos;
        ypos = _ypos;
    }

    /**
     * Constructor just for position data, gives it DataType.POS
     * 
     * @param _xpos
     * @param _ypos
     */
    public GameData(int _xpos, int _ypos) {
        type = DataType.POS;
        xpos = _xpos;
        ypos = _ypos;
    }

    /**
     * Constructor just for game satate, gives it DataType.STATE
     * 
     * @param _state
     */
    public GameData(GAME_STATES _state) {
        type = DataType.STATE;
        state = _state;
    }

    /**
     * Updates the game state
     * 
     * @param _state
     */
    public void updateGameState(GAME_STATES _state) {
        state = _state;
    }

    /**
     * Updates the x position
     * 
     * @param _xpos
     */
    public void updateXpos(int _xpos) {
        xpos = _xpos;
    }

    /**
     * Updates the y position
     * 
     * @param _ypos
     */
    public void updateYpos(int _ypos) {
        ypos = _ypos;
    }

    /**
     * Everything after this point is undocumented
     * Its just getters for every attribute
     * 
     */

    public DataType getType() {
        return this.type;
    }

    public GAME_STATES getState() {
        return this.state;
    }

    public String getName() {
        return this.name;
    }

    public Color getColor() {
        return this.color;
    }

    public int getXpos() {
        return this.xpos;
    }

    public int getYpos() {
        return this.ypos;
    }

}