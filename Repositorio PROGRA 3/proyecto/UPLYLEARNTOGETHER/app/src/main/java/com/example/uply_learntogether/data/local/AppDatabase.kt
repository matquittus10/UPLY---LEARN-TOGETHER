package com.example.uply_learntogether.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.uply_learntogether.data.local.dao.MensajeDao
import com.example.uply_learntogether.data.local.dao.PublicacionDao
import com.example.uply_learntogether.data.local.dao.SesionDao
import com.example.uply_learntogether.data.local.dao.TutorCacheDao
import com.example.uply_learntogether.data.local.dao.TutorFavoritoDao
import com.example.uply_learntogether.data.local.entity.MensajeEntity
import com.example.uply_learntogether.data.local.entity.PublicacionEntity
import com.example.uply_learntogether.data.local.entity.SesionEntity
import com.example.uply_learntogether.data.local.entity.TutorCacheEntity
import com.example.uply_learntogether.data.local.entity.TutorFavoritoEntity

@Database(
    entities = [
        TutorFavoritoEntity::class,
        TutorCacheEntity::class,
        PublicacionEntity::class,
        MensajeEntity::class,
        SesionEntity::class
    ],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tutorFavoritoDao(): TutorFavoritoDao
    abstract fun tutorCacheDao(): TutorCacheDao
    abstract fun publicacionDao(): PublicacionDao
    abstract fun mensajeDao(): MensajeDao
    abstract fun sesionDao(): SesionDao

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

        private val MIGRACION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `mensajes` (" +
                        "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "`nombreContacto` TEXT NOT NULL, " +
                        "`contenido` TEXT NOT NULL, " +
                        "`fechaHoraTexto` TEXT NOT NULL, " +
                        "`fechaTimestamp` INTEGER NOT NULL, " +
                        "`esRemitenteUsuario` INTEGER NOT NULL)"
                )
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `sesiones` (" +
                        "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "`nombreContacto` TEXT NOT NULL, " +
                        "`materia` TEXT NOT NULL, " +
                        "`mesTexto` TEXT NOT NULL, " +
                        "`diaTexto` TEXT NOT NULL, " +
                        "`horarioTexto` TEXT NOT NULL, " +
                        "`modalidad` TEXT NOT NULL, " +
                        "`precioTexto` TEXT NOT NULL, " +
                        "`resumenCompleto` TEXT NOT NULL, " +
                        "`estado` TEXT NOT NULL)"
                )
            }
        }

        private val MIGRACION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE `publicaciones` ADD COLUMN `nombreAutor` TEXT NOT NULL DEFAULT 'Usuario UPLY'"
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
                    .addMigrations(MIGRACION_1_2, MIGRACION_2_3, MIGRACION_3_4, MIGRACION_4_5)
                    .build()
                    .also { instancia = it }
            }
    }
}
