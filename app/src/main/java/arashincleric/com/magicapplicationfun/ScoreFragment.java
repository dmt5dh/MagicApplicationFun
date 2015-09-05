package arashincleric.com.magicapplicationfun;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link ScoreFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScoreFragment extends Fragment {

    private static final String ARG_SCORE = "SCORE";


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

        if(savedInstanceState != null){
            String mScore = getArguments().getString(ARG_SCORE);
            scoreView.setText(savedInstanceState.getString(mScore));
            score = Integer.parseInt(mScore);
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

    @Override
    public void onStop(){
        getArguments().putString(ARG_SCORE, Integer.toString(score));
        super.onStop();
    }

}
