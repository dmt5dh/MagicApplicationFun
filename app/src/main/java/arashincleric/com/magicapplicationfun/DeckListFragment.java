package arashincleric.com.magicapplicationfun;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
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

    public final static String DECK_LIST_MESSAGE = "com.arashincleric.magicapplicationfun.DECKLIST";
    public final static String DECK_NAME_MESSAGE = "com.arashincleric.magicapplicationfun.DECKNAME";
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
        setRetainInstance(true);
        //TODO: save json of deck lists
        File deckFile = new File(getActivity().getFilesDir(), FILENAME);
        if(!deckFile.exists()){
            try{
                JSONArray userDecks = new JSONArray(); //Add blank stuff
                writeToFile(userDecks.toString());
            }
            catch(Exception e){
                Log.e("DECKLIST", e.getMessage());
            }

        }

    }

    private void writeToFile(String content){
        try{
            FileOutputStream fos = getActivity().openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fos.write(content.getBytes());
            fos.close();
        } catch (Exception e){
            Log.e("DECKFILE", e.getMessage());
        }
    }

    public void deleteDeck(String deckName){
        try{
            JSONArray deckArray = getDeckJson();
            for(int i = 0; i < deckArray.length(); i++){
                if(deckArray.getJSONObject(i).getString("name").equals(deckName)){
                    deckArray.remove(i);
                    break;
                }
            }

            writeToFile(deckArray.toString());


        } catch(JSONException e){
            Log.e("DELETEDECK", e.getMessage());
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        refreshListView();
    }

    public void refreshListView(){
        adapter.clear();
        adapter.addAll(getDeckList());
        adapter.notifyDataSetChanged();
        getListView().invalidateViews();
        getListView().refreshDrawableState();
    }

    //1; success, 2:error
    public ArrayList<String> deleteFromDeck(String deck, String card){
        JSONArray deckArray = getDeckJson();
        JSONObject deckObject = null;
        try{
            int pos = -1;
            for(int i = 0; i < deckArray.length(); i++){
                if(deckArray.getJSONObject(i).getString("name").equals(deck)){
                    deckObject = deckArray.getJSONObject(i);
                    pos = i;
                    break;
                }
            }
            if(deckObject != null && pos != -1){
                JSONArray cardList = deckObject.getJSONArray("deckList");
                for(int i = 0; i < cardList.length(); i++){
                    if(cardList.get(i).equals(card)){
                        cardList.remove(i);
                        break;
                    }
                }
                deckObject.put("deckList", cardList);
                deckArray.put(pos, deckObject);

                writeToFile(deckArray.toString());

                ArrayList<String> updatedList = new ArrayList<String>();
                for(int i = 0; i < cardList.length(); i++){
                    updatedList.add(cardList.getString(i));
                }
                return updatedList;

            }
            else{
                return null;
            }

        } catch (JSONException e){
            Log.e("ADDTODECK", e.getMessage());
            return null;
        }
    }

    //1: success, 2: deck full, 3: error
    public int addToDeck(String deck, String card){
        JSONArray deckArray = getDeckJson();
        JSONObject deckObject = null;
        try{
            int pos = -1;
            for(int i = 0; i < deckArray.length(); i++){
                if(deckArray.getJSONObject(i).getString("name").equals(deck)){
                    deckObject = deckArray.getJSONObject(i);
                    pos = i;
                    break;
                }
            }
            if(deckObject != null && pos != -1){
                JSONArray cardList = deckObject.getJSONArray("deckList");
                if(cardList.length() >= 60){
                    return 2;
                }
                else{
                    cardList.put(card);
                    deckObject.put("deckList", cardList);
                    deckArray.put(pos, deckObject);

                    writeToFile(deckArray.toString());
                    return 1;
                }

            }

        } catch(JSONException e){
            Log.e("ADDTODECK", e.getMessage());
        }
        return 3;
    }

    public JSONArray getDeckJson(){
        JSONArray deckArray;
        try{
            //Read the JSON
            Activity a = getActivity();
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
                    deckNamesList.add(deckArray.getJSONObject(i).getString("name"));
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
            //TODO:CLEAN THIS UP
            //do this to check for duplicates but also don't double loop
            if(deckArray != null){
                for(int i = 0; i < deckArray.length(); i++){
                    String deckName = deckArray.getJSONObject(i).getString("name");
                    if(deckName.equals(name)){
                        new AlertDialog.Builder(getActivity())
                                .setMessage(R.string.add_deck_in_use)
                                .setNegativeButton(R.string.alert_close, null)
                                .show();
                        return;
                    }
                    deckNamesList.add(deckName);
                }
                JSONObject newDeckObject = new JSONObject();
                newDeckObject.put("name", name);
                newDeckObject.putOpt("deckList", new JSONArray());
                deckArray.put(newDeckObject);
                deckNamesList.add(name);

                //Saved the file
                writeToFile(deckArray.toString());

                adapter.clear();
                adapter.addAll(deckNamesList);
                adapter.notifyDataSetChanged();
                getListView().invalidateViews();
                getListView().refreshDrawableState();
            }

        } catch (JSONException e) {
            Log.e("ADDDECK", e.getMessage() + " JSON");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, getDeckList());
        setListAdapter(adapter);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_deck_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        getListView().invalidateViews();
        getListView().refreshDrawableState();
    }

    @Override
    public void onListItemClick(ListView l, View v, int pos, long id){
        //TODO: click to view whole list
        JSONArray j = getDeckJson();
        try{
            JSONObject object = j.getJSONObject(pos);
            JSONArray array = object.getJSONArray("deckList");
            ArrayList<String> deckList = new ArrayList<String>();
            for(int i = 0; i < array.length(); i++){
                deckList.add(array.getString(i));
            }
            Intent intent = new Intent(getActivity(), ViewDeckActivity.class);
            intent.putStringArrayListExtra(DECK_LIST_MESSAGE, deckList);
            intent.putExtra(DECK_NAME_MESSAGE, object.getString("name"));
            startActivity(intent);
//            Toast.makeText(getActivity(), j.getJSONObject(pos).toString(), Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            Log.e("DECKITEMCLICKED", e.getMessage());
        }

    }


}
