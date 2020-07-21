package guesstheplace.guesstheplace;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class StartActivity extends AppCompatActivity {
private EditText editText;
private Context context;
private PreferencesManager preferencesManager;
//icon source: https://freeicons.io
//icons generated in: https://appicon.co

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        preferencesManager= new PreferencesManager();
        String savedName = getSharedPreferences("Preferences", Context.MODE_PRIVATE).getString("name", "");
        preferencesManager.resetPreferences(context);
        setContentView(R.layout.activity_start);
        editText = (EditText) findViewById(R.id.nameInput);
        if(savedName == null) {
            savedName ="";
        }
        editText.setText(savedName);
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPlaying();
            }
        });

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        preferencesManager.resetPreferences(context);
    }

    public int startPlaying(){

        String name = editText.getText().toString();
        if(!checkNetworkState()){
            Toast.makeText(this, "Internet connection required", Toast.LENGTH_SHORT).show();
            return 1;
        }
        if (name.matches("[A-Za-z0-9]+") && name.length() <= 32) {
            getSharedPreferences("Preferences",Context.MODE_PRIVATE).edit().putString("name", "").apply();
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("name", name);
            Log.d("name", name);
            startActivity(intent);
            return 0;
        }
        else{
            Toast.makeText(this, "Enter a valid name", Toast.LENGTH_SHORT).show();
            return 1;
        }
    }

    private boolean checkNetworkState(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        // connected to the internet
        return activeNetwork != null;
    }

}