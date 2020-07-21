package guesstheplace.guesstheplace;

import java.util.ArrayList;
import java.util.List;

public class PreferencesResult {
    static List<PreferencesResult> PreferencesResultList = new ArrayList<>();
    private int id;
    private int score;
    private float difficulty;
    private int threshold;
    private int level;

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public float getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(float difficulty) {
        this.difficulty = difficulty;
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public PreferencesResult(int id, int score, float difficulty, int threshold, int level){
        this.id = id;
        this.score = score;
        this.difficulty = difficulty;
        this.threshold = threshold;
        this.level = level;
    }
}
