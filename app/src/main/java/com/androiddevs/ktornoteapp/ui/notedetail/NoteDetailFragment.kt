package com.androiddevs.ktornoteapp.ui.notedetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.androiddevs.ktornoteapp.databinding.FragmentNoteDetailBinding

class NoteDetailFragment: Fragment() {
    private var _binding: FragmentNoteDetailBinding? = null
    val mBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNoteDetailBinding.inflate(layoutInflater, container, false)
        return mBinding.root
    }
}