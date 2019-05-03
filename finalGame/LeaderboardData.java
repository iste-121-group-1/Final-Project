package finalGame;

import finalGame.Game.*;
import java.io.Serializable;
import java.awt.Color;

/**
 * LeaderboardData
 */
public class LeaderboardData extends DataObject implements Serializable {

    public DataType DataType;
    public String name;
    public String data;

    /**
     * Leaderboard
     * @param username The winning player's name
     * @param data the winning player's time
     */
    public LeaderboardData(String username, String data) {
        this.DataType = DataType.LEADERBOARD;
        this.name = username;
        this.data = data;
    }

    /**
     * Leaderboard
     * @param data the winning player's time and name
     */
    public LeaderboardData(String data) {
        this.DataType = DataType.LEADERBOARD;
        this.data = data;
    }
}