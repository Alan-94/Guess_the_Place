package guesstheplace.guesstheplace;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.List;

import guesstheplace.guesstheplace.Database.Score;

import static guesstheplace.guesstheplace.MainActivity.scoresDatabase;

public class ScoreboardFragment extends Fragment {
private List<Score> allScores;
private int numberOfScores;
private int i;


    public ScoreboardFragment() {
        // Required empty public constructor
    }
    public static ScoreboardFragment newInstance() {
        ScoreboardFragment fragment = new ScoreboardFragment();
        Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        allScores = scoresDatabase.scoreDao().getAll();
        numberOfScores = allScores.size();
    }

    @SuppressLint("ResourceType")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.scoreboard_fragment, container, false);

        TableLayout tableLayout = v.findViewById(R.id.scoresTable);
        //NestedScrollView nestedScrollView =v.findViewById(R.id.nestedScrollView);

        for(i = 0; i < numberOfScores; i++){
            inflater.inflate(R.xml.table_row,tableLayout,true);
            TableRow tableRow = (TableRow) tableLayout.getChildAt(i);
            TextView numberTextView = (TextView) tableRow.getChildAt(0);
            TextView nameTextView = (TextView) tableRow.getChildAt(1);
            TextView levelTextView = (TextView) tableRow.getChildAt(2);
            TextView scoreTextView = (TextView) tableRow.getChildAt(3);
            String numberText = i + 1 + ".";
            numberTextView.setText(numberText);
            nameTextView.setText(String.valueOf(allScores.get(i).getPlayerName()));
            levelTextView.setText(String.valueOf(allScores.get(i).getLevel()));
            scoreTextView.setText(String.valueOf(allScores.get(i).getPoints()));

            numberTextView.setTextColor(Color.parseColor("#FFFFFF"));
            nameTextView.setTextColor(Color.parseColor("#FFFFFF"));
            levelTextView.setTextColor(Color.parseColor("#FFFFFF"));
            scoreTextView.setTextColor(Color.parseColor("#FFFFFF"));
        }


        Button buttonReturn = v.findViewById(R.id.buttonReturn);
        buttonReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        return v;
    }

}