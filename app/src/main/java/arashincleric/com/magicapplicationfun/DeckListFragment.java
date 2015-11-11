package arashincleric.com.magicapplicationfun;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


/**
 * Fragment that shows list of deck. This fragment also handles all manipulation of the decks
 * such as adding and removing cards and adding and deleting decks
 */
public class DeckListFragment extends ListFragment {

    public final static String DECK_MAIN_MESSAGE = "com.arashincleric.magicapplicationfun.DECKMAIN";
    public final static String DECK_SIDE_MESSAGE = "com.arashincleric.magicapplicationfun.DECKSIDE";
    public final static String DECK_NAME_MESSAGE = "com.arashincleric.magicapplicationfun.DECKNAME";
    private ArrayAdapter<String> adapter;
    private final String FILENAME = "decklist_json";


    public static DeckListFragment newInstance() {
        DeckListFragment fragment = new DeckListFragment();
        Bundle args = new Bundle(); //TODO: need this?
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

    /**
     * Helper method to write contents to a file.
     * @param content String to write to file.
     */
    private void writeToFile(String content){
        try{
            FileOutputStream fos = getActivity().openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fos.write(content.getBytes());
            fos.close();
        } catch (Exception e){
            Log.e("DECKFILE", e.getMessage());
        }
    }

    /**
     * Deletes a deck from the list of decks
     * @param deckName Name of deck to remove
     */
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

    /**
     * Helper method to refresh ListView when data changes
     * TODO: check if the addAll call correctly adds decklist
     */
    public void refreshListView(){
        adapter.clear();
        adapter.addAll(getDeckList());
        adapter.notifyDataSetChanged();
        getListView().invalidateViews();
        getListView().refreshDrawableState();
    }

    //1; success, 2:error

    /**
     * Delete a card from a deck
     * @param deck Deck name to delete from
     * @param card Card name to delete
     * @param inMain <tt>true</tt> if deleting from main, <tt>false</tt> otherwise.
     * @return Response: -1 for error or the card position where it was removed
     */
    public int deleteFromDeck(String deck, String card, boolean inMain){
        JSONArray deckArray = getDeckJson();
        JSONObject deckObject = null;
        try{
            int pos = -1;
            for(int i = 0; i < deckArray.length(); i++){ //Get the deck first
                if(deckArray.getJSONObject(i).getString("name").equals(deck)){
                    deckObject = deckArray.getJSONObject(i);
                    pos = i;
                    break;
                }
            }
            if(deckObject != null && pos != -1){
                JSONObject deckListObj = deckObject.getJSONObject("deckList");
                String whichBoard = inMain ? "main" : "side";
                JSONArray cardList = deckListObj.getJSONArray(whichBoard); //Get list
                int cardPos = -1;
                for(int i = 0; i < cardList.length(); i++){
                    if(cardList.get(i).equals(card)){
                        cardList.remove(i);
                        cardPos = i;
                        break;
                    }
                }
                deckListObj.put(whichBoard, cardList);
                deckObject.put("deckList", deckListObj);
                deckArray.put(pos, deckObject);

                writeToFile(deckArray.toString());

                return cardPos;

            }
            else{
                return -1;
            }

        } catch (JSONException e){
            Log.e("ADDTODECK", e.getMessage());
            return -1;
        }
    }
    
    //0: success, 1: main full, 2: side full, 4: error

    /**
     * Add a card to a deck
     * @param deck Deck name to add to
     * @param card Card name to add
     * @param inMain <tt>true</tt> if adding to main, <tt>false</tt> otherwise
     * @return Response: 0-success, 1-main is full, 2-sideboard is full, 4-error
     */
    public int addToDeck(String deck, String card, boolean inMain){
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
                JSONObject deckListObj = deckObject.getJSONObject("deckList");
                String whichBoard = inMain ? "main" : "side";
                JSONArray cardList = deckListObj.getJSONArray(whichBoard);
                if(inMain && cardList.length() >= 60){ //Check main
                    return 1;
                }
                else if(!inMain && cardList.length() >= 15){ // Check side
                    return 2;
                }
                else{ //Not full so lets add
                    cardList.put(card);
                    //Gross but sorts the list
                    ArrayList<String> sortList = new ArrayList<String>();
                    for(int i = 0; i < cardList.length(); i++){
                        sortList.add(cardList.getString(i));
                    }
                    Collections.sort(sortList);
                    for(int i = 0; i < cardList.length(); i++){
                        cardList.put(i, sortList.get(i));
                    }
                    deckListObj.put(whichBoard, cardList);
                    deckObject.put("deckList", deckListObj);
                    deckArray.put(pos, deckObject);

                    writeToFile(deckArray.toString());
                    return 0;
                }

            }

        } catch(JSONException e){
            Log.e("ADDTODECK", e.getMessage());
        }
        return 4;
    }

    /**
     * Get the JSONArray of all saved decks
     * @return
     */
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

    /**
     * Get list of all deck names
     * @return List of deck names
     */
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

    /** TODO: clean this up!!!
     * Add a deck to the deck list
     * @param name Name of deck to add
     */
    public void addDeck(String name){
        try{
            //This will pass to the adapter for refresh
            ArrayList<String> deckNamesList = new ArrayList<String>();

            //Check if already included
            JSONArray deckArray = getDeckJson();
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
                //Make a new object
                JSONObject newDeckObject = new JSONObject();
                newDeckObject.put("name", name);

                //Make a decklist object with main and sideboard arrays in them
                JSONObject deckListObj = new JSONObject();
                deckListObj.putOpt("main", new JSONArray());
                deckListObj.putOpt("side", new JSONArray());
                newDeckObject.putOpt("deckList", deckListObj);
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
        JSONArray j = getDeckJson();
        try{
            JSONObject object = j.getJSONObject(pos);
            JSONObject deckListObj = object.getJSONObject("deckList");
            JSONArray mainArray = deckListObj.getJSONArray("main");
            JSONArray sideArray = deckListObj.getJSONArray("side");
            ArrayList<String> mainList = new ArrayList<String>();
            for(int i = 0; i < mainArray.length(); i++){
                mainList.add(mainArray.getString(i));
            }

            ArrayList<String> sideList = new ArrayList<String>();
            for(int i = 0; i < sideArray.length(); i++){
                sideList.add(sideArray.getString(i));
            }
            Intent intent = new Intent(getActivity(), ViewDeckActivity.class);
            intent.putStringArrayListExtra(DECK_MAIN_MESSAGE, mainList);
            intent.putStringArrayListExtra(DECK_SIDE_MESSAGE, sideList);
            intent.putExtra(DECK_NAME_MESSAGE, object.getString("name"));
            startActivity(intent);
        } catch (JSONException e) {
            Log.e("DECKITEMCLICKED", e.getMessage());
        }

    }


}
