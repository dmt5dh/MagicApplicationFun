import android.app.Fragment;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;
import org.robolectric.util.FragmentTestUtil;

import arashincleric.com.magicapplicationfun.BuildConfig;
import arashincleric.com.magicapplicationfun.R;
import arashincleric.com.magicapplicationfun.ScoreFragment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class ScoreFragmentTest {

    private ScoreFragment fragment;

    @Before
    public void setup(){
        fragment = ScoreFragment.newInstance();
        SupportFragmentTestUtil.startFragment(fragment);
    }

    @Test
    public void fragmentSetUp(){
        assertNotNull(fragment.getView());
        assertEquals(fragment.getScore(), 20);
        assertEquals(((TextView) fragment.getView().findViewById(R.id.scoreView)).getText(), "20");
    }

    @Test
    public void decreaseScore(){
        assertEquals(fragment.getScore(), 20);
        fragment.decreaseScore();
        assertEquals(fragment.getScore(), 19);
        assertEquals(((TextView) fragment.getView().findViewById(R.id.scoreView)).getText(), "19");
    }

    @Test
    public void decreaseScore5(){
        assertEquals(fragment.getScore(), 20);
        for(int i = 0; i < 5; i++){
            fragment.decreaseScore();
        }
        assertEquals(fragment.getScore(), 15);
        assertEquals(((TextView) fragment.getView().findViewById(R.id.scoreView)).getText(), "15");
    }

    @Test
    public void increaseScore(){
        assertEquals(fragment.getScore(), 20);
        fragment.increaseScore();
        assertEquals(fragment.getScore(), 21);
        assertEquals(((TextView) fragment.getView().findViewById(R.id.scoreView)).getText(), "21");
    }

    @Test
    public void increaseScore5(){
        assertEquals(fragment.getScore(), 20);
        for(int i = 0; i < 5; i++){
            fragment.increaseScore();
        }
        assertEquals(fragment.getScore(), 25);
        assertEquals(((TextView) fragment.getView().findViewById(R.id.scoreView)).getText(), "25");
    }

    @Test
    public void setScore(){
        assertEquals(fragment.getScore(), 20);
        fragment.setScoreView("50");
        assertEquals(fragment.getScore(), 50);
        assertEquals(((TextView) fragment.getView().findViewById(R.id.scoreView)).getText(), "50");
    }

    @Test
    public void setScoreNegative(){
        assertEquals(fragment.getScore(), 20);
        fragment.setScoreView("-5");
        assertEquals(fragment.getScore(), 20);
        assertEquals(((TextView) fragment.getView().findViewById(R.id.scoreView)).getText(), "20");
    }

    @Test
    public void resetScore(){
        assertEquals(fragment.getScore(), 20);
        fragment.decreaseScore();
        fragment.decreaseScore();
        fragment.decreaseScore();
        assertEquals(fragment.getScore(), 17);
        assertEquals(((TextView) fragment.getView().findViewById(R.id.scoreView)).getText(), "17");
        fragment.resetScore();
        assertEquals(fragment.getScore(), 20);
        assertEquals(((TextView) fragment.getView().findViewById(R.id.scoreView)).getText(), "20");

    }

    @Test
    public void didScorePassZero(){
        assertEquals(fragment.getScore(), 20);
        assertEquals(((TextView) fragment.getView().findViewById(R.id.scoreView)).getText(), "20");
        for(int i = 1; i < 20; i++){
            fragment.decreaseScore();
        }
        assertEquals(fragment.getScore(), 1);
        assertEquals(((TextView) fragment.getView().findViewById(R.id.scoreView)).getText(), "1");
        fragment.decreaseScore();
        assertEquals(fragment.getScore(), 0);
        assertEquals(((TextView) fragment.getView().findViewById(R.id.scoreView)).getText(), "0");
        fragment.decreaseScore();
        assertEquals(fragment.getScore(), 0);
        assertEquals(((TextView) fragment.getView().findViewById(R.id.scoreView)).getText(), "0");
    }
}
