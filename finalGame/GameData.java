package finalGame;

import finalGame.Game.*;
import static finalGame.DataObject.DataType.GAME;
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
     * @param _state the GAME_STATES passed from the client
     * @param _name the client's username
     * @param _color the client player's color
     * @param _xpos player x position
     * @param _ypos player y position
     */
    public GameData(GAME_STATES _state, String _name, Color _color, int _xpos, int _ypos) {
        type = GAME;
        state = _state;
        name = _name;
        color = _color;
        xpos = _xpos;
        ypos = _ypos;
    }

    public void updateGameState(GAME_STATES _state) {
        state = _state;
    }

    public void updateXpos(int _xpos) {
        xpos = _xpos;
    }

    public void updateYpos(int _ypos) {
        ypos = _ypos;
    }
}