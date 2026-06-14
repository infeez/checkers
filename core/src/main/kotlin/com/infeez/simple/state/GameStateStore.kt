package com.infeez.simple.state

import com.badlogic.gdx.Gdx

class GameStateStore(
    private val preferencesName: String = "checkers_state",
) {
    fun load(): GameState? {
        val preferences = Gdx.app?.getPreferences(preferencesName) ?: return null
        return GameStateSerializer.deserialize(preferences.getString(KEY_STATE, null))
    }

    fun save(state: GameState) {
        val preferences = Gdx.app?.getPreferences(preferencesName) ?: return
        preferences.putString(KEY_STATE, GameStateSerializer.serialize(state))
        preferences.flush()
    }

    fun clear() {
        val preferences = Gdx.app?.getPreferences(preferencesName) ?: return
        preferences.remove(KEY_STATE)
        preferences.flush()
    }

    private companion object {
        const val KEY_STATE = "state"
    }
}
