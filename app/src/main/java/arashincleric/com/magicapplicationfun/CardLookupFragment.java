package arashincleric.com.magicapplicationfun;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CardLookupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CardLookupFragment extends ListFragment {


    private ImageView cardImage;
    private TextView textView;
    private String currentQuery;
    private ArrayAdapter<String> adapter;
    private OnSearchSelectedListener mCallback;
    private Button addCardBtn;
    private String currentCard;

    //Tell activity to clean up search bar when something chosen
    public interface OnSearchSelectedListener {
        public void itemSelected();
        public ArrayList<String> getDeckList();
        public int addToDeck(String deck, String card);

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment CardLookupFragment.
     */
    public static CardLookupFragment newInstance() {
        CardLookupFragment fragment = new CardLookupFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public CardLookupFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        cardImage = (ImageView)view.findViewById(R.id.cardImage);

        textView = (TextView)view.findViewById(R.id.cardInfo);

        addCardBtn = (Button)view.findViewById(R.id.add_button);
        addCardBtn.setVisibility(View.GONE);

        addCardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO:AlertDIalog to add to deck
                ArrayList<String> decksList = mCallback.getDeckList();
                final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_list_item_1, decksList);
                final ListView listView = new ListView(getActivity());
                listView.setAdapter(adapter);

                new AlertDialog.Builder(getActivity())
                        .setTitle("Add card to which deck?")
                        .setNegativeButton("Cancel", null)
//                                .setView(listView)
                        .setAdapter(adapter, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final String deck = adapter.getItem(which);
                                new AlertDialog.Builder(getActivity())
                                        .setMessage("Are you sure?")
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                int i = mCallback.addToDeck(deck, currentCard);
                                                switch (i) {
                                                    case 1:
                                                        new AlertDialog.Builder(getActivity())
                                                                .setMessage("Success")
                                                                .setNegativeButton("Close", null)
                                                                .show();
                                                        break;
                                                    case 2:
                                                        new AlertDialog.Builder(getActivity())
                                                                .setMessage("Deck list full")
                                                                .setNegativeButton("Close", null)
                                                                .show();
                                                        break;
                                                    default:
                                                        new AlertDialog.Builder(getActivity())
                                                                .setMessage("Error adding card")
                                                                .setNegativeButton("Close", null)
                                                                .show();
                                                        break;
                                                }
                                            }
                                        })
                                        .setNegativeButton("Cancel", null)
                                        .show();
                            }
                        })
                        .show();


            }
        });
    }

    public void getAutoComplete(String query){
        String url = "https://api.deckbrew.com/mtg/cards/typeahead?q=" + query;
        cardImage.setVisibility(View.GONE); //Clear image if present
        textView.setVisibility(View.GONE);
        getListView().setVisibility(View.VISIBLE);
        new DownloadSuggestionTask().execute(url);

    }

    @Override
    public void onListItemClick(ListView l, View v, int pos, long id){
        //TODO: click to view whole list
        String query = (String)l.getItemAtPosition(pos);
        testConnection(query);
        l.setVisibility(View.GONE);
        mCallback.itemSelected();

    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        try{
            mCallback = (OnSearchSelectedListener) activity;
        } catch (ClassCastException e){
            throw new ClassCastException(activity.toString()
                    + " must implement OnSearchSelectedListener");
        }
    }

    public void testConnection(@Nullable String q){
        if(q == null){
            return;
        }
        //get rid of spaces
        currentQuery = q.toLowerCase().trim().replaceAll("\\s+", "-").replaceAll("[,.;:'`]", "");
        String url = "https://api.deckbrew.com/mtg/cards/" + currentQuery;
        ConnectivityManager connMgr = (ConnectivityManager)
                getActivity().getSystemService(getActivity().CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()){
            new DownloadWebpageTask().execute(url);
        }
        else{
            Toast.makeText(getActivity(),"No connection", Toast.LENGTH_LONG).show();
        }
    }

    //Async task to lookup a card
    private class DownloadWebpageTask extends AsyncTask<String,Void,String> {
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
                cardImage.setImageResource(0); //Clear image if present
                new DownloadSuggestionTask().execute(url);
            }
            else{ //Otherwise just print the stats
                printStats(results);
                new DownloadImage().execute(results);
                cardImage.setVisibility(View.VISIBLE);
                textView.setVisibility(View.VISIBLE);
                addCardBtn.setVisibility(View.VISIBLE);
            }

        }
    }

    //Async task to check suggestions
    private class DownloadSuggestionTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            try{
                return downloadUrl(urls[0]);
            }
            catch(IOException e){
                Log.i("CARDLOOKUP", "Blank response for suggestions");
                return "";
            }
        }

        @Override
        protected void onPostExecute(String results){
            String s = results;
            JSONArray jsonArray;
            try{
                jsonArray = new JSONArray(results);
                ArrayList<String> nameList = new ArrayList<String>();

//                StringBuilder suggestions = new StringBuilder("Did you mean: \n");
                for(int i = 0; i < jsonArray.length(); i++){
//                    suggestions.append('\t' + jsonArray.getJSONObject(i).getString("name") + '\n');
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

    public void clearList(){
        adapter.clear();
        adapter.notifyDataSetChanged();
        getListView().invalidateViews();
        getListView().refreshDrawableState();
    }

    //Async task to get card image
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
            if(bmp != null){
                cardImage.setImageBitmap(bmp);
            }
        }
    }

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

    public void printStats(String results){
        JSONObject stats;
        try{
            stats = new JSONObject(results);
            //TODO: specify what info to print and remove brackets in some values
            currentCard = stats.getString("name");
            String name = stats.getString("name") + '\n';
            String types = stats.getString("types") + '\n';
            String subtypes = stats.getString("subtypes") + '\n';
            JSONArray sets = stats.getJSONArray("editions");
            StringBuilder setList = new StringBuilder();
            for(int i = 0; i < sets.length(); i++){
                setList.append(sets.getJSONObject(i).getString("set"));
            }

            textView.setText(name + types + subtypes + setList.toString());
        } catch (JSONException e){
            Log.e("CARDLOOKUP", "JSON NOT PARSED(PrintStats)");
        }

    }

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

}
