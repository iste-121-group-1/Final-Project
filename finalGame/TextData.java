package finalGame;
/**
 * TextData
 */
public class TextData extends DataObject{

    DataType type;
    String message;
    
    public TextData() {
        type = DataType.TEXT;
        message = "";
    }
    
    public TextData(String _message) {
        type = DataType.TEXT;
        message = _message;
    }
}