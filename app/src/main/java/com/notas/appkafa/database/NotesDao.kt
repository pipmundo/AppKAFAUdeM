package com.notas.appkafa.database

import androidx.room.*
import androidx.room.Update



@Dao
interface NotesDao {
   @Query("SELECT * FROM notas WHERE eliminada='0' ORDER BY _id DESC")
   abstract fun getNotes(): List<Note>

   @Query("SELECT * FROM notas where _id = :noteId")
   abstract fun getNote(noteId:Int): Note

   @Query("SELECT COUNT(1) FROM notas")
   fun getCount(): Int

   @Insert(onConflict = OnConflictStrategy.REPLACE)
   abstract fun insertNote(note: Note)

   @Query("SELECT * FROM notas WHERE eliminada='1'")
   abstract fun getNotesEliminated(): List<Note>

   @Update(onConflict = OnConflictStrategy.REPLACE)
   fun updateNote(note: Note)

   @Delete
   fun deleteNote(note: Note)


}

