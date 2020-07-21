package guesstheplace.guesstheplace.Database;

import android.os.AsyncTask;

import androidx.annotation.NonNull;

import java.util.List;

public class DatabaseInitializer {
    private static final String TAG = DatabaseInitializer.class.getName();
    public static void populateSync(@NonNull final ScoresDatabase db){
        populateWithData(db);
    }

    private static void populateWithData(ScoresDatabase db){
        List<Score> scoreList = db.scoreDao().getAll();
    }
    public class InsertAsyncTask extends AsyncTask<Score, Void, Void>{
        ScoreDao mScoreDao;
        public InsertAsyncTask(ScoreDao mScoreDao){
            this.mScoreDao = mScoreDao;
        }

        @Override
        protected Void doInBackground(Score... scores) {
            mScoreDao.insertScore(scores[0]);
            return null;
        }
    }

}
