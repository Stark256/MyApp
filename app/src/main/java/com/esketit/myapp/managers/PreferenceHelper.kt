package com.esketit.myapp.managers

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

abstract class PreferenceHelper(){


    fun getPref(context: Context): SharedPreferences{
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

    protected fun getStringValue(context: Context, key: String): String? = getPref(context).getString(key, null)

    protected fun <U> storeValue(context: Context, key: String, value: U?) {
        if (value == null) { getPref(context).edit().remove(key).apply() }
        else { getPref(context).edit().putString(key, value.toString()).apply() }

    }

}
