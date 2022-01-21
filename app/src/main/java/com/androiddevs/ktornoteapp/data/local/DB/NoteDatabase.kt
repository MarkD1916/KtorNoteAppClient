package com.vmakd1916gmail.com.login_logout_register.DB

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.androiddevs.ktornoteapp.data.local.DAO.NoteDAO
import com.androiddevs.ktornoteapp.data.local.model.LocallyDeletedNoteID
import com.androiddevs.ktornoteapp.data.local.model.Note

@Database(
    entities = [Note::class, LocallyDeletedNoteID::class],
    version = 1
)
@TypeConverters(TypeConverter::class)
abstract class NoteDatabase : RoomDatabase() {
    abstract fun noteDAO(): NoteDAO
}