package guesstheplace.guesstheplace;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import guesstheplace.guesstheplace.Database.DatabaseInitializer;
import guesstheplace.guesstheplace.Database.Place;
import guesstheplace.guesstheplace.Database.Placesdb;
import guesstheplace.guesstheplace.Database.Score;
import guesstheplace.guesstheplace.Database.ScoresDatabase;

public class MainActivity extends AppCompatActivity implements MapEventsReceiver {
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private MapView map = null;
    private static final long START_TIME_IN_MILLIS = 20000;
    private TextView mTextViewCountDown;
    private CountDownTimer mCountDownTimer;
    private boolean mTimerRunning;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;
    private FloatingActionButton imageButton;
    private FloatingActionButton wikiLinkButton;
    private PopupWindow popupWindowEnd;
    private LayoutInflater layoutInflater;
    private RelativeLayout relativeLayout;
    private GeoPoint geoPoint;
    private String latitudeSign;
    private String longitudeSign;
    private Marker correctMarker;
    private int level;
    private int score;
    private float difficulty;
    private int threshold;
    private String name;
    private FrameLayout scoreboardFragmentContainer;
    private ViewGroup container;
    private int height;
    private int width;
    private Timer timer;
    private MainActivity instance;
    private ImageView imageView;
    private Location startLoc;
    private Location correctLoc;
    private RequestQueue requestQueue;
    private String result;
    private String pageId;
    private Place[][] placeList;
    private String placeName;
    private String placeImageUrl;
    private double placeLatitude;
    private double placeLongitude;
    private Context context;
    public static ScoresDatabase scoresDatabase;
    private int id;
    private String coordString;
    private String wikiURL;
    private PreferencesManager preferencesManager;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this.getApplicationContext();
        preferencesManager = new PreferencesManager();
        //retrieve values

