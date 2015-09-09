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
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link ScoreFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScoreFragment extends Fragment {

    private static final String ARG_SCORE = "SCORE";
    private SharedPreferences sharedPreferences;

    private int score;
    private TextView scoreView;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param scoreParam Score Parameter.
     * @return A new instance of fragment ScoreFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ScoreFragment newInstance(String scoreParam) {
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

        String savedScore = sharedPreferences.getString(ARG_SCORE, null);

        if(savedScore != null){
//            String mScore = getArguments().getString(ARG_SCORE);
            scoreView.setText(savedScore);
            score = Integer.parseInt(savedScore);
        }
        else {
            score = 20;
            scoreView.setText("20");
        }

        scoreView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (score <= 0) {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("End Game")
                            .setMessage("Replay?")
                            .setCancelable(false)
                            .setPositiveButton(R.string.replay_yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    score = 20;
                                    scoreView.setText(Integer.toString(score));
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

    private void decreaseScore() {
        if(score > 0){
            score--;
            scoreView.setText(Integer.toString(score));
        }
    }

    private void increaseScore() {
        score++;
        scoreView.setText(Integer.toString(score));
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



}
