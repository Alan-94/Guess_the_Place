package guesstheplace.guesstheplace.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Score.class}, version = 1, exportSchema = false)
public abstract class ScoresDatabase extends RoomDatabase {
    private static ScoresDatabase INSTANCE;
    public abstract ScoreDao scoreDao();

    public static ScoresDatabase getScoresDatabase(Context context){
        if (INSTANCE == null){
            INSTANCE = Room
                    .databaseBuilder(context.getApplicationContext(), ScoresDatabase.class, "scores")
                    .allowMainThreadQueries()
                    .build();
        }
        return INSTANCE;
    }
    }
