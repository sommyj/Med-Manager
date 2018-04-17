package com.sommy.android.med_manager;

import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.sommy.android.med_manager.ui.AddMedictionActivity;
import com.sommy.android.med_manager.ui.MainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;

/**
 * Created by somto on 4/16/18.
 */

@RunWith(AndroidJUnit4.class)
public class MainActivityScreenTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(
            MainActivity.class);


    @Test
    public void clickButton() {

        Intents.init();

        onView(withId(R.id.fab)).perform(click());
        intended(hasComponent(AddMedictionActivity.class.getName()));

        onView(withId(R.id.nav_addMedication)).perform(click());
        intended(hasComponent(AddMedictionActivity.class.getName()));

        Intents.release();
    }

    /**
     * Clicks on a RecyclerView item and checks it opens up the MedicationDetailsActivity with the correct details.
     */
    @Test
    public void clickRecyclerViewItem_MedicationDetailsActivity() {

        // Uses {@link Espresso#onData(org.hamcrest.Matcher)} to get a reference to a specific
        // recyclerview item and clicks it.
        onData(anything()).inAdapterView(withId(R.id.medication_recyclerView)).atPosition(0).perform(click());

        // Checks that the OMedicationDetailsActivity opens with the correct title name displayed
        onView(withId(R.id.detailTitle_textView)).check(matches(withText("me")));


    }
}
