package com.androiddevs.ktornoteapp.ui.note

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.androiddevs.ktornoteapp.databinding.FragmentNotesBinding

class NoteFragment: Fragment() {
    private var _binding: FragmentNotesBinding? = null
    val mBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotesBinding.inflate(layoutInflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.fabAddNote.setOnClickListener {
            findNavController().navigate(NoteFragmentDirections.actionNoteFragmentToModificationNoteFragment(""))
        }
    }
}