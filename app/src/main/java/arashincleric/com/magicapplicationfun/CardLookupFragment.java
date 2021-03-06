package arashincleric.com.magicapplicationfun;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.lang3.text.WordUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CardLookupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CardLookupFragment extends ListFragment {


    final class Edition{
        public String edition;
        public String artist;
        public String rarity;
        public String imageURL;
        public Edition(String edition, String artist, String rarity, String imageURL){
            this.edition = edition;
            this.artist = artist;
            this.rarity = rarity;
            this.imageURL = imageURL;
        }
    }

    private static final String[] text = {"Card Name: ", "Mana Cost: ","Converted Mana Cost: ",
            "Types: ", "Card Text: ","Loyalty: ", "Power/Toughness: ", "Edition: ", "Rarity: "
            , "Artist: " };

    private TextView textView;
    private String currentQuery;
    private ArrayAdapter<String> adapter;
    private OnSearchSelectedListener mCallback;
    private Button addCardBtn;
    private String currentCard;
    private ArrayList<Edition> currentCardEditions;
    HashMap<String, String> cardValues;

    private ViewPager viewPager;
    private ViewPagerAdapter pageAdapter;
    private int viewPageIndex; //Used for tracking what info to show

    private ScrollView scrollView;

    ProgressBar progress;
    ProgressDialog progressDialog;

    //Tell activity to clean up search bar when something chosen
    public interface OnSearchSelectedListener {
        public void itemSelected();
        public ArrayList<String> getDeckList();
        public int addToDeck(String deck, String card, boolean isMain);
        public void closeKeyboard();

    }

    public String getTextViewText(){
        return textView.getText().toString();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment CardLookupFragment.
     */
    public static CardLookupFragment newInstance() {
        CardLookupFragment fragment = new CardLookupFragment();
        Bundle args = new Bundle(); //TODO: do i need this?
        fragment.setArguments(args);
        return fragment;
    }

    public CardLookupFragment() {
        // Required empty public constructor
    }

    /**
     * Checks to see if fragment is attached to the main activity. Only the main activity can the
     * card lookup add cards. Otherwise the user is just viewing the card.
     * @return <tt>true</tt> If this fragment is attached to the main activity.
     */
    public boolean isAttachedToMain(){
        return getActivity().getClass().getName().equals(MainActivity.class.getName());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog = new ProgressDialog(getActivity());
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1);
        setListAdapter(adapter);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_card_lookup, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){

        scrollView = (ScrollView)view.findViewById(R.id.scroll_view);

        progress = (ProgressBar)view.findViewById(R.id.progress_bar);
        progress.setVisibility(View.GONE);

        viewPager = (ViewPager)view.findViewById(R.id.pager);
        pageAdapter = new ViewPagerAdapter(getActivity());
        viewPager.setAdapter(pageAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position != viewPageIndex){
                    viewPageIndex = position;
                    setText(position);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        textView = (TextView)view.findViewById(R.id.cardInfo);

        if(!isAttachedToMain()){
            textView.setVisibility(View.GONE);
        }
        addCardBtn = (Button)view.findViewById(R.id.add_button);
        addCardBtn.setVisibility(View.GONE);

        addCardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> decksList = mCallback.getDeckList();
                if(decksList.isEmpty()){
                    new AlertDialog.Builder(getActivity())
                            .setMessage(R.string.alert_no_decks)
                            .setNegativeButton(R.string.alert_close, null)
                            .show();
                }
                else{
                    //Set up ListView for alert dialog
                    final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                            android.R.layout.simple_list_item_1, decksList);
                    final ListView listView = new ListView(getActivity());
                    listView.setAdapter(adapter);
                    final CharSequence a[] = {"main", "side"};
                    final boolean b[] = {false, false}; //false to leave unchecked

                    new AlertDialog.Builder(getActivity())
                            .setTitle(R.string.alert_add_deck_sel)
                            .setNegativeButton(R.string.alert_cancel, null)
                            .setAdapter(adapter, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    final String deck = adapter.getItem(which);
                                    String confirmTemplate = getResources().getString(R.string.alert_confirmation);
                                    String confirmMsg = String.format(confirmTemplate, currentCard, deck);
                                    new AlertDialog.Builder(getActivity())
                                            .setTitle(confirmMsg)
                                            .setMultiChoiceItems(a, b, new DialogInterface.OnMultiChoiceClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                                    if (isChecked) {
                                                        b[which] = true;
                                                    } else {
                                                        b[which] = false;
                                                    }
                                                }
                                            })
                                            .setPositiveButton(R.string.alert_yes, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    int mainResult = -1;
                                                    int sideResult = -1;
                                                    if (b[0]) { //Add to main if necessary
                                                        mainResult = mCallback.addToDeck(deck, currentCard, true);
                                                    }
                                                    if (b[1]) { //Add to sideboard if necessary
                                                        sideResult = mCallback.addToDeck(deck, currentCard, false);
                                                    }

                                                    //Build response string here
                                                    String alertSuccess = getResources().getString(R.string.alert_success);
                                                    String alertFull = getResources().getString(R.string.alert_deck_full);
                                                    String alertError = getResources().getString(R.string.alert_error);
                                                    String mainMsg = "";
                                                    String sideMsg = "";
                                                    switch (mainResult) {
                                                        case 0:
                                                            mainMsg = String.format(alertSuccess, deck + "(Main)");
                                                            break;
                                                        case 1:
                                                            mainMsg = String.format(alertFull, deck + "(Main)");
                                                            break;
                                                        case 4:
                                                            mainMsg = String.format(alertError, deck + "(Main)");
                                                            break;
                                                    }
                                                    switch (sideResult) {
                                                        case 0:
                                                            sideMsg = String.format(alertSuccess, deck + "(Side)");
                                                            break;
                                                        case 2:
                                                            sideMsg = String.format(alertFull, deck + "(Side)");
                                                            break;
                                                        case 4:
                                                            sideMsg = String.format(alertError, deck + "(Side)");
                                                            break;
                                                    }
                                                    new AlertDialog.Builder(getActivity())
                                                            .setMessage(mainMsg + '\n' + sideMsg)
                                                            .setNegativeButton(R.string.alert_close, null)
                                                            .show();
                                                }
                                            })
                                            .setNegativeButton(R.string.alert_cancel, null)
                                            .show();
                                }
                            })
                            .show();
                }
            }
        });
    }

    /**
     * Sends a query to Deckbrew API to find possible matches.
     * @param query The string of characters to enter to search
     */
    public void getAutoComplete(String query){
        String urlQuery = query.toLowerCase().trim().replaceAll("\\s+", "%20")
                .replaceAll("[,]", "%2C")
                .replaceAll("[.]", "%2E")
                .replaceAll("[;]", "%3B")
                .replaceAll("[:]", "%3A")
                .replaceAll("[']", "%27")
                .replaceAll("[`]", "%60");
        String url = "https://api.deckbrew.com/mtg/cards/typeahead?q=" + urlQuery;
        //Hide all of this to show ListView
        scrollView.setVisibility(View.GONE);
        viewPager.setVisibility(View.GONE);
        textView.setVisibility(View.GONE);
        addCardBtn.setVisibility(View.GONE);
        getListView().setVisibility(View.VISIBLE);
        new DownloadSuggestionTask().execute(url);
    }

    @Override
    public void onListItemClick(ListView l, View v, int pos, long id){
        String query = (String)l.getItemAtPosition(pos);
        testConnection(query);
        l.setVisibility(View.GONE);
        mCallback.itemSelected();

    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        if(isAttachedToMain()){ //Check if attaching activity implemented interface
            try{
                mCallback = (OnSearchSelectedListener) activity;
            } catch (ClassCastException e){
                throw new ClassCastException(activity.toString()
                        + " must implement OnSearchSelectedListener");
            }
        }
    }

    /**
     * Sends request to Deckbrew API to retrieve card JSON.
     * @param q Name of card to look up.
     */
    public void testConnection(@Nullable String q){
        if(q == null){ //TODO: I think this was an older idea
            return;
        }
        //Remove spaces and special characters
        currentQuery = q.toLowerCase().trim().replaceAll("\\s+", "-").replaceAll("[,.;:'`]", "");
        String url = "https://api.deckbrew.com/mtg/cards/" + currentQuery;
        ConnectivityManager connMgr = (ConnectivityManager)
                getActivity().getSystemService(getActivity().CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        //If we have a network connection then send request
        if(networkInfo != null && networkInfo.isConnected()){
            if(mCallback != null){
                mCallback.closeKeyboard(); //Testing purposes
            }
            progressDialog.setMessage("Retrieving");
            progressDialog.show();
            AsyncTask task = new DownloadWebpageTask().execute(url);
            progress.setVisibility(View.VISIBLE);
            try{
                task.get();
            }
            catch(Exception e){
                Log.e("DOWNLOADDATA", e.getStackTrace().toString());
            }
        }
        else{
            Toast.makeText(getActivity(),"No connection", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Async task to look up a card
     */
    private class DownloadWebpageTask extends AsyncTask<String,Void,String> {

        @Override
        protected void onPreExecute(){
            progressDialog.setMessage("Retrieving");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... urls){
            try{
                return downloadUrl(urls[0]);
            }
            catch (IOException e){
                Log.i("CARDLOOKUP", "Blank response, checking suggestions");
                return "";
            }
        }

        @Override
        protected void onPostExecute(String results){

            //If empty there was an incomplete search so try to find suggestions
            if(results.isEmpty()){
                String url = "https://api.deckbrew.com/mtg/cards/typeahead?q=" + currentQuery;
                new DownloadSuggestionTask().execute(url);
            }
            else{ //Otherwise just print the stats
                printStats(results);
                //TODO: add progress bar that works
                AsyncTask task = new DownloadImages().execute(currentCardEditions);
//                new DownloadImage().execute(results);
                viewPager.setVisibility(View.VISIBLE);
                textView.setVisibility(View.VISIBLE);
                if(!isAttachedToMain()){
                    addCardBtn.setVisibility(View.GONE);
                }
                else{
                    addCardBtn.setVisibility(View.VISIBLE);
                }
                try{
                    task.get(5000, TimeUnit.MILLISECONDS);
                    scrollView.setVisibility(View.VISIBLE);
                }
                catch(Exception e){
                    Log.e("DOWNLOADDATA", e.getStackTrace().toString());
                }
            }

        }
    }


    /**
     * Async task to look up possible matches given a string of characters
     */
    private class DownloadSuggestionTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            try{
                return downloadUrl(urls[0]);
            } catch(IOException e){
                Log.i("CARDLOOKUP", "Blank response for suggestions");
                return "";
            }
        }

        @Override
        protected void onPostExecute(String results){
            JSONArray jsonArray;
            try{ //Grab all the names of response JSON
                jsonArray = new JSONArray(results);
                ArrayList<String> nameList = new ArrayList<String>();

                for(int i = 0; i < jsonArray.length(); i++){
                    nameList.add(jsonArray.getJSONObject(i).getString("name"));
                }
                adapter.clear();
                adapter.addAll(nameList);
                adapter.notifyDataSetChanged();
                getListView().invalidateViews();
                getListView().refreshDrawableState();

//                textView.setText(suggestions);
            }
            catch(JSONException e){
                Log.e("CARDLOOKUP", "JSON NOT PARSED(Suggestions)");
            }

        }
    }

    /**
     * Clears ListView of all entries
     */
    public void clearList(){
        adapter.clear();
        adapter.notifyDataSetChanged();
        getListView().invalidateViews();
        getListView().refreshDrawableState();
    }

    /**
     * Async task to get card image.
     */
    private class DownloadImage extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... urls){
            try {
                return getImageBitmap(urls[0]);
            } catch(IOException e) {
                Log.e("IMAGE", "COULD NOT OPEN IMAGE URL");
            } catch(JSONException e) {
                Log.e("IMAGE", "COULD NOT PARSE JSON");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bmp){

        }
    }

    private class DownloadImages extends AsyncTask<ArrayList<Edition>, Void, ArrayList<Bitmap>>{
        @Override
        protected ArrayList<Bitmap> doInBackground(ArrayList<Edition>... urls){
            try {
                return getImageBitmaps(urls[0]);
            } catch(IOException e) {
                Log.e("IMAGE", "COULD NOT OPEN IMAGE URL");
            } catch(JSONException e) {
                Log.e("IMAGE", "COULD NOT PARSE JSON");
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Bitmap> bitmaps){
            if(bitmaps != null){
                pageAdapter.updateView(bitmaps);
            }
            progress.setVisibility(View.GONE);
            if(progressDialog.isShowing()){
                progressDialog.dismiss();
            }
        }
    }


    /**
     * Sends a request and retrieves response from Deckbrew API
     * @param myurl The URL to get query
     * @return JSON response
     * @throws IOException
     */
    private String downloadUrl(String myurl) throws IOException{
        InputStream is = null;
        try{
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);

            conn.connect();
            int response = conn.getResponseCode();
            Log.i("URL", "Response: " + response);
            is = conn.getInputStream();

            return readIt(is);
        } finally {
            if (is != null){
                is.close();
            }
        }
    }

    /**
     * Reads the input stream and convert to a string
     * @param is Input stream from query response
     * @return String of JSON response
     * @throws IOException
     */
    public String readIt(InputStream is)throws IOException {
        Reader reader;
        reader = new InputStreamReader(is, "UTF-8");
        BufferedReader bufferedReader = new BufferedReader(reader);
        StringBuilder stringBuilder = new StringBuilder();
        String line;

        while((line = bufferedReader.readLine()) != null){
            stringBuilder.append(line);
        }

        return stringBuilder.toString();
    }

    /**
     * Grabs relevant information of the card to display on screen.
     * @param results JSON string of response
     */
    public void printStats(String results){
        JSONObject stats;
        try{
            stats = new JSONObject(results);
            String cardNameText = stats.getString("name");
            currentCard = cardNameText;
            JSONArray types = stats.getJSONArray("types");
            String cardType;
            //Card shouldn't be more than 2 types
            if (types.length() > 1){ //Get the second field b/c it will show us the real basic type
                cardType = types.getString(1);
            }
            else{
                cardType = types.getString(0);
            }

            cardValues = new HashMap<String, String>();
            cardValues.put(text[0], cardNameText); //Name

            if(cardType.equals("land")){
                cardValues.put(text[1], null); //Cost
                cardValues.put(text[2], null); //CMC
            }
            else{
                cardValues.put(text[1], stats.getString("cost")); //Cost
                cardValues.put(text[2], stats.getString("cmc")); //CMC
            }

            StringBuilder cardTypesText = new StringBuilder(types.toString().replaceAll("\\[|\\]|,", ""));
            if(stats.has("supertypes")){
                cardTypesText.insert(0, stats.getString("supertypes")
                        .replaceAll("\\[|\\]", "").replaceAll(",", " ") + " ");

            }
            if(stats.has("subtypes")){
                cardTypesText.append(" - " + stats.getString("subtypes")
                        .replaceAll("\\[|\\]", "").replaceAll(",", " "));

            }
            cardValues.put(text[3], WordUtils.capitalize(cardTypesText.toString().replaceAll("\"", ""))); //Types

            if(cardType.equals("land")){
                cardValues.put(text[4], null); //Text
            }
            else{
                BufferedReader reader = new BufferedReader(new StringReader(stats.getString("text")));
                String line = null;
                StringBuilder cardTextBuilder = new StringBuilder();
                try{
                    while ((line = reader.readLine()) != null){
                        cardTextBuilder.append("<br />" + "<i>" + line + "</i>");
                    }
                } catch(IOException e){
                    Log.e("GETTEXTCARD", e.getStackTrace().toString());
                }
//                String cardText = cardTextBuilder.toString();
//                cardText = cardText.toString().replace("\n", "<br />");
                cardValues.put(text[4], cardTextBuilder.toString()); //Text
            }

            if(cardType.equals("planeswalker")){ //Loyalty
                cardValues.put(text[5], stats.getString("loyalty"));
            }
            else{
                cardValues.put(text[5], null);
            }

            if(cardType.equals("creature")){ //P/T
                cardValues.put(text[6], stats.getString("power") + "/" + stats.getString("toughness"));
            }
            else{
                cardValues.put(text[6], null);
            }

            JSONArray editionsList = stats.getJSONArray("editions"); //Get all editions here
            currentCardEditions = new ArrayList<Edition>();
            for(int i = 0; i < editionsList.length(); i++){
                JSONObject editionObj = editionsList.getJSONObject(i);
                if(editionObj.getString("set").equals("Prerelease Events")){
                    continue; //Skip this because pointless to add
                }
                currentCardEditions.add(new Edition(editionObj.getString("set"),
                        editionObj.getString("artist"), editionObj.getString("rarity"),
                        editionObj.getString("image_url")));

            }

            cardValues.put(text[7], currentCardEditions.get(0).edition); //Edition
            cardValues.put(text[8], currentCardEditions.get(0).rarity); //Rarity
            cardValues.put(text[9], currentCardEditions.get(0).artist); //Artist


            StringBuilder displayText = new StringBuilder();
            for(int i = 0; i < text.length; i++){ //Go through and display what is needed
                if(cardValues.get(text[i]) != null){
                    displayText.append("<b>" + text[i] + "</b>" + cardValues.get(text[i]) + "<br />");
                }
            }

            textView.setText(Html.fromHtml(displayText.toString()));
        } catch (JSONException e){
            Log.e("CARDLOOKUP", "JSON NOT PARSED(PrintStats)");
        }

    }

    //TODO:CLEAN THIS UP
    public void setText(int pos){
        cardValues.put(text[7], currentCardEditions.get(pos).edition); //Edition
        cardValues.put(text[8], currentCardEditions.get(pos).rarity); //Rarity
        cardValues.put(text[9], currentCardEditions.get(pos).artist); //Artist
        StringBuilder displayText = new StringBuilder();
        for(int i = 0; i < text.length; i++){ //Go through and display what is needed
            if(cardValues.get(text[i]) != null){
                displayText.append("<b>" + text[i] + "</b>" + cardValues.get(text[i]) + "<br />");
            }
        }

        textView.setText(Html.fromHtml(displayText.toString()));
    }

    /**
     * Gets the image provided by the JSON response
     * @param results JSON response
     * @return Bitmap stream of response for the image
     * @throws JSONException
     * @throws IOException
     */
    public Bitmap getImageBitmap(String results) throws JSONException, IOException{
        JSONObject stats;
        stats = new JSONObject(results);
        JSONArray sets = stats.getJSONArray("editions");
        String imageURL = null;
        //Loop until we can find an actual image (ie Prerelease cards have no image)
        for(int i = 0; i < sets.length(); i++){
            imageURL = sets.getJSONObject(i).getString("image_url");
            if(!sets.getJSONObject(i).getString("set").equals("Prerelease Events")){
                break;
            }
        }

        URL image = new URL(imageURL);
        return BitmapFactory.decodeStream(image.openConnection().getInputStream());
    }

    public ArrayList<Bitmap> getImageBitmaps(ArrayList<Edition> editions) throws JSONException, IOException{
        ArrayList<Bitmap> images = new ArrayList<Bitmap>();
        for(int i = 0; i < editions.size(); i++){
            String imageURL = editions.get(i).imageURL;
            URL image = new URL(imageURL);
            images.add(BitmapFactory.decodeStream(image.openConnection().getInputStream()));
        }

        return images;
    }

}
