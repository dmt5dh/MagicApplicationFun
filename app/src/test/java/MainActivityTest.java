import android.support.v4.app.Fragment;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.junit.Assert.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import arashincleric.com.magicapplicationfun.BuildConfig;
import arashincleric.com.magicapplicationfun.CardLookupFragment;
import arashincleric.com.magicapplicationfun.DeckListFragment;
import arashincleric.com.magicapplicationfun.MainActivity;
import arashincleric.com.magicapplicationfun.R;
import arashincleric.com.magicapplicationfun.ScoreFragment;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class MainActivityTest {

    private MainActivity activity;
    private Fragment fragment;

    @Before
    public void setup(){
        activity = Robolectric.buildActivity(MainActivity.class).create().get();
        fragment = activity.getFragment();
    }

    @Test
    public void activityNotNull(){
        assertNotNull(activity);
    }

    @Test
    public void fragmentNull(){
        assertNull(fragment);
    }

    @Test
    public void changeFragmentToScore(){
        activity.switchFragment(activity.ARG_LIFE_COUNTER_FRAGMENT);
        fragment = activity.getFragment();
        assertEquals(fragment.getClass(), ScoreFragment.class);
    }

    @Test
    public void changeFragmentToCardLookup(){
        activity.switchFragment(activity.ARG_CARD_LOOKUP);
        fragment = activity.getFragment();
        assertEquals(fragment.getClass(), CardLookupFragment.class);
    }

    @Test
    public void changeFragmentToDecklist(){
        activity.switchFragment(activity.ARG_CARD_DECKLIST);
        fragment = activity.getFragment();
        assertEquals(fragment.getClass(), DeckListFragment.class);
    }
}
