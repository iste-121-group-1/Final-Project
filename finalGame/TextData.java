package finalGame;

import static finalGame.DataObject.DataType.TEXT;

/**
 * TextData
 */
public class TextData extends DataObject {

    public DataType type;
    public String message;

    public TextData() {
        type = TEXT;
        message = "";
    }
    
    public TextData(String _message) {
        type = TEXT;
        message = _message;
    }
}