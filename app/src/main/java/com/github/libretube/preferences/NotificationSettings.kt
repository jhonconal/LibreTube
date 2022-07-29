package com.github.libretube.preferences

import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.github.libretube.R
import com.github.libretube.activities.SettingsActivity
import com.github.libretube.util.NotificationHelper

class NotificationSettings : PreferenceFragmentCompat() {
    val TAG = "SettingsFragment"

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.notification_settings, rootKey)

        val settingsActivity = activity as SettingsActivity
        settingsActivity.changeTopBarText(getString(R.string.notifications))

        val notificationsEnabled = findPreference<SwitchPreferenceCompat>(PreferenceKeys.NOTIFICATION_ENABLED)
        notificationsEnabled?.setOnPreferenceChangeListener { _, _ ->
            NotificationHelper.enqueueWork(requireContext())
            true
        }

        val checkingFrequency = findPreference<ListPreference>(PreferenceKeys.CHECKING_FREQUENCY)
        checkingFrequency?.setOnPreferenceChangeListener { _, _ ->
            NotificationHelper.enqueueWork(requireContext())
            true
        }
    }
}