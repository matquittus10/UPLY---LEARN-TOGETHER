package com.example.uply_learntogether.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [TutorFavoritoEntity::class, TutorCacheEntity::class, PublicacionEntity::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tutorFavoritoDao(): TutorFavoritoDao
    abstract fun tutorCacheDao(): TutorCacheDao
    abstract fun publicacionDao(): PublicacionDao

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

        private val MIGRACION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `publicaciones` (" +
                        "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "`tipo` TEXT NOT NULL, " +
                        "`titulo` TEXT NOT NULL, " +
                        "`materia` TEXT NOT NULL, " +
                        "`descripcion` TEXT NOT NULL, " +
                        "`fechaCreacion` INTEGER NOT NULL)"
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
                    .addMigrations(MIGRACION_1_2, MIGRACION_2_3)
                    .build()
                    .also { instancia = it }
            }
    }
}
