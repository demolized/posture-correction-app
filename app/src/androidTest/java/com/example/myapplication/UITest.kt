package com.example.myapplication

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UITest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun testPostureFragmentUI() {
        onView(withId(R.id.posture_circle)).check(matches(isDisplayed()))
        onView(withId(R.id.postureAngle)).check(matches(isDisplayed()))
        onView(withId(R.id.start_session_button)).check(matches(isDisplayed()))
        onView(withId(R.id.tracking_toggle)).check(matches(isDisplayed()))
    }

    @Test
    fun testProgressFragmentUI() {
        onView(withId(R.id.navigation_progress)).perform(click())
        onView(withId(R.id.progress_bar)).check(matches(isDisplayed()))
        onView(withId(R.id.streak_graph)).check(matches(isDisplayed()))
        onView(withId(R.id.drill_history)).check(matches(isDisplayed()))
    }

    @Test
    fun testSettingsFragmentUI() {
        onView(withId(R.id.navigation_settings)).perform(click())
        onView(withId(R.id.calibrate_button)).check(matches(isDisplayed()))
        onView(withId(R.id.sensitivity_slider)).check(matches(isDisplayed()))
        onView(withId(R.id.overlay_mode_toggle)).check(matches(isDisplayed()))
    }
}