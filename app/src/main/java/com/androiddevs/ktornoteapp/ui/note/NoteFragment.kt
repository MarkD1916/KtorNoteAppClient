package com.androiddevs.ktornoteapp.ui.note

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.androiddevs.ktornoteapp.R
import com.androiddevs.ktornoteapp.adapters.AdapterActionListener
import com.androiddevs.ktornoteapp.adapters.NoteAdapter
import com.androiddevs.ktornoteapp.data.local.model.Note
import com.androiddevs.ktornoteapp.databinding.FragmentNotesBinding
import com.androiddevs.ktornoteapp.preferences.BasicAuthPreferences
import com.androiddevs.ktornoteapp.ui.auth.AuthFragmentDirections
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NoteFragment: Fragment() {
    private var _binding: FragmentNotesBinding? = null
    val mBinding get() = _binding!!

    @Inject
    lateinit var basicAuthSharedPreferences: BasicAuthPreferences

    private lateinit var noteAdapter: NoteAdapter

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

        setupRecyclerView()

        mBinding.fabAddNote.setOnClickListener {
            findNavController().navigate(NoteFragmentDirections.actionNoteFragmentToModificationNoteFragment(""))
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_notes, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.miLogout->{
                logout()
                val navOptions = NavOptions.Builder()
                    .setPopUpTo(R.id.noteFragment, true)
                    .build()
                findNavController().navigate(NoteFragmentDirections.actionNoteFragmentToAuthFragment(), navOptions)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupRecyclerView() = mBinding.rvNotes.apply {
        noteAdapter = NoteAdapter(requireContext(), object: AdapterActionListener{
            override fun itemClick(item: Note) {

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