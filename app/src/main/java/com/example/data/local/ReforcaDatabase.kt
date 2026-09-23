package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.ProgressTrackingEntity
import com.example.data.model.QuizQuestionEntity
import com.example.data.model.SubscriptionPlanEntity
import com.example.data.model.UserEntity

@Database(
    entities = [
        UserEntity::class,
        QuizQuestionEntity::class,
        SubscriptionPlanEntity::class,
        ProgressTrackingEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ReforcaDatabase : RoomDatabase() {

    abstract fun reforcaDao(): ReforcaDao

    companion object {
        @Volatile
        private var INSTANCE: ReforcaDatabase? = null

        fun getInstance(context: Context): ReforcaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ReforcaDatabase::class.java,
                    "reforca_database.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
