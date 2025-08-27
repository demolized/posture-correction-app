package com.example.myapplication

import android.content.Context
import android.hardware.SensorManager
import androidx.preference.PreferenceManager
import com.example.myapplication.ui.PostureViewModel
import org.junit.Before
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class PostureSensorManagerTest {

    @Mock
    private lateinit var mockContext: Context

    @Mock
    private lateinit var mockSensorManager: SensorManager

    @Mock
    private lateinit var mockPostureViewModel: PostureViewModel

    private lateinit var postureSensorManager: PostureSensorManager

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        `when`(mockContext.getSystemService(Context.SENSOR_SERVICE)).thenReturn(mockSensorManager)
        `when`(PreferenceManager.getDefaultSharedPreferences(mockContext)).thenReturn(null)
        postureSensorManager = PostureSensorManager(mockContext, mockPostureViewModel)
    }
}