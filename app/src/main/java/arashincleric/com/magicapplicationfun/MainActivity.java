package arashincleric.com.magicapplicationfun;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements CardLookupFragment.OnSearchSelectedListener {

    public final String ARG_LIFE_COUNTER_FRAGMENT = "LifeCounter";
    public final String ARG_CARD_LOOKUP = "CardLookup";
    public final String ARG_CARD_DECKLIST= "Decklist";
    public final String ARG_CURRENT_FRAGMENT= "CurrentFragment";
    private static final String ARG_SCORE = "SCORE";
    private ListView mDrawerList;
    private String curFragName;

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private Fragment mContent;
    private Menu mOptionsMenu;

    private ArrayList<Fragment> fragmentStack = new ArrayList<Fragment>(); //Easier to deal with than backstack

    private SearchView searchView;

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            CardLookupFragment c = (CardLookupFragment)mContent;
            c.getAutoComplete(searchView.getQuery().toString());
        }
    };

    public Fragment getFragment(){
        return mContent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerList = (ListView)findViewById(R.id.navList);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        setTitle(R.string.application_title);

        //If something already present
        //TODO: why is this here?
        if(findViewById(R.id.fragment_container) != null) {
            if(savedInstanceState != null){
                return;
            }
        }
    }

    //Override the back logic to support back button for fragments
    @Override
    public void onBackPressed(){
        if(mDrawerLayout.isDrawerOpen(GravityCompat.START)){ //Close nav drawer if open
            mDrawerLayout.closeDrawers();
        }
        else if (fragmentStack.size() == 1){ //If no more fragments exit app
            finish();
        }
        else if (fragmentStack.size() > 1) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.hide(mContent);
            fragmentStack.remove(fragmentStack.size() - 1);
            mContent = fragmentStack.get(fragmentStack.size() - 1);
            transaction.show(mContent);
            transaction.commit();
            curFragName = mContent.getTag();
            onPrepareOptionsMenu(mOptionsMenu);
        }
        else { //Why is this here? idk just in case...
            super.onBackPressed();
        }
    }

    /**
     * Gets the current fragment attached to activity
     * @return Fragment current on activity
     */
    private Fragment getCurrentFragment(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        String fragmentName = fragmentManager
                .getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1).getName();
        return fragmentManager.findFragmentByTag(fragmentName);
    }

    /**
     * Switches fragments in the main activity.
     * @param sel Argument string for which fragment to show
     */
    public void switchFragment(String sel){
        //Check to see if selected fragment is currently visible
        if(mContent == null || !mContent.getTag().equals(sel)){
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            //Try to find the fragment
            Fragment fragment = fragmentManager.findFragmentByTag(sel);

            //Hide current attached fragment
            if(mContent != null){
                transaction.hide(mContent);
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
//                transaction.addToBackStack(sel);
                fragmentStack.add(mContent);
            }
            else {
                mContent = fragment;
                if(fragmentStack.size() >= 3) { //Get the fragment and put on top of stack but retain rest of stack
                    fragmentStack.remove(fragmentStack.indexOf(mContent));
                    fragmentStack.add(mContent);

                }
                else{
                    fragmentStack.add(mContent);
                }
            }
            if(mContent.isAdded() || mContent.isDetached()) {
                transaction.show(mContent);
            }
            else{
                transaction.add(R.id.fragment_container, mContent, sel);
            }

            transaction.commit();
        }
        curFragName = sel;
    }

    /**
     * Used to setup the navigation drawer
     */
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

    /**
     * Add list of items to show on the navigation drawer
     */
    private void addDrawerItems(){
        String[] navListArray = getResources().getStringArray(R.array.nav_drawer_menu);
        ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, navListArray);
        mDrawerList.setAdapter(mAdapter);
    }

    public void closeKeyboard(){
        if(getCurrentFocus() != null){
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
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
            public boolean onQueryTextSubmit(String query) { //Close search and show results
                mOptionsMenu.findItem(R.id.search).collapseActionView();
                return true; //Set to true so we don't fire off intent
            }

            @Override
            public boolean onQueryTextChange(String newText) { //After 1 second do an autocomplete
                if (!newText.isEmpty()) {
                    handler.removeCallbacks(runnable);
                    handler.postDelayed(runnable, 100);
                }
                else{
                    if(mContent != null && getSupportFragmentManager().findFragmentByTag(ARG_CARD_LOOKUP) != null
                            && getSupportFragmentManager().findFragmentByTag(ARG_CARD_LOOKUP).isVisible()
                            && mContent.getClass() == CardLookupFragment.class
                            && ((CardLookupFragment)mContent).getListView().getCount() == 0){
                        ((CardLookupFragment) mContent).clearList();
                    }
                }

                return true;
            }
        });

        //Back button press to collapse searchView and clear list
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    mOptionsMenu.findItem(R.id.search).collapseActionView();
//                    searchView.setQuery("", false);
//                    CardLookupFragment c = (CardLookupFragment) mContent;
//                    c.clearList();
                }
            }
        });

        return true;
    }

    @Override
    protected void onResume(){
        super.onResume();
        if(mContent != null){ //Do this to refresh contents of fragment (mainly decklist)
            mContent.onResume();
        }
    }

    @Override
    public void itemSelected(){ //Call from card lookup fragment to collapse searchview
        mOptionsMenu.findItem(R.id.search).collapseActionView();
    }

    @Override
    public ArrayList<String> getDeckList(){
        DeckListFragment deckListFragment = (DeckListFragment)getSupportFragmentManager()
                .findFragmentByTag(ARG_CARD_DECKLIST);

        //Check if fragment attached
        if(deckListFragment == null){
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            deckListFragment = DeckListFragment.newInstance();
            transaction.add(R.id.fragment_container, deckListFragment, ARG_CARD_DECKLIST);
            transaction.hide(deckListFragment);
            transaction.commit();
            getSupportFragmentManager().executePendingTransactions();

        }

        return deckListFragment.getDeckList();
    }
    public int addToDeck(String deck, String card, boolean inMain){
        DeckListFragment deckListFragment = (DeckListFragment)getSupportFragmentManager()
                .findFragmentByTag(ARG_CARD_DECKLIST);

        //Check if fragment attached
        if(deckListFragment == null){
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            deckListFragment = DeckListFragment.newInstance();
            transaction.add(deckListFragment, ARG_CARD_DECKLIST);
            transaction.commit();
            getSupportFragmentManager().executePendingTransactions();
        }

        return deckListFragment.addToDeck(deck, card, inMain);
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

            if(scoreFragment != null){ //TODO: Replace score but not sure if this needed anymore...
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
                    menu.findItem(R.id.change_score).setVisible(true);
                    break;
                case ARG_CARD_LOOKUP:
                    menu.findItem(R.id.search).setVisible(true);
                    break;
                case ARG_CARD_DECKLIST:
                    menu.findItem(R.id.new_deck).setVisible(true);
                    break;
            }
        }
        invalidateOptionsMenu();
        return super.onPrepareOptionsMenu(menu);
}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //Show an alert when user ants to play a new game
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
        else if(id == R.id.new_deck) {
            final DeckListFragment deckListFragment = (DeckListFragment)getSupportFragmentManager()
                    .findFragmentByTag(ARG_CARD_DECKLIST);
            final EditText input = new EditText(this);
            input.setHint(R.string.add_deck_hint);
            input.setSingleLine(true);
            input.setFocusableInTouchMode(true); //Make it focusable for enter button listener
            input.requestFocus();

            final String deckType = DeckListFragment.ARG_DECK_STANDARD;

            AlertDialog.Builder alert = new AlertDialog.Builder(this);

            alert.setMessage(R.string.add_deck_confirmation)
                    .setPositiveButton(R.string.add_deck, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deckListFragment.addDeck(input.getText().toString(), deckType);
                        }
                    })
                    .setNegativeButton(R.string.alert_cancel, null)
                    .setView(input);

            final AlertDialog alertToShow = alert.create(); //Show the keyboard
            alertToShow.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