        PreferencesResult preferencesResult = preferencesManager.getPreferencesObject(context);
        id = preferencesResult.getId();
        score = preferencesResult.getScore();
        difficulty = preferencesResult.getDifficulty();
        threshold = preferencesResult.getThreshold();
        level = preferencesResult.getLevel();
        if(level > 10){
            level = 1;
        }
        placeList = getList();
        int randomPlace = (int) (placeList[level - 1].length * Math.random());
        placeName = placeList[level - 1][randomPlace].getName();
        placeImageUrl = placeList[level - 1][randomPlace].getImageUrl();
        placeLatitude = placeList[level - 1][randomPlace].getLatitude();
        placeLongitude = placeList[level - 1][randomPlace].getLongitude();
        name = Objects.requireNonNull(getIntent().getExtras()).getString("name", "ERROR");
        requestQueue = Volley.newRequestQueue(context);
        startAsyncTask();
        wikiURL = "https://en.wikipedia.org/wiki/" + placeName;
        wikiURL = wikiURL.replace(" ", "_");
        // create database
        scoresDatabase = ScoresDatabase.getScoresDatabase(this);
        DatabaseInitializer.populateSync(scoresDatabase);
        //      LOAD MAP
        //load/initialize the osmdroid configuration
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context));
        //load tile map
        setContentView(R.layout.activity_main);
        map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.BASE_OVERLAY_NL);
        requestPermissionsIfNecessary(new String[]{
                // if you need to show the current location, uncomment the line below
                Manifest.permission.ACCESS_FINE_LOCATION,
                // WRITE_EXTERNAL_STORAGE is required in order to show the map
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        });
        //parameters
        setMapParameters();
        //instance
        instance = this;
        //overlay for markers
        MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(this);
        map.getOverlays().add(0, mapEventsOverlay);
        //image button
        loadImageButton();
        //marker with correct position
        geoPoint = new GeoPoint(placeLatitude, placeLongitude);
        setLatitudeSign();

        correctMarker = new Marker(map);
        correctMarker.setPosition(geoPoint);
        correctMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        Drawable greenIcon = getDrawable(R.drawable.icon_green_resized);
        correctMarker.setIcon(greenIcon);
        coordString = (int) geoPoint.getLatitude() + "°" + (int) ((Math.abs(geoPoint.getLatitude())-Math.abs((int) geoPoint.getLatitude())) * 60) + "'" + latitudeSign + ", "
                + (int) geoPoint.getLongitude() + "°" +(int) ((Math.abs(geoPoint.getLongitude()-Math.abs((int) geoPoint.getLongitude())) * 60)) + "'"+ longitudeSign;
        correctLoc = new Location("");
        correctLoc.setLatitude(geoPoint.getLatitude());
        correctLoc.setLongitude(geoPoint.getLongitude());
        //timer
        mTextViewCountDown = findViewById(R.id.text_view_countdown);
        timer = new Timer();
        timer.setmTimeLeftInMillis(START_TIME_IN_MILLIS);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        timer.startTimer(mTextViewCountDown, instance);
    }
    //do not remove even if empty
    @Override
    public boolean singleTapConfirmedHelper(GeoPoint p) {
        imageView.setVisibility(View.INVISIBLE);
        return false;
    }
    @Override
    public void onBackPressed() {
        //super - do not perform normal actions
        super.onBackPressed();
        finish();
    }
    @Override public boolean longPressHelper(GeoPoint p) {
        if (mTimerRunning) {
            timer.pauseTimer();
        }
                Marker marker = new Marker(map);
                GeoPoint startPoint = new GeoPoint(p.getLatitude(), p.getLongitude());
                marker.setPosition(startPoint);
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                Drawable blueIcon = getDrawable(R.drawable.icon_blue_resized);
                marker.setIcon(blueIcon);
                map.getOverlays().add(marker);
                startLoc = new Location("");
                startLoc.setLatitude(p.getLatitude());
                startLoc.setLongitude(p.getLongitude());
                checkScore();
                return false;
    }
    @Override
    protected void onResume() {
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ArrayList<String> permissionsToRequest = new ArrayList<>(Arrays.asList(permissions).subList(0, grantResults.length));
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE
            );
        }
    }
        private void requestPermissionsIfNecessary(String[] permissions){
            ArrayList<String> permissionsToRequest = new ArrayList<>();
            for (String permission : permissions){
                if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    //permission is not granted
                    permissionsToRequest.add(permission);
                }
            }
            if (permissionsToRequest.size() > 0){
                ActivityCompat.requestPermissions(
                        this,
                        permissionsToRequest.toArray(new String[0]),
                        REQUEST_PERMISSIONS_REQUEST_CODE
                );
            }
        }

        private void checkScore(){
            timer.pauseTimer();
            double distance = startLoc.distanceTo(correctLoc) / 1000;
            Toast.makeText(this, "Distance: " + (int) distance + " km", Toast.LENGTH_SHORT).show();
            if (distance <= threshold){
                score += (int) ((1000 - distance) * difficulty);
                if(level < 10) {
                    EndLevel();
                }
                else{
                    getSharedPreferences("Preferences",Context.MODE_PRIVATE).edit().putInt("level", level + 1).apply();
                    EndPlay();
                }
            }
            else{
                EndPlay();
            }
        }
        @SuppressLint({"SetTextI18n", "DefaultLocale"})
        public void EndLevel(){
            pointToCorrectLocation();
            popupWindowLauncher();
            container = (ViewGroup) layoutInflater.inflate(R.layout.endlevel_window, null);
            popupWindowEnd = new PopupWindow(container, width, height, false);
            popupWindowEnd.showAtLocation(relativeLayout, Gravity.BOTTOM, 0,0);
            popupWindowEnd.setOutsideTouchable(false);
            //set textviews
            TextView nameText = (TextView) container.findViewById(R.id.name);
            TextView coordsText = (TextView) container.findViewById(R.id.coords);
            TextView scoreText = (TextView) container.findViewById(R.id.scoreNum);
            TextView wikiText = (TextView) container.findViewById(R.id.wikiText);
            ScrollView scrollView= (ScrollView) container.findViewById(R.id.scrollView);
            scrollView.setScrollbarFadingEnabled(false);
            FloatingActionButton wikiLinkButton = (FloatingActionButton) container.findViewById(R.id.wikiLinkButton);
            nameText.setText(placeName);
            //buttons
            Button nextLevelButton = (Button) container.findViewById(R.id.nextButton);
            nextLevelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindowEnd.dismiss();
                    NextLevel();
                    layoutInflater = null;
                }
            });
            wikiLinkButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                    browserIntent.setData(Uri.parse(wikiURL));
                    startActivity(browserIntent);
                }
            });
            coordsText.setText(coordString);
            scoreText.setText(String.valueOf(score));
            wikiText.setText(" " + result +"\n");
        }
        public void EndPlay(){
        Log.d("endplay", "endplay");
            pointToCorrectLocation();
            popupWindowLauncher();
            container = (ViewGroup) layoutInflater.inflate(R.layout.endplay_window, null);
            popupWindowEnd = new PopupWindow(container, width, height, false);
            popupWindowEnd.showAtLocation(relativeLayout, Gravity.BOTTOM, 0,0);
            popupWindowEnd.setOutsideTouchable(false);
            //set textviews
            TextView nameText = (TextView) container.findViewById(R.id.name);
            TextView coordsText = (TextView) container.findViewById(R.id.coords);
            TextView scoreText = (TextView) container.findViewById(R.id.scoreNum);
            TextView wikiText = (TextView) container.findViewById(R.id.wikiText);
            ScrollView scrollView= (ScrollView) container.findViewById(R.id.scrollView);
            FloatingActionButton wikiLinkButton = (FloatingActionButton) container.findViewById(R.id.wikiLinkButton);
            scrollView.setScrollbarFadingEnabled(false);
            nameText.setText(placeName);
            getIntent().putExtra("score", score);
            //save name & score to scoreboard
            if(score > 0) {
                getSharedPreferences("Preferences",Context.MODE_PRIVATE).edit().putInt("id", id + 1).apply();
                writeScoreToDatabase();
            }
            //buttons
            Button restartButton = (Button) container.findViewById(R.id.restartButton);
            restartButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindowEnd.dismiss();
                    Restart();
                    recreate();
                }
            });
            Button scoreboardButton = (Button) container.findViewById(R.id.scoreboardButton);
            scoreboardButton.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     popupWindowEnd.dismiss();
                     scoreboardFragmentContainer = (FrameLayout) findViewById(R.id.scoreboardContainer);
                     displayScoreboard();
                 }
            });
            wikiLinkButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                    browserIntent.setData(Uri.parse(wikiURL));
                    startActivity(browserIntent);
                }
            });
            coordsText.setText(coordString);
            scoreText.setText(String.valueOf(score));
            wikiText.setText(" " + result +"\n");

        }
        private void NextLevel(){
            preferencesManager.incrementPreferences(context, level, score, difficulty, threshold);
            recreate();
        }
        private void Restart(){
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            layoutInflater = null;
            preferencesManager.resetPreferences(context);
            recreate();
        }
        public Place[][] getList(){
            return Placesdb.populatePlacesData();
        }
    // Backwards compatible recreate().
    @Override
    public void recreate()
    {
        super.recreate();
    }
    public void displayScoreboard(){
        //https://www.youtube.com/watch?v=BMTNaPcPjdw
        ScoreboardFragment scoreboardFragment = ScoreboardFragment.newInstance();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        //pass the arguments 2 times, so they work if you go back
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
        transaction.addToBackStack(null);
        transaction.add(R.id.scoreboardContainer, scoreboardFragment, "SCOREBOARD_FRAGMENT").commit();
    }
    public MainActivity getInstance() {
        return instance;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (popupWindowEnd != null){
            popupWindowEnd.dismiss();
        }
    }

    private void loadImageButton(){
        relativeLayout = (RelativeLayout) findViewById(R.id.relative_layout);
        imageButton = findViewById(R.id.image_button);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        width = dm.widthPixels;
        height = dm.heightPixels;
        final int imageWidth = (int) (width * .8);
        final int imageHeight = (int) (height * .8);
        imageView = findViewById(R.id.imageView);
        imageView.getLayoutParams().height = imageHeight;
        imageView.getLayoutParams().width = imageWidth;
        try {
            Picasso.get().load(placeImageUrl).fit().centerInside().into(imageView);
        }catch(Exception e){
            Log.e("Picasso", "Picasso error", e);
        }
        imageButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public void onClick(View v) {
                toggleImageViewVisibility();
            }
        });
    }
    private void toggleImageViewVisibility(){
        if(imageView.getVisibility() == View.INVISIBLE){
            imageView.setVisibility(View.VISIBLE);
        }
        else{
            imageView.setVisibility(View.INVISIBLE);
        }
    }
    private void popupWindowLauncher(){
        mTextViewCountDown.setText(" ");
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        width = dm.widthPixels;
        height = dm.heightPixels;
        Context context = getApplicationContext();
        context.setTheme(R.style.EndLevelWindow);
        layoutInflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
    }
    private void writeScoreToDatabase(){
        Score scoreEntity = new Score();
        scoreEntity.setId(id);
        scoreEntity.setPlayerName(name);
        scoreEntity.setLevel(level -  1);
        scoreEntity.setPoints(score);
        scoresDatabase.scoreDao().insertScore(scoreEntity);
    }
    private class LoadWikipediaArticle extends AsyncTask<Void, Void, String>{
        //https://www.youtube.com/watch?v=uKx0FuVriqA
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {
            String urlPageId ="https://en.wikipedia.org/w/api.php?action=query&format=json&list=search&utf8=1&srsearch=" + placeName;
            JsonObjectRequest requestPageId = new JsonObjectRequest(Request.Method.GET, urlPageId,null,  new Response.Listener<JSONObject>(){

                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONObject resultsPageId = response.getJSONObject("query").getJSONArray("search").getJSONObject(0);
                        pageId = resultsPageId.getString("pageid");
                        String url="https://en.wikipedia.org/w/api.php?action=query&format=json&prop=extracts&exintro&explaintext&titles=" + placeName;
                        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,null,  new Response.Listener<JSONObject>(){

                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    JSONObject results = response.getJSONObject("query").getJSONObject("pages").getJSONObject(pageId);
                                    result = results.getString("extract");
                                } catch (JSONException e) {
                                    Log.e("JSON", "Json error", e);
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("JSON", "Volley error", error);
                            }
                        });
                        requestQueue.add(request);
                    } catch (JSONException e) {
                        Log.e("JSON", "Json error", e);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("JSON", "Volley error", error);
                }
            });
            requestQueue.add(requestPageId);
            requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<StringRequest>(){
                @Override
                public void onRequestFinished(Request<StringRequest> request) {
                }
            });
            return result;
        }
        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }
    public void startAsyncTask(){
        LoadWikipediaArticle loadWikipediaArticle = new LoadWikipediaArticle();
        loadWikipediaArticle.execute();
    }
    private void setMapParameters(){
        map.getController().setZoom(3.0);
        map.setMaxZoomLevel(7.0);
        map.setMinZoomLevel(3.0);
        map.setMultiTouchControls(true);

    }
    private void pointToCorrectLocation(){
        map.getController().setZoom(5.0);
        map.getOverlays().add(correctMarker);
        map.invalidate();
        map.getController().setCenter(correctMarker.getPosition());
        imageButton.setVisibility(View.INVISIBLE);
    }
    private void setLatitudeSign(){
        if (geoPoint.getLatitude() > 0){
            latitudeSign = "N";
        }
        else{
            latitudeSign = "S";
        }
        if (geoPoint.getLongitude() > 0){
            longitudeSign = "E";
        }
        else{
            longitudeSign = "W";
        }
    }
}