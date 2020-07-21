package guesstheplace.guesstheplace;

import android.content.Context;

public class PreferencesManager {
    public void resetPreferences(Context context){
        context.getSharedPreferences("Preferences", Context.MODE_PRIVATE).edit().putInt("level", 1).apply();
        context.getSharedPreferences("Preferences",Context.MODE_PRIVATE).edit().putInt("score", 0).apply();
        context.getSharedPreferences("Preferences",Context.MODE_PRIVATE).edit().putFloat("difficulty", 1.0f).apply();
        context.getSharedPreferences("Preferences",Context.MODE_PRIVATE).edit().putInt("threshold", 1000).apply();
    }
    public void incrementPreferences(Context context, int level, int score, float difficulty, int threshold){
        context.getSharedPreferences("Preferences",Context.MODE_PRIVATE).edit().putInt("level", level + 1).apply();
        context.getSharedPreferences("Preferences",Context.MODE_PRIVATE).edit().putInt("score", score).apply();
        context.getSharedPreferences("Preferences",Context.MODE_PRIVATE).edit().putFloat("difficulty", difficulty * 1.2f).apply();
        context.getSharedPreferences("Preferences",Context.MODE_PRIVATE).edit().putInt("threshold", threshold - 50).apply();
    }

    public PreferencesResult getPreferencesObject(Context context){
        int id = context.getSharedPreferences("Preferences", Context.MODE_PRIVATE).getInt("id", 0);
        int score = context.getSharedPreferences("Preferences", Context.MODE_PRIVATE).getInt("score", 0);
        float difficulty = context.getSharedPreferences("Preferences",Context.MODE_PRIVATE).getFloat("difficulty", 1.0f);
        int threshold = context.getSharedPreferences("Preferences",Context.MODE_PRIVATE).getInt("threshold", 1000);
        int level = context.getSharedPreferences("Preferences",Context.MODE_PRIVATE).getInt("level", 1);
        return new PreferencesResult(id, score, difficulty, threshold, level);

    }


}
