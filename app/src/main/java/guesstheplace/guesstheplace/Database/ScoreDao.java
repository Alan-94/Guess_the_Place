package guesstheplace.guesstheplace.Database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ScoreDao {

//@Query("INSERT INTO scores (playerName) VALUES ('new playerName')")
@Insert (onConflict = OnConflictStrategy.REPLACE)
    void insertScore(Score score);
@Query("SELECT * FROM scores ORDER BY points DESC")
    List<Score> getAll();
@Delete
   void delete(Score score);
}
