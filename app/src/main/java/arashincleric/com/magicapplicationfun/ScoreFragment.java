package arashincleric.com.magicapplicationfun;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


/**
 * Fragment to keep track of score during a game
 * TODO: clean up the sharedpreferences stuff because no longer neede(maybe)
 */
public class ScoreFragment extends Fragment {

    private static final String ARG_SCORE = "SCORE";
    private SharedPreferences sharedPreferences;

    private int score;
    private TextView scoreView;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment ScoreFragment.
     */
    public static ScoreFragment newInstance() {
        ScoreFragment fragment = new ScoreFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public ScoreFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);
        sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_score, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        Button increaseScoreBtn = (Button)view.findViewById(R.id.increase_score);
        Button decreaseScoreBtn = (Button)view.findViewById(R.id.decrease_score);

        increaseScoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increaseScore();
            }
        });

        decreaseScoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decreaseScore();
            }
        });

        scoreView = (TextView)view.findViewById(R.id.scoreView);

        if(sharedPreferences != null){
            setScoreView(sharedPreferences.getString(ARG_SCORE, "20"));
        }

        scoreView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (score <= 0) {
                    new AlertDialog.Builder(getActivity())
                            .setTitle(R.string.end_game_title)
                            .setMessage(R.string.replay_game_message)
                            .setCancelable(false)
                            .setPositiveButton(R.string.replay_yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    resetScore();
                                }
                            })
                            .setNegativeButton(R.string.replay_no, null).show();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /**
     * Reset the score
     */
    public void resetScore(){
        score = 20;
        scoreView.setText(Integer.toString(score));
    }

    /**
     * Decrease score by one
     */
    public void decreaseScore() {
        if(score > 0){
            score--;
            scoreView.setText(Integer.toString(score));
        }
    }

    /**
     * Increase score by one
     */
    public void increaseScore() {
        score++;
        scoreView.setText(Integer.toString(score));
    }

    /**
     * Getter to retrieve score
     * @return Current score
     */
    public int getScore(){
        return score;
    }

    //Does this even work?
    @Override
    public void onStop(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(ARG_SCORE, Integer.toString(score));
        editor.commit();

//        getArguments().putString(ARG_SCORE, Integer.toString(score));
        super.onStop();
    }

    /**
     * Set the score during configuration change or new game
     * @param savedScore Score to display on screen
     */
    public void setScoreView(String savedScore){
        if(savedScore != null){
            if(Integer.parseInt(savedScore) >= 0){
                scoreView.setText(savedScore);
                score = Integer.parseInt(savedScore);
            }
        }
        else {
            score = 20;
            scoreView.setText("20");
        }
    }



}
