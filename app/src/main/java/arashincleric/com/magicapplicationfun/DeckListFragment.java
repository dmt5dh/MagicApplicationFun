package arashincleric.com.magicapplicationfun;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link DeckListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DeckListFragment extends ListFragment {
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */

    private ArrayAdapter<String> adapter;
    private final String FILENAME = "decklist_json";


    public static DeckListFragment newInstance() {
        DeckListFragment fragment = new DeckListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);

        return fragment;
    }

    public DeckListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1);

        File deckFile = new File(FILENAME);
        if(!deckFile.exists()){
            try{
                FileOutputStream fos = getActivity().openFileOutput(FILENAME, Context.MODE_PRIVATE);
                JSONArray userDecks = new JSONArray();
                userDecks.put("blankDeck");
                fos.write(userDecks.toString(4).getBytes());
                fos.close();
            }
            catch(Exception e){
                Log.e("DECKLIST", e.getMessage());
            }

        }

    }

    public void addDeck(String name){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        String[] values = new String[] { "Android", "iPhone", "WindowsMobile",
                "Blackberry", "WebOS", "Ubuntu", "Windows7", "Max OS X",
                "Linux", "OS/2" };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, values);
        setListAdapter(adapter);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_deck_list, container, false);
    }

    @Override
    public void onListItemClick(ListView l, View v, int pos, long id){
        //TODO: click to view whole list
    }


}
