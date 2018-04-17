package com.sommy.android.med_manager;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.sommy.android.med_manager.ui.AddMedicationActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by somto on 4/17/18.
 */
@RunWith(AndroidJUnit4.class)
public class AddMedicationActivityScreenTest {

    @Rule
    public ActivityTestRule<AddMedicationActivity> mActivityRule = new ActivityTestRule<>(
            AddMedicationActivity.class);



    @Test
    public void clickHomeButton_NavigateUp() {
        onView(withContentDescription("Navigate up")).perform(click());
    }

    @Test
    public void clickSaveButton_AddMedictionActivity() {

        onView(withId(R.id.action_save)).perform(click());
    }

}
