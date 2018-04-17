package com.sommy.android.med_manager;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.sommy.android.med_manager.ui.MainActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.anything;

/**
 * Created by somto on 4/16/18.
 */
@RunWith(AndroidJUnit4.class)
public class IdlingResourceMainActivityTest {

    /**
     * The ActivityTestRule is a rule provided by Android used for functional testing of a single
     * activity. The activity that will be tested, MainActivity in this case, will be launched
     * before each test that's annotated with @Test and before methods annotated with @Before.
     *
     * The activity will be terminated after the test and methods annotated with @After are
     * complete. This rule allows you to directly access the activity during the test.
     */
    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule =
            new ActivityTestRule<>(MainActivity.class);

    private IdlingResource mIdlingResource;


    // Registers any resource that needs to be synchronized with Espresso before the test is run.
    @Before
    public void registerIdlingResource() {
        mIdlingResource = mActivityTestRule.getActivity().getIdlingResource();
        // To prove that the test fails, omit this call:
        Espresso.registerIdlingResources(mIdlingResource);
    }

    @Test
    public void idlingResourceTest() {
        onData(anything()).inAdapterView(withId(R.id.medication_recyclerView)).atPosition(0).perform(click());
    }

    // unregister resources when not needed to avoid malfunction.
    @After
    public void unregisterIdlingResource() {
        if (mIdlingResource != null) {
            Espresso.unregisterIdlingResources(mIdlingResource);
        }
    }

}