//            alertToShow.show();

            String[] choices = {"60 card", "100 card commander"};
            final boolean[] choicesSel = {true, false};
            new AlertDialog.Builder(this)
                    .setTitle("What type of deck?")
                    .setMultiChoiceItems(choices, choicesSel, new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                            if(isChecked){
                                switch(which){
                                    case 0:
                                        choicesSel[0]=true;
                                        ((AlertDialog)dialog).getListView().setItemChecked(0, true);
                                        choicesSel[1]=false;
                                        ((AlertDialog)dialog).getListView().setItemChecked(1, false);
                                        break;
                                    case 1:
                                        choicesSel[0]=false;
                                        ((AlertDialog)dialog).getListView().setItemChecked(0, false);
                                        choicesSel[1]=true;
                                        ((AlertDialog)dialog).getListView().setItemChecked(1, true);
                                        break;
                                }
                            }
                        }
                    })
                    .setPositiveButton("Next", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            alertToShow.show();
                        }
                    })
                    .setNegativeButton(R.string.alert_cancel, null)
                    .show();

            input.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (event != null && (keyCode == KeyEvent.KEYCODE_ENTER)
                            && (event.getAction() == KeyEvent.ACTION_DOWN)) {
                        deckListFragment.addDeck(input.getText().toString(), deckType);
                        if(alertToShow.isShowing()){ //Close the alert dialog manually
                            alertToShow.cancel();
                        }
                        return true;
                    }
                    return false;
                }
            });

        }
        else if(id == R.id.change_score){
            final ScoreFragment scoreFragment = (ScoreFragment)getSupportFragmentManager()
                    .findFragmentByTag(ARG_LIFE_COUNTER_FRAGMENT);
            if (scoreFragment != null){
                final String[] scoreChoices = {"Standard", "Commander"};
                final boolean[] scoreChoicesSel = {true, false};
                new AlertDialog.Builder(this)
                        .setTitle("Change starting score to:")
                        .setMultiChoiceItems(scoreChoices, scoreChoicesSel, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                if(isChecked){
                                    switch(which){
                                        case 0:
                                            scoreChoicesSel[0] = true;
                                            ((AlertDialog)dialog).getListView().setItemChecked(0, true);
                                            scoreChoicesSel[1] = false;
                                            ((AlertDialog)dialog).getListView().setItemChecked(1, false);
                                            break;
                                        case 1:
                                            scoreChoicesSel[0] = false;
                                            ((AlertDialog)dialog).getListView().setItemChecked(0, false);
                                            scoreChoicesSel[1] = true;
                                            ((AlertDialog)dialog).getListView().setItemChecked(1, true);
                                            break;
                                    }
                                }
                            }
                        })
                        .setPositiveButton("Reset and Change", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(scoreChoicesSel[0]){
                                    scoreFragment.setScoreView("20");
                                }
                                else{
                                    scoreFragment.setScoreView("40");
                                }
                            }
                        })
                        .setNegativeButton(R.string.alert_cancel, null)
                        .show();
            }
        }

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //TODO:DO WE NEED THIS?
    @Override
    protected void onStop(){
        super.onStop();
        //Clear the score if user quits app
        SharedPreferences sharedPrefs = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.clear();
        editor.commit();
    }

    //TODO:DO WE NEED THIS?
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
