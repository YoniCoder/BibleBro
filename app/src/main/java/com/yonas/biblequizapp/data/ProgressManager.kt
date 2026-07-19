package com.yonas.biblequizapp.data

import android.content.Context
import android.content.SharedPreferences

class ProgressManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("BibleQuizProgress", Context.MODE_PRIVATE)

    fun saveScore(section: String, level: Int, score: Int) {
        val editor = prefs.edit()
        // Save best score only if higher than existing
        val currentBest = getScore(section, level)
        if (score > currentBest) {
            editor.putInt("${section}_level_${level}_score", score)
        }
        // If passed (score >= 6), mark as passed permanently
        if (score >= 6) {
            editor.putBoolean("${section}_level_${level}_passed", true)
            // Unlock next level if passed
            val currentMax = getMaxUnlockedLevel(section)
            if (level >= currentMax) {
                editor.putInt("${section}_max_level", level + 1)
            }
        }
        editor.apply()
    }

    fun getScore(section: String, level: Int): Int {
        return prefs.getInt("${section}_level_${level}_score", 0)
    }

    fun isLevelPassed(section: String, level: Int): Boolean {
        return prefs.getBoolean("${section}_level_${level}_passed", false)
    }

    fun isLevelUnlocked(section: String, level: Int): Boolean {
        val maxUnlocked = getMaxUnlockedLevel(section)
        return level <= maxUnlocked
    }

    fun getMaxUnlockedLevel(section: String): Int {
        return prefs.getInt("${section}_max_level", 1)
    }

    fun getTotalPassed(section: String): Int {
        var count = 0
        for (i in 1..5) {
            if (isLevelPassed(section, i)) count++
        }
        return count
    }

    fun resetProgress() {
        prefs.edit().clear().apply()
    }
}