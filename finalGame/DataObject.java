package finalGame;

import java.io.Serializable;

/**
 * DataObject
 */
public abstract class DataObject implements Serializable {

    public enum DataType {
        TEXT,
        GAME,
        POS,
        STATE,
        WIN,
    }

	public DataType DataType;
}