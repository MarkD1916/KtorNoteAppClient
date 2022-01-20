package com.androiddevs.ktornoteapp.ui.note

import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_USER
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.androiddevs.ktornoteapp.R
import com.androiddevs.ktornoteapp.adapters.AdapterActionListener
import com.androiddevs.ktornoteapp.adapters.NoteAdapter
import com.androiddevs.ktornoteapp.data.local.model.Note
import com.androiddevs.ktornoteapp.databinding.FragmentNotesBinding
import com.androiddevs.ktornoteapp.other.EventObserver
import com.androiddevs.ktornoteapp.preferences.BasicAuthPreferences
import com.androiddevs.ktornoteapp.ui.snackbar
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
                it.second?.let {notes->
                    noteAdapter.notes = notes
                }
                mBinding.swipeRefreshLayout.isRefreshing = false
            }
        ) {
            noteAdapter.notes = it
            mBinding.swipeRefreshLayout.isRefreshing = false
        }
        )
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

    private fun setupRecyclerView() = mBinding.rvNotes.apply {
        noteAdapter = NoteAdapter(requireContext(), object : AdapterActionListener {
            override fun itemClick(item: Note) {
                findNavController().navigate(
                    NoteFragmentDirections.actionNoteFragmentToModificationNoteFragment(item.id.toString())
                )
            }
        })

        adapter = noteAdapter
        layoutManager = LinearLayoutManager(requireContext())
    }

    private fun logout() {
        basicAuthSharedPreferences.setStoredEmail("")
        basicAuthSharedPreferences.setStoredPassword("")
    }
}