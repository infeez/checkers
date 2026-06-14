package com.infeez.simple.entity

import com.badlogic.gdx.math.Rectangle
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

class GameObjectTest {
    @Test
    fun setRectangle_updatesInternalRectangle() {
        val gameObject = TestGameObject()
        val originalRectangle = gameObject.rectangle
        val newRectangle = Rectangle(2f, 3f, 4f, 5f)

        gameObject.setRectangle(newRectangle)

        assertSame(originalRectangle, gameObject.rectangle)
        assertEquals(2f, gameObject.x, 0.001f)
        assertEquals(3f, gameObject.y, 0.001f)
        assertEquals(4f, gameObject.width, 0.001f)
        assertEquals(5f, gameObject.height, 0.001f)
    }

    private class TestGameObject : GameObject(
        id = 1,
        x = 0f,
        y = 0f,
        width = 10f,
        height = 10f,
        batch = null,
    )
}
