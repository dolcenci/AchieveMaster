package com.example.achievemaster.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.achievemaster.data.model.FitnessGoal
import com.example.achievemaster.data.model.UserSession
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SharedPrefsHelper(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("AchieveMasterPrefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USERNAME = "username"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_NOTIFICATIONS = "notifications_enabled"
        private const val KEY_FITNESS_GOAL = "fitness_goal"
        private const val KEY_WORKOUT_FREQUENCY = "workout_frequency"
        private const val KEY_GOALS_LIST = "fitness_goals_list"
    }

    // ==================== USER SESSION ====================

    fun saveUserSession(session: UserSession) {
        sharedPreferences.edit().apply {
            putBoolean(KEY_IS_LOGGED_IN, session.isLoggedIn)
            putInt(KEY_USER_ID, session.userId)
            putString(KEY_USERNAME, session.username)
            putString(KEY_USER_EMAIL, session.email)
            apply()
        }
    }

    fun getUserSession(): UserSession {
        return UserSession(
            userId = sharedPreferences.getInt(KEY_USER_ID, 0),
            username = sharedPreferences.getString(KEY_USERNAME, "") ?: "",
            email = sharedPreferences.getString(KEY_USER_EMAIL, "") ?: "",
            isLoggedIn = sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
        )
    }

    fun clearSession() {
        sharedPreferences.edit().apply {
            putBoolean(KEY_IS_LOGGED_IN, false)
            putInt(KEY_USER_ID, 0)
            putString(KEY_USERNAME, "")
            putString(KEY_USER_EMAIL, "")
            apply()
        }
    }

    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    // ==================== GENERIC SETTINGS ====================

    fun saveSetting(key: String, value: Boolean) {
        sharedPreferences.edit().putBoolean(key, value).apply()
    }

    fun saveSetting(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    fun getSetting(key: String, defaultValue: Boolean): Boolean {
        return sharedPreferences.getBoolean(key, defaultValue)
    }

    fun getSetting(key: String, defaultValue: String): String {
        return sharedPreferences.getString(key, defaultValue) ?: defaultValue
    }

    fun areNotificationsEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_NOTIFICATIONS, true)
    }

    fun getFitnessGoal(): String {
        return sharedPreferences.getString(KEY_FITNESS_GOAL, "General Fitness")
            ?: "General Fitness"
    }

    fun getWorkoutFrequency(): String {
        return sharedPreferences.getString(KEY_WORKOUT_FREQUENCY, "3 times per week")
            ?: "3 times per week"
    }

    // ==================== FITNESS GOALS ====================

    /**
     * Saves a goal. If a goal with the same ID exists, it is replaced.
     * Otherwise, the new goal is added to the list.
     */
    fun saveGoal(goal: FitnessGoal) {
        val goals = getAllGoals().toMutableList()
        val existingIndex = goals.indexOfFirst { it.id == goal.id }
        if (existingIndex >= 0) {
            goals[existingIndex] = goal
        } else {
            goals.add(goal)
        }
        saveGoalsList(goals)
    }

    /**
     * Returns all stored fitness goals.
     */
    fun getAllGoals(): List<FitnessGoal> {
        val json = sharedPreferences.getString(KEY_GOALS_LIST, null) ?: return emptyList()
        return try {
            val type = object : TypeToken<List<FitnessGoal>>() {}.type
            Gson().fromJson<List<FitnessGoal>>(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Deletes a goal by its ID.
     */
    fun deleteGoal(goalId: String) {
        val goals = getAllGoals().filter { it.id != goalId }
        saveGoalsList(goals)
    }

    /**
     * Updates an existing goal with the given goal object.
     */
    fun updateGoal(goal: FitnessGoal) {
        saveGoal(goal)
    }

    /**
     * Internal helper - saves the full goals list as JSON.
     */
    private fun saveGoalsList(goals: List<FitnessGoal>) {
        val json = Gson().toJson(goals)
        sharedPreferences.edit().putString(KEY_GOALS_LIST, json).apply()
    }
}