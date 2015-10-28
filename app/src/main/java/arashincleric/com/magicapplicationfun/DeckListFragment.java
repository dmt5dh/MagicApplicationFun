package arashincleric.com.magicapplicationfun;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;


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

        //TODO: save json of deck lists
        File deckFile = new File(getActivity().getFilesDir(), FILENAME);
        if(!deckFile.exists()){
            try{
                FileOutputStream fos = getActivity().openFileOutput(FILENAME, Context.MODE_PRIVATE);
                JSONArray userDecks = new JSONArray(); //Add blank stuff
                fos.write(userDecks.toString(4).getBytes());
                fos.close();
            }
            catch(Exception e){
                Log.e("DECKLIST", e.getMessage());
            }

        }

    }

    public JSONArray getDeckJson(){
        JSONArray deckArray;
        try{
            //Read the JSON
            FileInputStream fis = getActivity().openFileInput(FILENAME);
            StringBuffer sb = new StringBuffer("");
            byte[] buffer = new byte[1024];
            int n;

            while((n = fis.read(buffer)) != -1) {
                sb.append(new String(buffer, 0, n));
            }

            fis.close();

            //Check if already included
            deckArray = new JSONArray(sb.toString());
            return deckArray;

        } catch (IOException e) {
            Log.e("ADDDECK", e.getMessage());
        } catch (JSONException e) {
            Log.e("ADDDECK", e.getMessage());
        }
        return null;

    }

    public ArrayList<String> getDeckList(){
        ArrayList<String> deckNamesList = new ArrayList<String>();
        try{

            //Check if already included
            JSONArray deckArray = getDeckJson();
            if(deckArray != null){
                for(int i = 0; i < deckArray.length(); i++){
                    deckNamesList.add(deckArray.getString(i));
                }
            }

        } catch (JSONException e) {
            Log.e("ADDDECK", e.getMessage());
        }

        return deckNamesList;

    }

    //I know this is inefficient... but lets assume these will be relatively small...
    public void addDeck(String name){
        try{
            ArrayList<String> deckNamesList = new ArrayList<String>();

            //Check if already included
            JSONArray deckArray = getDeckJson();
            //do this to check for duplicates but also don't double loop
            if(deckArray != null){
                for(int i = 0; i < deckArray.length(); i++){
                    String deckName = deckArray.getString(i);
                    if(deckName.equals(name)){
                        new AlertDialog.Builder(getActivity())
                                .setMessage("Name already in use")
                                .setNegativeButton("Close", null)
                                .show();
                        return;
                    }
                    deckNamesList.add(deckName);
                }
                deckArray.put(name);
                deckNamesList.add(name);

                //Saved the file
                FileOutputStream fos = getActivity().openFileOutput(FILENAME, Context.MODE_PRIVATE);
                fos.write(deckArray.toString().getBytes());
                fos.close();

                adapter.clear();
                adapter.addAll(deckNamesList);
                adapter.notifyDataSetChanged();
                getListView().invalidateViews();
                getListView().refreshDrawableState();
            }

        } catch (IOException e) {
            Log.e("ADDDECK", e.getMessage());
        } catch (JSONException e) {
            Log.e("ADDDECK", e.getMessage());
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


//        String[] values = new String[] { "Android", "iPhone", "WindowsMobile",
//                "Blackberry", "WebOS", "Ubuntu", "Windows7", "Max OS X",
//                "Linux", "OS/2" };
        adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, getDeckList());
        setListAdapter(adapter);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_deck_list, container, false);
    }

    @Override
    public void onListItemClick(ListView l, View v, int pos, long id){
        //TODO: click to view whole list
    }


}
