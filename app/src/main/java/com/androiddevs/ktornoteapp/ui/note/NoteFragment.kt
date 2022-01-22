package com.androiddevs.ktornoteapp.ui.note

import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_USER
import android.graphics.Canvas
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androiddevs.ktornoteapp.R
import com.androiddevs.ktornoteapp.adapters.AdapterActionListener
import com.androiddevs.ktornoteapp.adapters.NoteAdapter
import com.androiddevs.ktornoteapp.data.local.model.Note
import com.androiddevs.ktornoteapp.databinding.FragmentNotesBinding
import com.androiddevs.ktornoteapp.other.asyncUtil.EventObserver
import com.androiddevs.ktornoteapp.preferences.BasicAuthPreferences
import com.androiddevs.ktornoteapp.ui.snackbar
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NoteFragment : Fragment() {
    private var _binding: FragmentNotesBinding? = null
    val mBinding get() = _binding!!

    @Inject
    lateinit var basicAuthSharedPreferences: BasicAuthPreferences

    private lateinit var noteAdapter: NoteAdapter

    private val viewModel: NoteViewModel by viewModels()

    private val swipingItem = MutableLiveData(false)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotesBinding.inflate(layoutInflater, container, false)
        setHasOptionsMenu(true)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().requestedOrientation = SCREEN_ORIENTATION_USER
        setupRecyclerView()
        subscribeToObservers()
        setupSwipeRefreshLayout()
        mBinding.fabAddNote.setOnClickListener {
            findNavController().navigate(
                NoteFragmentDirections.actionNoteFragmentToModificationNoteFragment(
                    ""
                )
            )
        }
    }

    private fun subscribeToObservers() {
        viewModel.allNotes.observe(viewLifecycleOwner, EventObserver(
            onLoading = {
                it?.let { notes ->
                    noteAdapter.notes = notes
                }
                mBinding.swipeRefreshLayout.isRefreshing = true
            },
            onError = {
                snackbar(it.first)
                it.second?.let { notes ->
                    noteAdapter.notes = notes
                }
                mBinding.swipeRefreshLayout.isRefreshing = false
            }
        ) {
            noteAdapter.notes = it
            mBinding.swipeRefreshLayout.isRefreshing = false
        }
        )

        swipingItem.observe(viewLifecycleOwner, Observer {
            mBinding.swipeRefreshLayout.isEnabled = !it
        })
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_notes, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.miLogout -> {
                logout()
                val navOptions = NavOptions.Builder()
                    .setPopUpTo(R.id.noteFragment, true)
                    .build()
                findNavController().navigate(
                    NoteFragmentDirections.actionNoteFragmentToAuthFragment(),
                    navOptions
                )
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
        0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
    ) {
        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                swipingItem.postValue(isCurrentlyActive)
            }
        }

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.layoutPosition
            val note = noteAdapter.notes[position]
            viewModel.deleteNote(note.id)
            Snackbar.make(requireView(), "Note was successfully deleted", Snackbar.LENGTH_LONG)
                .apply {
                    setAction("Undo") {
                        viewModel.insertNote(note)
                        viewModel.deleteLocallyDeletedNoteID(note.id)
                    }
                    show()
                }

        }
    }

    private fun setupSwipeRefreshLayout() {
        mBinding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.syncAllNotes()
        }
    }

    private fun setupRecyclerView() = mBinding.rvNotes.apply {
        noteAdapter = NoteAdapter(requireContext(), object : AdapterActionListener {
            override fun itemClick(item: Note) {
                findNavController().navigate(
                    NoteFragmentDirections.actionNoteFragmentToNoteDetailFragment(item.id)
                )
            }
        })

        adapter = noteAdapter
        layoutManager = LinearLayoutManager(requireContext())
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(this)
    }

    private fun logout() {
        basicAuthSharedPreferences.setStoredEmail("")
        basicAuthSharedPreferences.setStoredPassword("")
    }
}