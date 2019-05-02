package finalGame;

import static finalGame.DataObject.DataType.TEXT;

import java.io.Serializable;

/**
 * TextData
 */
public class TextData extends DataObject implements Serializable {

    public DataType DataType;
    public String message;
    public String username;

    /**
     * Constructor for creating an empty message
     */
    public TextData() {
        DataType = TEXT;
        message = "";
    }
    
    /**
     * Constructor for sending a message tied to no player to all clients
     * @param _message the message being sent
     */
    public TextData(String _message) {
        DataType = TEXT;
        message = _message;
    }

    /**
     * Constructor for sending a message from a specific user to all clients
     * @param _username the user the message is being sent from
     * @param _message the message being sent
     */
    public TextData(String _username, String _message) {
        DataType = TEXT;
        username = _username;
        message = _message;
    }
}