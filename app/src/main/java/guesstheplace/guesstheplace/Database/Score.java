package guesstheplace.guesstheplace.Database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity (tableName = "scores")
public class Score{
@PrimaryKey
    private int id;
@ColumnInfo(name="playerName")
    private String playerName;
    @ColumnInfo(name="level")
    private int level;
    @ColumnInfo(name="points")
    private int points;

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
