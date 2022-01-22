package com.androiddevs.ktornoteapp.ui.notedetail

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.androiddevs.ktornoteapp.R
import com.androiddevs.ktornoteapp.data.local.model.Note
import com.androiddevs.ktornoteapp.databinding.FragmentNoteDetailBinding
import com.androiddevs.ktornoteapp.other.EventObserver
import com.androiddevs.ktornoteapp.ui.dialogs.AddOwnerDialog
import com.androiddevs.ktornoteapp.ui.snackbar
import dagger.hilt.android.AndroidEntryPoint
import io.noties.markwon.Markwon

const val ADD_OWNER_DIALOG_TAG = "ADD_OWNER_DIALOG_TAG"

@AndroidEntryPoint
class NoteDetailFragment : Fragment() {
    private var _binding: FragmentNoteDetailBinding? = null
    val mBinding get() = _binding!!

    private var curNote: Note? = null

    private val viewModel: NoteDetailViewModel by viewModels()

    private val args: NoteDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNoteDetailBinding.inflate(layoutInflater, container, false)
        setHasOptionsMenu(true)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToObservers()
        mBinding.fabEditNote.setOnClickListener {
            findNavController().navigate(
                NoteDetailFragmentDirections.actionNoteDetailFragmentToModificationNoteFragment2(
                    args.id
                )
            )
        }

        if (savedInstanceState != null) {
            val addOwnerDialog = parentFragmentManager.findFragmentByTag(ADD_OWNER_DIALOG_TAG)
                    as AddOwnerDialog?
            addOwnerDialog?.setPositiveListener {
                addOwnerToCurNote(it)
            }
        }
    }

    private fun showAddOwnerDialog() {
        AddOwnerDialog().apply {
            setPositiveListener {
                addOwnerToCurNote(it)
            }
        }.show(parentFragmentManager, ADD_OWNER_DIALOG_TAG)
    }

    private fun addOwnerToCurNote(email: String) {
        curNote?.let { note ->
            viewModel.addOwnerToNote(email, note.id)
        }
    }

    private fun setMarkdownText(text: String) {
        val markwon = Markwon.create(requireContext())
        val markdown = markwon.toMarkdown(text)
        markwon.setParsedMarkdown(mBinding.tvNoteContent, markdown)
    }

    private fun subscribeToObservers() {
        viewModel.observeNoteByID(args.id).observe(viewLifecycleOwner, Observer {
            it?.let { note ->
                mBinding.tvNoteTitle.text = note.title
                setMarkdownText(note.content)
                curNote = note
            } ?: snackbar("Note not found")
        })

        viewModel.addOwnerStatus.observe(viewLifecycleOwner, EventObserver(
            onLoading = {
                mBinding.addOwnerProgressBar.visibility = View.VISIBLE
            },
            onError = {
                mBinding.addOwnerProgressBar.visibility = View.GONE
                snackbar(it.first)
            }
        ){
            mBinding.addOwnerProgressBar.visibility = View.GONE
            snackbar(it.message)
        }
        )
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.note_detail_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.miAddOwner -> showAddOwnerDialog()
        }
        return super.onOptionsItemSelected(item)
    }

}