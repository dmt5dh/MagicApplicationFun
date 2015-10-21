package arashincleric.com.magicapplicationfun;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class MainActivity extends AppCompatActivity {

    private final String ARG_LIFE_COUNTER_FRAGMENT = "LifeCounter";
    private final String ARG_CARD_LOOKUP = "CardLookup";
    private final String ARG_CARD_DECKLIST= "Decklist";
    private final String ARG_CURRENT_FRAGMENT= "CurrentFragment";
    private static final String ARG_SCORE = "SCORE";
    private ListView mDrawerList;
    private String curFragName;

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private Fragment mContent;
    private Menu mOptionsMenu;

    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerList = (ListView)findViewById(R.id.navList);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
//        setTitle(R.string.application_title);
        setTitle("");

        //If something already present
        if(findViewById(R.id.fragment_container) != null) {
            if(savedInstanceState != null){
                return;
            }
        }
    }

    private void switchFragment(String sel){
        //Check to see if selected fragment is currently visible
        if(mContent == null || !mContent.getTag().equals(sel)){
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            //Try to find the fragment
            Fragment fragment = fragmentManager.findFragmentByTag(sel);

            //detach current attached fragment
            if(mContent != null){
                transaction.detach(mContent);
            }

            //Make new instance of fragment if not before
            if(fragment == null){
                switch (sel) {
                    case ARG_LIFE_COUNTER_FRAGMENT:
                        mContent = ScoreFragment.newInstance();
                        break;
                    case ARG_CARD_LOOKUP:
                        mContent = CardLookupFragment.newInstance();
                        break;
                    case ARG_CARD_DECKLIST:
                        mContent = DeckListFragment.newInstance();
                        break;
                    default:
                        mContent = ScoreFragment.newInstance();
                }
            }
            else {
                mContent = fragment;
            }
            if(mContent.isAdded() || mContent.isDetached()) {
                transaction.attach(mContent);
            }
            else{
                transaction.add(R.id.fragment_container, mContent, sel);
            }

            transaction.commit();
        }
        curFragName = sel;
    }

    private void setupDrawer() {
        //Setup the actions to take when opening/closing nav drawer
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.drawer_open, R.string.drawer_close) {
            /** Called when drawer has settled completely open **/
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if(getSupportActionBar() != null){
                    getSupportActionBar().setTitle(R.string.navigation_title);
                    invalidateOptionsMenu();
                }
            }

            /** Called When drawer has settled completely closed **/
            public void onDrawerClosed(View view){
                super.onDrawerClosed(view);
                if(getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(R.string.application_title);
                }
                invalidateOptionsMenu();
            }
        };

        //actually enable toggle
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    //Associate the data to the listview in nav drawer
    private void addDrawerItems(){
        String[] navListArray = {"Life Counter", "Card Lookup", "Decklist"};
        ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, navListArray);
        mDrawerList.setAdapter(mAdapter);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mOptionsMenu = menu;

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        //Sends search query to search fragment
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //TODO: Send to cardlookup fragment
                //TODO: clean this up
                CardLookupFragment c = (CardLookupFragment)mContent;
                c.testConnection(query);
                return true; //Set to true so we don't fire off intent
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //TODO: Implement autocomplete when possible
                return true;
            }
        });

        return true;
    }

    //Sync state of nav drawer when activity created
    @Override
    protected void onPostCreate(Bundle savedInstanceState){
        super.onPostCreate(savedInstanceState);

        ScoreFragment scoreFragment = (ScoreFragment)getSupportFragmentManager()
                .findFragmentByTag(ARG_LIFE_COUNTER_FRAGMENT);

        //Pull previously saved score
        if(savedInstanceState != null){
            curFragName = savedInstanceState.getString(ARG_CURRENT_FRAGMENT, null);

            if(curFragName != null){
                mContent = getSupportFragmentManager().findFragmentByTag(curFragName);
            }

            if(scoreFragment != null){
                String savedScore = savedInstanceState.getString(ARG_SCORE, "20");
                scoreFragment.setScoreView(savedScore);
            }
        }

        //Set up navigation drawer here to handle configuration change
        addDrawerItems();
        setupDrawer();
        //Add hamburger icon on action bar
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        switchFragment(ARG_LIFE_COUNTER_FRAGMENT);
                        break;
                    case 1:
                        switchFragment(ARG_CARD_LOOKUP);
                        break;
                    case 2:
                        switchFragment(ARG_CARD_DECKLIST);
                        break;
                    default:
                        break;
                }
                mDrawerLayout.closeDrawers();
            }
        });

        mDrawerToggle.syncState();

    }

    //Sync state of nav drawer on config change
    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    //Hide override settings
    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        //Clear the options menu
        for(int i = 0; i < menu.size(); i++){
            menu.getItem(i).setVisible(false);
        }
        //Set the menu items according to fragment chosen
        if(mContent != null){
            switch(curFragName){
                case ARG_LIFE_COUNTER_FRAGMENT:
                    menu.findItem(R.id.new_game).setVisible(true);
                    break;
                case ARG_CARD_LOOKUP:
                    menu.findItem(R.id.search).setVisible(true);
                    break;
            }
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //Show an alert when user wnats to play a new game
        if (id == R.id.new_game) {
            final ScoreFragment scoreFragment = (ScoreFragment)getSupportFragmentManager()
                    .findFragmentByTag(ARG_LIFE_COUNTER_FRAGMENT);
            if(scoreFragment != null){
                //If the fragment was created
                new AlertDialog.Builder(this)
                        .setMessage(R.string.new_game_confirm)
                        .setPositiveButton(R.string.replay_yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                scoreFragment.resetScore();
                            }
                        })
                        .setNegativeButton(R.string.replay_no, null).show();
            }
            return true;
        }

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop(){
        super.onStop();
        //Clear the score if user quits app
        SharedPreferences sharedPrefs = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.clear();
        editor.commit();
    }

    //Save the score on config change
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);

        ScoreFragment scoreFragment = (ScoreFragment)getSupportFragmentManager()
                .findFragmentByTag(ARG_LIFE_COUNTER_FRAGMENT);
        if(scoreFragment != null){
            int scoreToSave = scoreFragment.getScore();

            savedInstanceState.putString(ARG_SCORE, Integer.toString(scoreToSave));
        }

        if(mContent != null){
            savedInstanceState.putString(ARG_CURRENT_FRAGMENT, curFragName);
        }
    }
}
