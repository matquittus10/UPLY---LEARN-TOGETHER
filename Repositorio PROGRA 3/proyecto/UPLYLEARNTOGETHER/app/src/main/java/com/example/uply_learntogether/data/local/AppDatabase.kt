package com.example.uply_learntogether.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [TutorFavoritoEntity::class, TutorCacheEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tutorFavoritoDao(): TutorFavoritoDao
    abstract fun tutorCacheDao(): TutorCacheDao

    companion object {
        @Volatile
        private var instancia: AppDatabase? = null

        private val MIGRACION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `tutor_cache` (" +
                        "`id` TEXT NOT NULL, " +
                        "`nombre` TEXT NOT NULL, " +
                        "`materia` TEXT NOT NULL, " +
                        "`calificacion` TEXT NOT NULL, " +
                        "`resenas` TEXT NOT NULL, " +
                        "`precio` TEXT NOT NULL, " +
                        "PRIMARY KEY(`id`))"
                )
            }
        }

        fun obtener(context: Context): AppDatabase =
            instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "uply_learntogether.db"
                )
                    .addMigrations(MIGRACION_1_2)
                    .build()
                    .also { instancia = it }
            }
    }
}
