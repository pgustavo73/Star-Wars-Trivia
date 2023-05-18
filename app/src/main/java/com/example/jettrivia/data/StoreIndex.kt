package com.example.jettrivia.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

class StoreIndex(private val context: Context) {

    companion object {
        private val Context.dataStoreIndex: DataStore<Preferences> by preferencesDataStore("questionIndex")
        val INDEX_KEY = stringPreferencesKey("questionIndex")

        private val Context.dataStoreAnswer: DataStore<Preferences> by preferencesDataStore("correctAnswer")
        val ANSWER_KEY = stringPreferencesKey("correctAnswer")
    }

    val getIndex = context.dataStoreIndex.data
        .map{ preferences ->
            preferences[INDEX_KEY] ?: ""
        }

    val getAnswer = context.dataStoreAnswer.data
        .map{ preferences ->
            preferences[ANSWER_KEY] ?: ""
        }

    suspend fun saveIndex(index: String){
        context.dataStoreIndex.edit { preferences ->
        preferences[INDEX_KEY] = index
        }
    }

    suspend fun saveAnswer(answer: String){
        context.dataStoreAnswer.edit { preferences ->
            preferences[ANSWER_KEY] = answer
        }
    }

}