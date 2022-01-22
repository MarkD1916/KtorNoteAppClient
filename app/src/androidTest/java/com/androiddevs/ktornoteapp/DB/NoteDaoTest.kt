package com.vmakd1916gmail.com.ktornoteapp.DB


// for assertions on Java 8 types (Streams and java.util.Optional)
import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.androiddevs.ktornoteapp.data.local.DAO.NoteDAO
import com.androiddevs.ktornoteapp.data.local.model.Note
import com.google.common.truth.Truth.assertThat
import com.vmakd1916gmail.com.login_logout_register.DB.NoteDatabase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class NoteDaoTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()


    private lateinit var database: NoteDatabase
    private lateinit var dao: NoteDAO

    private val testDispatcher = TestCoroutineDispatcher()
    private val testScope = TestCoroutineScope(testDispatcher)

    /**
     *
     */
    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            NoteDatabase::class.java
        ).allowMainThreadQueries()
            .setTransactionExecutor(testDispatcher.asExecutor())
            .setQueryExecutor(testDispatcher.asExecutor())
            .build()
        dao = database.noteDAO()

    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertNote() = runBlockingTest {
        val note = Note(
            title = "Test title",
            content = "Test content",
            date = 123,
            owners = listOf("Test owners"),
            color = "000000",
            isSynced = false,

            )
        dao.insertNote(note)

        val element = dao.getAllNotes().take(1).toList()

        assertThat(element[0]).contains(note)
    }

    @Test
    fun deleteNote() = runBlockingTest {
        val note = Note(
            title = "Test title",
            content = "Test content",
            date = 123,
            owners = listOf("Test owners"),
            color = "000000",
            isSynced = false,
        )
        dao.insertNote(note)
        dao.deleteNoteById(note.id)
        val element = dao.getAllNotes().take(1).toList()
        assertThat(element).doesNotContain(note)
    }

//    @Test
//    fun deleteAllSyncedNotes() = testScope.runBlockingTest {
//
//        val note_synced = Note(
//            title = "Test title",
//            content = "Test content",
//            date = 123,
//            owners = listOf("Test owners"),
//            color = "000000",
//            isSynced = true,
//        )
//
//        val note_not_synced = Note(
//            title = "Test title",
//            content = "Test content",
//            date = 123,
//            owners = listOf("Test owners"),
//            color = "000000",
//            isSynced = false,
//        )
//        val dataStartedCondition = async { dao.getAllNotes().take(1).toList()[0] }
//        assertThat(dataStartedCondition.await()).isEmpty()
//        Log.d("deleteAllSyncedNotes", "dataStartedCondition ${dataStartedCondition.await()}")
//
//
//        dao.insertNote(note_synced)
//        val addSyncedNote = async { dao.getAllNotes().take(1).toList()[0] }.await()
//        assertThat(addSyncedNote).contains(note_synced)
//        Log.d(
//            "deleteAllSyncedNotes",
//            "dataAddSyncedNote id = ${addSyncedNote[0].id}, " +
//                    "isSynced = ${addSyncedNote[0].isSynced}, " +
//                    "list len = ${addSyncedNote.size}"
//        )
//
//        dao.insertNote(note_not_synced)
//        val addNotSyncedNote = async { dao.getAllNotes().take(1).toList()[0] }.await()
//        assertThat(addNotSyncedNote).contains(note_not_synced)
//        Log.d(
//            "deleteAllSyncedNotes",
//            "dataAddNotSyncedNote id = ${addNotSyncedNote[1].id}, " +
//                    "isSynced = ${addNotSyncedNote[1].isSynced}, " +
//                    "list len = ${addNotSyncedNote.size}"
//        )
//
//        dao.deleteAllSyncedNotes()
//        val dataAfterDeleteAllSyncedNotes = async { dao.getAllNotes().take(1).toList()[0] }.await()
//        Log.d(
//            "deleteAllSyncedNotes",
//            "dataAfterDeleteAllSyncedNotes id = ${dataAfterDeleteAllSyncedNotes[0].id}, " +
//                    "isSynced = ${dataAfterDeleteAllSyncedNotes[0].isSynced}, " +
//                    "list len = ${dataAfterDeleteAllSyncedNotes.size}"
//        )
//        assertThat(dataAfterDeleteAllSyncedNotes).doesNotContain(note_synced)
//    }

    @Test
    fun getNoteById() = runBlockingTest {
        val note = Note(
            title = "Test title",
            content = "Test content",
            date = 123,
            owners = listOf("Test owners"),
            color = "000000",
            isSynced = false,
        )
        dao.insertNote(note)
        val noteByID = dao.getNoteById(note.id)
        Log.d("getNoteById", "${note.javaClass.name},${noteByID?.javaClass?.name} ")
        assertThat(noteByID).isEqualTo(note)
    }

    @Test
    fun getNoteById_IfIdDoesNotExists() = runBlockingTest {
        val note = Note(
            title = "Test title",
            content = "Test content",
            date = 123,
            owners = listOf("Test owners"),
            color = "000000",
            isSynced = false,
        )
        dao.insertNote(note)
        val noteByID = dao.getNoteById(UUID.randomUUID().toString())
        assertThat(noteByID).isNull()
    }

//    @Test
//    fun getAllUnsyncedNotes() = testScope.runBlockingTest {
//        val note_synced = Note(
//            title = "Test title",
//            content = "Test content",
//            date = 123,
//            owners = listOf("Test owners"),
//            color = "000000",
//            isSynced = true,
//        )
//
//        val note_not_synced = Note(
//            title = "Test title",
//            content = "Test content",
//            date = 123,
//            owners = listOf("Test owners"),
//            color = "000000",
//            isSynced = false,
//        )
//        val dataStartedCondition = async { dao.getAllNotes().take(1).toList()[0] }
//        assertThat(dataStartedCondition.await()).isEmpty()
//        Log.d("getAllUnsyncedNotes", "dataStartedCondition ${dataStartedCondition.await()}")
//
//
//        dao.insertNote(note_synced)
//        val addSyncedNote = async { dao.getAllNotes().take(1).toList()[0] }.await()
//        assertThat(addSyncedNote).contains(note_synced)
//        Log.d(
//            "getAllUnsyncedNotes",
//            "dataAddSyncedNote id = ${addSyncedNote[0].id}, " +
//                    "isSynced = ${addSyncedNote[0].isSynced}, " +
//                    "list len = ${addSyncedNote.size}"
//        )
//
//        dao.insertNote(note_not_synced)
//        val addNotSyncedNote = async { dao.getAllNotes().take(1).toList()[0] }.await()
//        assertThat(addNotSyncedNote).contains(note_not_synced)
//        Log.d(
//            "getAllUnsyncedNotes",
//            "dataAddNotSyncedNote id = ${addNotSyncedNote[1].id}, " +
//                    "isSynced = ${addNotSyncedNote[1].isSynced}, " +
//                    "list len = ${addNotSyncedNote.size}"
//        )
//
//        val unsyncedNote = dao.getAllUnsyncedNotes()
//        assertThat(unsyncedNote).contains(note_not_synced)
//        Log.d(
//            "getAllUnsyncedNotes",
//            "unsyncedNote id = ${unsyncedNote[0].id}, " +
//                    "isSynced = ${unsyncedNote[0].isSynced}, " +
//                    "list len = ${unsyncedNote.size}"
//        )
//    }



}