package com.infeez.simple.input

interface PCInputProcessor {
    fun mouseDrag(x: Float, y: Float, pointer: Int): Boolean

    fun mouseDown(x: Float, y: Float, pointer: Int, mouseButton: Int): Boolean

    fun mouseUp(x: Float, y: Float, pointer: Int, mouseButton: Int): Boolean

    fun touchCancelled(x: Float, y: Float, pointer: Int, mouseButton: Int): Boolean
}
