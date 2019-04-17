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
    DataType type;
    GAME_STATES state;
    String name;
    Color color;
    int xpos;
    int ypos;
    
    public GameData(GAME_STATES _state, String _name, Color _color, int _xpos, int _ypos) {
        type = DataType.GAME;
        state = _state;
        name = _name;
        color = _color;
        xpos = _xpos;
        ypos = _ypos;
    }
    
    /*public GameData(String _message) {
        type = DataType.GAME;
        //message = _message;
    }*/
}