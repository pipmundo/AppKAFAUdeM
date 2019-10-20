package com.notas.appkafa.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.notas.appkafa.tools.Constantes

@Database(entities = [Note::class], version = 4)
abstract class MyDatabase: RoomDatabase() {

    abstract fun notesDao(): NotesDao

    companion object {

        private var INSTANCE: MyDatabase? = null
        private val sLock = Any()

        fun getInstance(context: Context): MyDatabase {
            synchronized(sLock) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder<MyDatabase>(
                        context.applicationContext,
                        MyDatabase::class.java, "AppKAFA1.db"
                    )
                        .addMigrations(MIGRATION_1_2,MIGRATION_2_3,MIGRATION_3_4).allowMainThreadQueries()/*APPKAFA - Migraciones Implementadas*/
                        .build()
                }
                return INSTANCE as MyDatabase
            }
        }
/*APPKAFA - Se crea migraciones con el fin de contar con cambios en la base de datos sin perder las notas de versiones anteriores*/
        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // https://developer.android.com/reference/android/arch/persistence/room/ColumnInfo

                database.execSQL("ALTER TABLE  notas"
                        + " ADD COLUMN fechaCreacion DATETIME")
                database.execSQL("ALTER TABLE  notas"
                        + " ADD COLUMN fechaModificacion DATETIME")

                database.execSQL("UPDATE notas SET "
                        + " fechaCreacion=strftime('%Y-%m-%d %H:%M:%S', date('now')), " +
                        "fechaModificacion=strftime('%Y-%m-%d %H:%M:%S', date('now')) WHERE fechaCreacion IS NULL")
            }
        }

        val MIGRATION_2_3: Migration = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // https://developer.android.com/reference/android/arch/persistence/room/ColumnInfo

                database.execSQL("ALTER TABLE  notas"
                        + " ADD COLUMN fechaRecordatorio text")
            }
        }
        val MIGRATION_3_4: Migration = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // https://developer.android.com/reference/android/arch/persistence/room/ColumnInfo
                database.execSQL("ALTER TABLE  notas"
                        + " ADD COLUMN rutaImagen text")
            }
        }


    }

}
