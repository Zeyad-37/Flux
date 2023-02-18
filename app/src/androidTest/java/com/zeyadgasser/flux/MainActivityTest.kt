package com.zeyadgasser.flux

import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class MainActivityTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)
}