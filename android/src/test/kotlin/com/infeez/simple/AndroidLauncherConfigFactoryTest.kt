package com.infeez.simple

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AndroidLauncherConfigFactoryTest {
    @Test
    fun create_disablesUnusedSensorsAndEnablesImmersiveMode() {
        val config = AndroidLauncherConfigFactory.create()

        assertFalse(config.useAccelerometer)
        assertFalse(config.useCompass)
        assertFalse(config.useGyroscope)
        assertTrue(config.useImmersiveMode)
    }
}
