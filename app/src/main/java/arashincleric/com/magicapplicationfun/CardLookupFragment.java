package arashincleric.com.magicapplicationfun;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CardLookupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CardLookupFragment extends Fragment {


    private EditText searchText;
    private Handler handler = new Handler();
    private Button submitBtn;
    private ImageView cardImage;
    private TextView textView;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_card_lookup, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        searchText = (EditText)view.findViewById(R.id.searchText);
        searchText.addTextChangedListener(textWatcher);

        cardImage = (ImageView)view.findViewById(R.id.imageView);

        textView = (TextView)view.findViewById(R.id.textView);

        submitBtn = (Button)view.findViewById(R.id.button);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testConnection(null);
            }
        });
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            handler.removeCallbacks(runnable);
        }

        @Override
        public void afterTextChanged(Editable s) {

//            handler.postDelayed(runnable, 300);
        }
    };

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            getTypeAhead(searchText.getText().toString());
        }
    };

    public void getTypeAhead(String text){
        Toast.makeText(getActivity(),text, Toast.LENGTH_SHORT).show();
    }

    public void testConnection(@Nullable String q){

//        String query = searchText.getText().toString().toLowerCase().replaceAll(" ", "-");
        if(q.equals(null)){
            return;
        }
        String query = q.toLowerCase().replaceAll(" ", "-");
        String url = "https://api.deckbrew.com/mtg/cards/" + query;
        ConnectivityManager connMgr = (ConnectivityManager)
                getActivity().getSystemService(getActivity().CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()){
            new DownloadWebpageTask().execute(url);
        }
        else{
            Toast.makeText(getActivity(),"not connected", Toast.LENGTH_SHORT).show();
        }
    }

    private class DownloadWebpageTask extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String... urls){
            try{
                return downloadUrl(urls[0]);
            }
            catch (IOException e){
                return "unable to retrieve webpage";
            }
        }

        @Override
        protected void onPostExecute(String results){

            printStats(results);
            new DownloadImage().execute(results);
        }
    }

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
        int len = 500;
        try{
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);

            conn.connect();
            int response = conn.getResponseCode();
            is = conn.getInputStream();

            String contentString = readIt(is,len);
            return contentString;
        } finally {
            if (is != null){
                is.close();
            }
        }
    }

    public String readIt(InputStream is, int len)throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(is, "UTF-8");
        BufferedReader bufferedReader = new BufferedReader(reader);
        StringBuilder stringBuilder = new StringBuilder();
        String line = null;

        while((line = bufferedReader.readLine()) != null){
            stringBuilder.append(line + '\n');
        }

        return stringBuilder.toString();
    }

    public void printStats(String results){
//        textView.setText(results);
        JSONObject stats;
        try{
            stats = new JSONObject(results);
            Log.e("JSON", stats.toString(8));
            String name = stats.getString("name") + '\n';
            String types = stats.getString("types") + '\n';
            String supertypes = stats.getString("subtypes") + '\n';
            JSONArray sets = stats.getJSONArray("editions");
            StringBuilder setList = new StringBuilder();
            for(int i = 0; i < sets.length(); i++){
                setList.append(sets.getJSONObject(i).getString("set"));
            }

            textView.setText(name + types + supertypes + setList.toString());
        } catch (JSONException e){
            Log.e("ERROR", "JSON NOT PARSED");
        }

    }

    public Bitmap getImageBitmap(String results) throws JSONException, IOException{
        JSONObject stats;
        stats = new JSONObject(results);
        JSONArray sets = stats.getJSONArray("editions");
        String imageURL = null;
        for(int i = 0; i < sets.length(); i++){
            imageURL = sets.getJSONObject(i).getString("image_url");
            if(!sets.getJSONObject(i).getString("set").equals("Prerelease Events")){
                break;
            }
        }

        URL image = new URL(imageURL);
        Log.e("IMAGE", imageURL);
        Bitmap bmp = BitmapFactory.decodeStream(image.openConnection().getInputStream());
        return bmp;
    }

}
