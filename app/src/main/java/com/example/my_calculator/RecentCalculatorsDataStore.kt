package com.example.my_calculator

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.recentCalculatorsDataStore by preferencesDataStore("recent_calculators")

class RecentCalculatorsDataStore(private val context: Context) {
    private val RECENT_CALCULATORS_KEY = stringSetPreferencesKey("recent_calculators_list")

    // 从 DataStore 读取最近使用的计算器列表
    val recentCalculatorsFlow: Flow<List<String>> = context.recentCalculatorsDataStore.data.map { preferences ->
        preferences[RECENT_CALCULATORS_KEY]?.toList() ?: emptyList()
    }

    // 保存新的最近使用计算器
    suspend fun saveRecentCalculator(route: String) {
        context.recentCalculatorsDataStore.edit { preferences ->
            val current = preferences[RECENT_CALCULATORS_KEY]?.toMutableList() ?: mutableListOf()
            current.remove(route) // 避免重复
            current.add(0, route) // 新的放最前面
            if (current.size > 5) current.removeLast() // 最多保留5个
            preferences[RECENT_CALCULATORS_KEY] = current.toSet()
        }
    }
}
