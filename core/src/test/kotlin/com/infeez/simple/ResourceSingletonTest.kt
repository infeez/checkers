package com.infeez.simple

import org.junit.Assert.assertEquals
import org.junit.Test

class ResourceSingletonTest {
    @Test
    fun dispose_canBeCalledTwice() {
        ResourceSingleton.dispose()
        ResourceSingleton.dispose()
    }

    @Test
    fun getUniqueId_doesNotExhaustAfterManyObjects() {
        val ids = HashSet<Int>()

        repeat(1_500) {
            ids.add(ResourceSingleton.getUniqueId())
        }

        assertEquals(1_500, ids.size)
    }
}
