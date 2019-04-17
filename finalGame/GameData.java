package finalGame;

import finalGame.Game.*;
import java.awt.Color;

/**
 * GameData
 */
public class GameData extends DataObject{

            // GameData will contain Type: Game
            // Player GameState
            // Player Name
            // Player Color
            // Player Position -> for now, seperated as xpos and ypos
    public DataType type;
    public GAME_STATES state;
    public String name;
    public Color color;
    public int xpos;
    public int ypos;
    
    public GameData(GAME_STATES _state, String _name, Color _color, int _xpos, int _ypos) {
        type = DataType.GAME;
        state = _state;
        name = _name;
        color = _color;
        xpos = _xpos;
        ypos = _ypos;
    }
}