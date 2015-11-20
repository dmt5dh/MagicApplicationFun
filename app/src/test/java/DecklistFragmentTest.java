import android.content.Intent;
import android.util.Log;

import org.json.JSONArray;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import arashincleric.com.magicapplicationfun.BuildConfig;
import arashincleric.com.magicapplicationfun.DeckListFragment;
import arashincleric.com.magicapplicationfun.ViewDeckActivity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class DecklistFragmentTest {

    private DeckListFragment fragment;

    @Before
    public void setup(){
        fragment = DeckListFragment.newInstance();
        SupportFragmentTestUtil.startFragment(fragment);
    }

    @Test
    public void fragmentSetUp(){
        assertNotNull(fragment.getView());
        assertNotNull(fragment.getActivity());
        String s = "";
        try{
            //Read the JSON
            FileInputStream fis = fragment.getActivity().openFileInput(fragment.FILENAME);
            StringBuffer sb = new StringBuffer("");
            byte[] buffer = new byte[1024];
            int n;

            while((n = fis.read(buffer)) != -1) {
                sb.append(new String(buffer, 0, n));
            }

            fis.close();

            s = sb.toString();

        } catch (IOException e) {
            Log.e("ADDDECK", e.getMessage());
        }

        assertEquals(s, "[]");
    }

    @Test
    public void fragmentGetJsonBlank(){
        assertNotNull(fragment.getDeckJson());
        String json = fragment.getDeckJson().toString();
        assertNotNull(json);
        assertEquals(json, "[]");
    }

    @Test
    public void addDeck(){
        fragment.addDeck("deck1");
        assertEquals(fragment.getDeckJson().toString(), "[{\"name\":\"deck1\"," +
                "\"deckList\":{\"main\":[],\"side\":[]}}]");
    }

    @Test
    public void addDeckMulti(){
        fragment.addDeck("deck1");
        assertEquals(fragment.getDeckJson().toString(), "[{\"name\":\"deck1\"," +
                "\"deckList\":{\"main\":[],\"side\":[]}}]");
        fragment.addDeck("deck2");
        assertEquals(fragment.getDeckJson().toString(),
                "[{\"name\":\"deck1\",\"deckList\":{\"main\":[],\"side\":[]}},{\"name\":\"deck2\"," +
                        "\"deckList\":{\"main\":[],\"side\":[]}}]");
        fragment.addDeck("deck3");
        assertEquals(fragment.getDeckJson().toString(),
                "[{\"name\":\"deck1\",\"deckList\":{\"main\":[],\"side\":[]}},{\"name\":\"deck2\"," +
                        "\"deckList\":{\"main\":[],\"side\":[]}}," +
                        "{\"name\":\"deck3\",\"deckList\":{\"main\":[],\"side\":[]}}]");
    }

    @Test
    public void addDeckDuplicate(){
        fragment.addDeck("deck1");
        assertEquals(fragment.getDeckJson().toString(), "[{\"name\":\"deck1\"," +
                "\"deckList\":{\"main\":[],\"side\":[]}}]");
        fragment.addDeck("deck1");
        assertEquals(fragment.getDeckJson().toString(), "[{\"name\":\"deck1\"," +
                "\"deckList\":{\"main\":[],\"side\":[]}}]");
    }

    @Test
    public void deleteDeck(){
        fragment.addDeck("deck1");
        assertEquals(fragment.getDeckJson().toString(), "[{\"name\":\"deck1\"," +
                "\"deckList\":{\"main\":[],\"side\":[]}}]");
        fragment.deleteDeck("deck1");
        assertEquals(fragment.getDeckJson().toString(), "[]");
    }

    @Test
    public void deleteDeckMulti(){
        fragment.addDeck("deck1");
        assertEquals(fragment.getDeckJson().toString(), "[{\"name\":\"deck1\"," +
                "\"deckList\":{\"main\":[],\"side\":[]}}]");
        fragment.addDeck("deck2");
        assertEquals(fragment.getDeckJson().toString(),
                "[{\"name\":\"deck1\",\"deckList\":{\"main\":[],\"side\":[]}},{\"name\":\"deck2\"," +
                        "\"deckList\":{\"main\":[],\"side\":[]}}]");
        fragment.addDeck("deck3");
        assertEquals(fragment.getDeckJson().toString(),
                "[{\"name\":\"deck1\",\"deckList\":{\"main\":[],\"side\":[]}},{\"name\":\"deck2\"," +
                        "\"deckList\":{\"main\":[],\"side\":[]}}," +
                        "{\"name\":\"deck3\",\"deckList\":{\"main\":[],\"side\":[]}}]");
        fragment.deleteDeck("deck2");
        assertEquals(fragment.getDeckJson().toString(),
                "[{\"name\":\"deck1\",\"deckList\":{\"main\":[],\"side\":[]}}," +
                        "{\"name\":\"deck3\",\"deckList\":{\"main\":[],\"side\":[]}}]");
    }

    @Test
    public void deleteDeckNonExist(){
        fragment.addDeck("deck1");
        assertEquals(fragment.getDeckJson().toString(), "[{\"name\":\"deck1\"," +
                "\"deckList\":{\"main\":[],\"side\":[]}}]");
        fragment.deleteDeck("deck2");
        assertEquals(fragment.getDeckJson().toString(), "[{\"name\":\"deck1\"," +
                "\"deckList\":{\"main\":[],\"side\":[]}}]");
    }

    @Test
    public void getDeckNamesEmpty(){
        assertEquals(fragment.getDeckList().size(), 0);
    }

    @Test
    public void getDeckNames(){
        for(int i = 0; i < 5; i++){
            fragment.addDeck("deck" + Integer.toString(i));
        }
        ArrayList<String> deckList = fragment.getDeckList();
        assertEquals(deckList.size(), 5);
        for(int i = 0; i < 5; i++){
            assertEquals(deckList.get(i), "deck" + Integer.toString(i));
        }
    }

    @Test
    public void addCardToDeckMain(){
        fragment.addDeck("deck1");
        int response = fragment.addToDeck("deck1", "test", true);
        assertEquals(fragment.getDeckJson().toString(), "[{\"name\":\"deck1\"," +
                "\"deckList\":{\"main\":[\"test\"],\"side\":[]}}]");
        assertEquals(response, 0);
    }

    @Test
    public void addCardToDeckMainMulti(){
        fragment.addDeck("deck1");
        int response = -1;
        for(int i = 0; i < 5; i++){
            response = fragment.addToDeck("deck1", "test", true);
        }
        assertEquals(fragment.getDeckJson().toString(), "[{\"name\":\"deck1\"," +
                "\"deckList\":{\"main\":[\"test\",\"test\",\"test\",\"test\"," +
                "\"test\"],\"side\":[]}}]");
        assertEquals(response, 0);
    }

    @Test
    public void addCardToDeckMainFull(){
        fragment.addDeck("deck1");
        int response = -1;
        for(int i = 0; i < 61; i++){
            response = fragment.addToDeck("deck1", "test", true);
        }
        assertEquals(response, 1);
    }

    @Test
    public void addCardToDeckSide(){
        fragment.addDeck("deck1");
        int response = fragment.addToDeck("deck1","test",false);
        assertEquals(fragment.getDeckJson().toString(), "[{\"name\":\"deck1\"," +
                "\"deckList\":{\"main\":[],\"side\":[\"test\"]}}]");
        assertEquals(response, 0);
    }

    @Test
    public void addCardToDeckSideMulti(){
        fragment.addDeck("deck1");
        int response = -1;
        for(int i = 0; i < 5; i++){
            response = fragment.addToDeck("deck1","test",false);
        }
        assertEquals(fragment.getDeckJson().toString(), "[{\"name\":\"deck1\"," +
                "\"deckList\":{\"main\":[],\"side\":[\"test\"," +
                "\"test\",\"test\",\"test\",\"test\"]}}]");
        assertEquals(response, 0);
    }

    @Test
    public void addCardToDeckSideFull(){
        fragment.addDeck("deck1");
        int response = -1;
        for(int i = 0; i < 61; i++){
            response = fragment.addToDeck("deck1","test",false);
        }
        assertEquals(response, 2);
    }

    @Test
    public void addCardToDeckMainNoDeck(){
        int response = fragment.addToDeck("deck1", "test", true);
        assertEquals(response, 4);
    }

    @Test
    public void addCardToDeckSideNoDeck(){
        int response = fragment.addToDeck("deck1", "test", false);
        assertEquals(response, 4);
    }

    @Test
    public void deleteCardFromDeckMain(){
        fragment.addDeck("deck1");
        fragment.addToDeck("deck1", "test", true);
        assertEquals(fragment.getDeckJson().toString(), "[{\"name\":\"deck1\"," +
                "\"deckList\":{\"main\":[\"test\"],\"side\":[]}}]");
        int response = fragment.deleteFromDeck("deck1", "test", true);
        assertEquals(fragment.getDeckJson().toString(), "[{\"name\":\"deck1\"," +
                "\"deckList\":{\"main\":[],\"side\":[]}}]");
        assertEquals(response, 0);
    }

    @Test
    public void deleteCardFromDeckMainMulti(){
        fragment.addDeck("deck1");
        fragment.addToDeck("deck1", "test", true);
        fragment.addToDeck("deck1", "test1", true);
        fragment.addToDeck("deck1","test2",true);
        assertEquals(fragment.getDeckJson().toString(), "[{\"name\":\"deck1\"," +
                "\"deckList\":{\"main\":[\"test\",\"test1\",\"test2\"],\"side\":[]}}]");
        int response = fragment.deleteFromDeck("deck1", "test2", true);
        assertEquals(fragment.getDeckJson().toString(), "[{\"name\":\"deck1\"," +
                "\"deckList\":{\"main\":[\"test\",\"test1\"],\"side\":[]}}]");
        assertEquals(response, 2);
    }

    @Test
    public void deleteCardFromDeckMainNoExist(){
        fragment.addDeck("deck1");
        fragment.addToDeck("deck1", "test", true);
        fragment.addToDeck("deck1","test1",true);
        fragment.addToDeck("deck1","test2",true);
        assertEquals(fragment.getDeckJson().toString(), "[{\"name\":\"deck1\"," +
                "\"deckList\":{\"main\":[\"test\",\"test1\",\"test2\"],\"side\":[]}}]");
        int response = fragment.deleteFromDeck("deck1", "asdf", true);
        assertEquals(fragment.getDeckJson().toString(), "[{\"name\":\"deck1\"," +
                "\"deckList\":{\"main\":[\"test\",\"test1\",\"test2\"],\"side\":[]}}]");
        assertEquals(response, -1);
    }

    @Test
    public void deleteCardFromDeckSide(){
        fragment.addDeck("deck1");
        fragment.addToDeck("deck1", "test", false);
        assertEquals(fragment.getDeckJson().toString(), "[{\"name\":\"deck1\"," +
                "\"deckList\":{\"main\":[],\"side\":[\"test\"]}}]");
        int response = fragment.deleteFromDeck("deck1", "test", false);
        assertEquals(fragment.getDeckJson().toString(), "[{\"name\":\"deck1\"," +
                "\"deckList\":{\"main\":[],\"side\":[]}}]");
        assertEquals(response, 0);
    }

    @Test
    public void deleteCardFromDeckSideMulti(){
        fragment.addDeck("deck1");
        fragment.addToDeck("deck1", "test", false);
        fragment.addToDeck("deck1", "test1", false);
        fragment.addToDeck("deck1","test2",false);
        assertEquals(fragment.getDeckJson().toString(), "[{\"name\":\"deck1\"," +
                "\"deckList\":{\"main\":[],\"side\":[\"test\",\"test1\",\"test2\"]}}]");
        int response = fragment.deleteFromDeck("deck1", "test2", false);
        assertEquals(fragment.getDeckJson().toString(), "[{\"name\":\"deck1\"," +
                "\"deckList\":{\"main\":[],\"side\":[\"test\",\"test1\"]}}]");
        assertEquals(response, 2);
    }

    @Test
    public void deleteCardFromDeckSideNoExist(){
        fragment.addDeck("deck1");
        fragment.addToDeck("deck1", "test", false);
        fragment.addToDeck("deck1","test1",false);
        fragment.addToDeck("deck1","test2",false);
        assertEquals(fragment.getDeckJson().toString(), "[{\"name\":\"deck1\"," +
                "\"deckList\":{\"main\":[],\"side\":[\"test\",\"test1\",\"test2\"]}}]");
        int response = fragment.deleteFromDeck("deck1", "asdf", false);
        assertEquals(fragment.getDeckJson().toString(), "[{\"name\":\"deck1\"," +
                "\"deckList\":{\"main\":[],\"side\":[\"test\",\"test1\",\"test2\"]}}]");
        assertEquals(response, -1);
    }

    @Test
     public void deleteCardFromDeckRandom(){
        fragment.addDeck("deck1");
        fragment.addToDeck("deck1", "test", true);
        fragment.addToDeck("deck1","test1",false);
        fragment.addToDeck("deck1","test2",true);
        assertEquals(fragment.getDeckJson().toString(), "[{\"name\":\"deck1\"," +
                "\"deckList\":{\"main\":[\"test\",\"test2\"],\"side\":[\"test1\"]}}]");
        int response = fragment.deleteFromDeck("deck1", "test1", false);
        assertEquals(fragment.getDeckJson().toString(), "[{\"name\":\"deck1\"," +
                "\"deckList\":{\"main\":[\"test\",\"test2\"],\"side\":[]}}]");
        assertEquals(response, 0);
    }

    @Test
     public void deleteCardFromDeckRandomMulti(){
        fragment.addDeck("deck1");
        fragment.addToDeck("deck1", "test", true);
        fragment.addToDeck("deck1", "test1", false);
        fragment.addToDeck("deck1", "test2", true);
        assertEquals(fragment.getDeckJson().toString(), "[{\"name\":\"deck1\"," +
                "\"deckList\":{\"main\":[\"test\",\"test2\"],\"side\":[\"test1\"]}}]");
        int response = fragment.deleteFromDeck("deck1", "test1", false);
        assertEquals(fragment.getDeckJson().toString(), "[{\"name\":\"deck1\"," +
                "\"deckList\":{\"main\":[\"test\",\"test2\"],\"side\":[]}}]");
        assertEquals(response, 0);
        int response2 = fragment.deleteFromDeck("deck1", "test2", true);
        assertEquals(fragment.getDeckJson().toString(), "[{\"name\":\"deck1\"," +
                "\"deckList\":{\"main\":[\"test\"],\"side\":[]}}]");
        assertEquals(response2, 1);
    }

    @Test
    public void deleteCardFromDeckRandomNoExist(){
        fragment.addDeck("deck1");
        fragment.addToDeck("deck1", "test", true);
        fragment.addToDeck("deck1", "test1", false);
        fragment.addToDeck("deck1", "test2", true);
        assertEquals(fragment.getDeckJson().toString(), "[{\"name\":\"deck1\"," +
                "\"deckList\":{\"main\":[\"test\",\"test2\"],\"side\":[\"test1\"]}}]");
        int response = fragment.deleteFromDeck("deck1", "asdf", false);
        assertEquals(fragment.getDeckJson().toString(), "[{\"name\":\"deck1\"," +
                "\"deckList\":{\"main\":[\"test\",\"test2\"],\"side\":[\"test1\"]}}]");
        assertEquals(response, -1);
    }

    @Test
    public void onItemClick(){
        fragment.addDeck("deck1");
        fragment.onListItemClick(fragment.getListView(), fragment.getView(), 0, 0);
        Intent expectedIntent = new Intent(fragment.getActivity(), ViewDeckActivity.class);
        ShadowActivity shadowActivity = Shadows.shadowOf(fragment.getActivity());
        Intent actualIntent = shadowActivity.getNextStartedActivity();
        assertTrue(actualIntent.filterEquals(expectedIntent));
    }


}
