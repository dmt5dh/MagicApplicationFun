import android.widget.ArrayAdapter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import arashincleric.com.magicapplicationfun.BuildConfig;
import arashincleric.com.magicapplicationfun.CardLookupFragment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class CardLookupFragmentTest {

    private CardLookupFragment fragment;
    private String testJsonResponse = "{\n" +
            "  \"name\": \"Zurgo Helmsmasher\",\n" +
            "  \"id\": \"zurgo-helmsmasher\",\n" +
            "  \"url\": \"https://api.deckbrew.com/mtg/cards/zurgo-helmsmasher\",\n" +
            "  \"store_url\": \"http://store.tcgplayer.com/magic/product/show?partner=DECKBREW\\u0026ProductName=zurgo-helmsmasher\",\n" +
            "  \"types\": [\n" +
            "    \"creature\"\n" +
            "  ],\n" +
            "  \"supertypes\": [\n" +
            "    \"legendary\"\n" +
            "  ],\n" +
            "  \"subtypes\": [\n" +
            "    \"orc\",\n" +
            "    \"warrior\"\n" +
            "  ],\n" +
            "  \"colors\": [\n" +
            "    \"black\",\n" +
            "    \"red\",\n" +
            "    \"white\"\n" +
            "  ],\n" +
            "  \"cmc\": 5,\n" +
            "  \"cost\": \"{2}{R}{W}{B}\",\n" +
            "  \"text\": \"Haste\\nZurgo Helmsmasher attacks each combat if able.\\nZurgo Helmsmasher has indestructible as long as it's your turn.\\nWhenever a creature dealt damage by Zurgo Helmsmasher this turn dies, put a +1/+1 counter on Zurgo Helmsmasher.\",\n" +
            "  \"power\": \"7\",\n" +
            "  \"toughness\": \"2\",\n" +
            "  \"formats\": {\n" +
            "    \"commander\": \"legal\",\n" +
            "    \"legacy\": \"legal\",\n" +
            "    \"modern\": \"legal\",\n" +
            "    \"standard\": \"legal\",\n" +
            "    \"vintage\": \"legal\"\n" +
            "  },\n" +
            "  \"editions\": [\n" +
            "    {\n" +
            "      \"set\": \"Khans of Tarkir\",\n" +
            "      \"set_id\": \"KTK\",\n" +
            "      \"watermark\": \"Mardu\",\n" +
            "      \"rarity\": \"mythic\",\n" +
            "      \"artist\": \"Aleksi Briclot\",\n" +
            "      \"multiverse_id\": 386731,\n" +
            "      \"number\": \"214\",\n" +
            "      \"layout\": \"normal\",\n" +
            "      \"price\": {\n" +
            "        \"low\": 0,\n" +
            "        \"median\": 0,\n" +
            "        \"high\": 0\n" +
            "      },\n" +
            "      \"url\": \"https://api.deckbrew.com/mtg/cards?multiverseid=386731\",\n" +
            "      \"image_url\": \"https://image.deckbrew.com/mtg/multiverseid/386731.jpg\",\n" +
            "      \"set_url\": \"https://api.deckbrew.com/mtg/sets/KTK\",\n" +
            "      \"store_url\": \"http://store.tcgplayer.com/magic/khans-of-tarkir/zurgo-helmsmasher?partner=DECKBREW\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"set\": \"Prerelease Events\",\n" +
            "      \"set_id\": \"pPRE\",\n" +
            "      \"rarity\": \"special\",\n" +
            "      \"artist\": \"Aleksi Briclot\",\n" +
            "      \"multiverse_id\": 0,\n" +
            "      \"number\": \"127\",\n" +
            "      \"layout\": \"normal\",\n" +
            "      \"price\": {\n" +
            "        \"low\": 0,\n" +
            "        \"median\": 0,\n" +
            "        \"high\": 0\n" +
            "      },\n" +
            "      \"url\": \"https://api.deckbrew.com/mtg/cards?multiverseid=0\",\n" +
            "      \"image_url\": \"https://image.deckbrew.com/mtg/multiverseid/0.jpg\",\n" +
            "      \"set_url\": \"https://api.deckbrew.com/mtg/sets/pPRE\",\n" +
            "      \"store_url\": \"http://store.tcgplayer.com/magic/prerelease-events/zurgo-helmsmasher?partner=DECKBREW\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"set\": \"Duel Decks: Speed vs. Cunning\",\n" +
            "      \"set_id\": \"DDN\",\n" +
            "      \"rarity\": \"mythic\",\n" +
            "      \"artist\": \"Ryan Alexander Lee\",\n" +
            "      \"multiverse_id\": 386380,\n" +
            "      \"number\": \"1\",\n" +
            "      \"layout\": \"normal\",\n" +
            "      \"price\": {\n" +
            "        \"low\": 0,\n" +
            "        \"median\": 0,\n" +
            "        \"high\": 0\n" +
            "      },\n" +
            "      \"url\": \"https://api.deckbrew.com/mtg/cards?multiverseid=386380\",\n" +
            "      \"image_url\": \"https://image.deckbrew.com/mtg/multiverseid/386380.jpg\",\n" +
            "      \"set_url\": \"https://api.deckbrew.com/mtg/sets/DDN\",\n" +
            "      \"store_url\": \"http://store.tcgplayer.com/magic/duel-decks-speed-vs-cunning/zurgo-helmsmasher?partner=DECKBREW\"\n" +
            "    }\n" +
            "  ]\n" +
            "}";


    private String textViewExpected = "Card Name: Zurgo Helmsmasher\n" +
            "Mana Cost: {2}{R}{W}{B}\n" +
            "Converted Mana Cost: 5\n" +
            "Types: legendary creature - orc warrior\n" +
            "Card Text: Haste\n" +
            "Zurgo Helmsmasher attacks each combat if able.\n" +
            "Zurgo Helmsmasher has indestructible as long as it's your turn.\n" +
            "Whenever a creature dealt damage by Zurgo Helmsmasher this turn dies, put a +1/+1 counter on Zurgo Helmsmasher.\n" +
            "Power/Toughness: 7/2\n" +
            "Edition: Khans of Tarkir\n" +
            "Rarity: mythic\n" +
            "Artist: Aleksi Briclot\n";

    private String textViewExpected2 = "Card Name: Zurgo Helmsmasher\n" +
            "Mana Cost: {2}{R}{W}{B}\n" +
            "Converted Mana Cost: 5\n" +
            "Types: legendary creature - orc warrior\n" +
            "Card Text: Haste\n" +
            "Zurgo Helmsmasher attacks each combat if able.\n" +
            "Zurgo Helmsmasher has indestructible as long as it's your turn.\n" +
            "Whenever a creature dealt damage by Zurgo Helmsmasher this turn dies, put a +1/+1 counter on Zurgo Helmsmasher.\n" +
            "Power/Toughness: 7/2\n" +
            "Edition: Duel Decks: Speed vs. Cunning\n" +
            "Rarity: mythic\n" +
            "Artist: Ryan Alexander Lee\n";
    @Before
    public void setup(){
        fragment = CardLookupFragment.newInstance();
        SupportFragmentTestUtil.startFragment(fragment);
    }

    @Test
    public void fragmentSetUp(){
        assertNotNull(fragment.getView());
        assertNotNull(fragment.getListView());
        assertEquals(fragment.getListView().getCount(), 0);
    }

    @Test
    public void clearList(){
        for(int i = 0; i < 10; i++){
            ((ArrayAdapter<String>) fragment.getListView().getAdapter()).add("item" + Integer.toString(i));
        }
        assertEquals(fragment.getListView().getCount(), 10);
        fragment.clearList();
        assertEquals(fragment.getListView().getCount(), 0);
    }

    @Test
    public void printStats(){
        fragment.printStats(testJsonResponse);
        assertEquals(fragment.getTextViewText(), textViewExpected);
    }

    @Test
    public void setText(){
        fragment.printStats(testJsonResponse);
        assertEquals(fragment.getTextViewText(), textViewExpected);
        fragment.setText(1);
        assertEquals(fragment.getTextViewText(), textViewExpected2);
    }

    @Test
    public void testAutoComplete(){
        fragment.getAutoComplete("zurgo");
        assertEquals(fragment.getListView().getCount(), 2);
    }

    @Test
    public void testAutoCompleteNoResponse(){
        fragment.getAutoComplete("adsf");
        assertEquals(fragment.getListView().getCount(), 0);
    }

    @Test
    public void testTestConnection(){
        fragment.testConnection("zurgo-helmsmasher");
        assertEquals(fragment.getTextViewText(), textViewExpected);
    }

    @Test
    public void testTestConnectionIncomplete(){
        fragment.testConnection("zurgo");
        assertEquals(fragment.getListView().getCount(), 2);
    }
}
