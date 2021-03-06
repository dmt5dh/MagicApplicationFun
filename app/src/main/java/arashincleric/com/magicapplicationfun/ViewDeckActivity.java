package arashincleric.com.magicapplicationfun;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;


public class ViewDeckActivity extends AppCompatActivity implements ViewDeckListFragment.OnViewCardListener{

    private final String ARG_CARD_LOOKUP = "CardLookup";
    private final String ARG_CARD_DECKLIST= "Decklist";
    private final String ARG_DECK_FRAG= "DeckFrag";
    private ArrayList<String> mainList;
    private ArrayList<String> sideList;
    private Fragment mContent;
    private String deckName;

    @Override
    public void onBackPressed(){
        if(getSupportFragmentManager().getBackStackEntryCount() < 2){
            finish();
        }
        else {
            super.onBackPressed();
        }
    }

    /**
     * Opens a card lookup fragment when user wants to view a certain card in their deck.
     * @param cardName Card name to call to Deckbrew API
     */
    public void lookupCard(String cardName){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if(mContent != null){
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(ARG_CARD_LOOKUP);
            if(fragment == null){ //Check if already attached
                mContent = CardLookupFragment.newInstance();
            }
            else{
                mContent = fragment;
            }

            transaction.replace(R.id.fragment_container, mContent, ARG_CARD_LOOKUP);
            transaction.addToBackStack(ARG_CARD_LOOKUP);
            transaction.commit();

            getSupportFragmentManager().executePendingTransactions();

            ((CardLookupFragment) mContent).testConnection(cardName);
        }
    }

    /**
     * Delete a card from a deck.
     * @param cardName Name of card to delete
     * @param inMain <tt>true</tt> If adding to main, <tt>false</tt> otherwise.
     * @return Integer representing success or specified error
     */
    public int deleteCard(String cardName, boolean inMain){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if(mContent != null){
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(ARG_DECK_FRAG);
            if(fragment == null){
                fragment = DeckListFragment.newInstance();
                transaction.add(fragment, ARG_DECK_FRAG);
                transaction.commit();
                getSupportFragmentManager().executePendingTransactions();
            }

            return ((DeckListFragment) fragment).deleteFromDeck(deckName, cardName, inMain);

        }

        return -1;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_deck);

        Intent intent = getIntent(); //Grab all the extras here
        mainList = intent.getStringArrayListExtra(DeckListFragment.DECK_MAIN_MESSAGE);
        sideList = intent.getStringArrayListExtra(DeckListFragment.DECK_SIDE_MESSAGE);
        deckName = intent.getStringExtra(DeckListFragment.DECK_NAME_MESSAGE);

        if(mContent == null){
            mContent = ViewDeckListFragment.newInstance(mainList, sideList, deckName);
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_container, mContent, ARG_CARD_DECKLIST);
        transaction.addToBackStack(ARG_CARD_DECKLIST);
        transaction.commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_deck, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_delete) {
            String deleteDeck = getResources().getString(R.string.alert_delete_deck);
            String deleteDeckMsg = String.format(deleteDeck, deckName);
            new AlertDialog.Builder(this)
                    .setMessage(deleteDeckMsg)
                    .setPositiveButton(R.string.alert_yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                            DeckListFragment deckListFragment =
                                    (DeckListFragment)getSupportFragmentManager().findFragmentByTag(ARG_DECK_FRAG);

                            if(deckListFragment == null){
                                deckListFragment = DeckListFragment.newInstance();
                                transaction.add(deckListFragment, ARG_DECK_FRAG);
                                transaction.commit();
                            }
                            //Make sure we add the fragment before we call, otherwise will break
                            getSupportFragmentManager().executePendingTransactions();
                            deckListFragment.deleteDeck(deckName);
                            finish();

                        }
                    })
                    .setNegativeButton(R.string.alert_cancel, null)
                    .show();
        }

        return super.onOptionsItemSelected(item);
    }
}
