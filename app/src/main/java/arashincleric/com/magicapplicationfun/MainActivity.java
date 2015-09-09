package arashincleric.com.magicapplicationfun;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
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
    private static final String ARG_SCORE = "SCORE";
    private ListView mDrawerList;

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private Fragment mContent;
    private Menu mOptionsMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerList = (ListView)findViewById(R.id.navList);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        setTitle(R.string.application_title);

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

            //Show or hid new game buttons accordingly
            switch(sel){
                case ARG_LIFE_COUNTER_FRAGMENT:
                    mOptionsMenu.findItem(R.id.new_game).setVisible(true);
                    break;
                default:
                    mOptionsMenu.findItem(R.id.new_game).setVisible(false);
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
    }

    private void setupDrawer() {
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
        return true;
    }

    //Sync state of nav drawer when activity created
    @Override
    protected void onPostCreate(Bundle savedInstanceState){
        super.onPostCreate(savedInstanceState);

        ScoreFragment scoreFragment = (ScoreFragment)getSupportFragmentManager()
                .findFragmentByTag(ARG_LIFE_COUNTER_FRAGMENT);

        //Pull previously saved score
        if(savedInstanceState != null && scoreFragment != null){
            String savedScore = savedInstanceState.getString(ARG_SCORE, "20");
            scoreFragment.setScoreView(savedScore);
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
                //TODO: Fragment logic here
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
        MenuItem item = menu.findItem(R.id.new_game);
        ScoreFragment scoreFragment = (ScoreFragment)getSupportFragmentManager()
                .findFragmentByTag(ARG_LIFE_COUNTER_FRAGMENT);
        if(scoreFragment == null || !scoreFragment.isVisible()){
            item.setVisible(false);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

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
        SharedPreferences sharedPrefs = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.clear();
        editor.commit();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);

        ScoreFragment scoreFragment = (ScoreFragment)getSupportFragmentManager()
                .findFragmentByTag(ARG_LIFE_COUNTER_FRAGMENT);
        if(scoreFragment != null){
            int scoreToSave = scoreFragment.getScore();

            savedInstanceState.putString(ARG_SCORE, Integer.toString(scoreToSave));
        }
    }
}
