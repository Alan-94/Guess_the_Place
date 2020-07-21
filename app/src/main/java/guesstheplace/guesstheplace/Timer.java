package guesstheplace.guesstheplace;

import android.os.CountDownTimer;
import android.widget.TextView;

import java.util.Locale;

public class Timer {
    private boolean mTimerRunning;
    private long mTimeLeftInMillis;
    private CountDownTimer mCountDownTimer;

    public void setmTimeLeftInMillis(long mTimeLeftInMillis) {
        this.mTimeLeftInMillis = mTimeLeftInMillis;
    }

    public void updateCountDownText(TextView textViewCountDown, MainActivity activity){
        int seconds = (int) mTimeLeftInMillis / 1000;
        String timeLeftFormatted = String.format(Locale.getDefault(),"%02d", seconds);
        textViewCountDown.setText(timeLeftFormatted);
        if(seconds == 0){
            activity.EndPlay();
        }
    }

    public void startTimer(final TextView textViewCountDown, final MainActivity activity){
        //updated every one second
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText(textViewCountDown, activity);
            }

            @Override
            public void onFinish() {
                mTimerRunning = false;
            }
        }.start();
        mTimerRunning = true;
    }


    public void pauseTimer(){
        mCountDownTimer.cancel();
        mTimerRunning = false;
    }

}
