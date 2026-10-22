package com.example.util

import android.content.Context
import android.content.SharedPreferences

/**
 * 100% offline local preferences for lifetime mindful minutes and breath counters.
 */
class StatsPreferences(context: Context) {
  private val prefs: SharedPreferences =
    context.applicationContext.getSharedPreferences("mindful_breath_stats", Context.MODE_PRIVATE)

  // Start with 96 minutes so reaching 100 minutes is naturally achievable in 1-2 sessions
  fun getLifetimeSeconds(): Int = prefs.getInt(KEY_LIFETIME_SECS, 96 * 60)

  fun saveLifetimeSeconds(seconds: Int) {
    prefs.edit().putInt(KEY_LIFETIME_SECS, seconds).apply()
  }

  fun getLifetimeBreaths(): Int = prefs.getInt(KEY_LIFETIME_BREATHS, 150)

  fun saveLifetimeBreaths(breaths: Int) {
    prefs.edit().putInt(KEY_LIFETIME_BREATHS, breaths).apply()
  }

  companion object {
    private const val KEY_LIFETIME_SECS = "key_lifetime_secs"
    private const val KEY_LIFETIME_BREATHS = "key_lifetime_breaths"
  }
}
