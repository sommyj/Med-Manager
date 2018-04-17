package com.sommy.android.med_manager;

import android.content.Intent;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.sommy.android.med_manager.ui.AddMedicationActivity;
import com.sommy.android.med_manager.ui.MedicationDetailsActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtraWithKey;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.allOf;

/**
 * Created by somto on 4/17/18.
 */
@RunWith(AndroidJUnit4.class)
public class MedicationDetailsActivityScreenTest {
    @Rule
    public ActivityTestRule<MedicationDetailsActivity> mActivityRule = new ActivityTestRule<MedicationDetailsActivity>(
            MedicationDetailsActivity.class);


    @Test
    public void clickHomeButton_NavigateUp() {
        onView(withContentDescription("Navigate up")).perform(click());
    }

    @Test
    public void clickEditButton_AddMedictionActivity() {

        Intents.init();


        MedicationDetailsActivity.stringsForAddMedictionActivity = new String[]{"medId", "nameString", "descriptionString", "String.valueOf(intervalInt)", "startDateString", "endDateString"};

        onView(withId(R.id.action_edit)).perform(click());
        intended(allOf(hasComponent(AddMedicationActivity.class.getName()),
        hasExtraWithKey(Intent.EXTRA_TEXT)));


        Intents.release();
    }

}
