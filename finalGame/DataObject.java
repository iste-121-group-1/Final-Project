package finalGame;
/**
 * DataObject
 */
public abstract class DataObject {

    public enum DataType {
        TEXT,
        GAME,
        POS,
        STATE,
    }

	public DataType DataType;
}