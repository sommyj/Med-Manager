package com.sommy.android.med_manager;

import android.app.Activity;
import android.app.Instrumentation;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.sommy.android.med_manager.ui.LoginActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.ComponentNameMatchers.hasShortClassName;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;

/**
 * Created by somto on 4/17/18.
 */
@RunWith(AndroidJUnit4.class)
public class LoginActivityScreenTest {

        @Rule
        public ActivityTestRule<LoginActivity> mActivityRule = new ActivityTestRule<>(
                LoginActivity.class);



        @Test
        public void clickSignInButton_MainActivity() {

                Intents.init();

                intending(hasComponent(hasShortClassName(".MainActivity")))
                        .respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, null));

                Intents.release();
        }

}
