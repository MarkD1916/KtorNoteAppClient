package com.androiddevs.ktornoteapp.ui.notemodification

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.androiddevs.ktornoteapp.R
import com.androiddevs.ktornoteapp.data.local.model.Note
import com.androiddevs.ktornoteapp.databinding.FragmentAddModifiedNoteBinding
import com.androiddevs.ktornoteapp.other.Constants.DEFAULT_NOTE_COLOR
import com.androiddevs.ktornoteapp.other.asyncUtil.EventObserver
import com.androiddevs.ktornoteapp.preferences.BasicAuthPreferences
import com.androiddevs.ktornoteapp.ui.dialogs.ColorPickerDialogFragment
import com.androiddevs.ktornoteapp.ui.snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

const val FRAGMENT_TAG = "ModificationNoteFragment"

@AndroidEntryPoint
class ModificationNoteFragment : Fragment() {
    private var _binding: FragmentAddModifiedNoteBinding? = null
    val mBinding get() = _binding!!

    private val viewModel: ModificationNoteViewModel by viewModels()

    private val args: ModificationNoteFragmentArgs by navArgs()

    private var curNote: Note? = null
    private var curNoteColor = DEFAULT_NOTE_COLOR

    @Inject
    lateinit var basicAuthSharedPreferences: BasicAuthPreferences


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddModifiedNoteBinding.inflate(layoutInflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (args.id.isNotEmpty()) {
            viewModel.getNoteById(args.id)
            subscribeToObservers()
        }

        if (savedInstanceState != null) {
            val colorPickerDialog = parentFragmentManager.findFragmentByTag(FRAGMENT_TAG)
                    as ColorPickerDialogFragment?
            colorPickerDialog?.setPositiveListener {
                changeViewNoteColor(it)
            }
        }

        mBinding.viewNoteColor.setOnClickListener {
            ColorPickerDialogFragment().apply {
                setPositiveListener {
                    changeViewNoteColor(it)
                }
            }.show(parentFragmentManager, FRAGMENT_TAG)
        }

    }

    private fun changeViewNoteColor(colorString: String) {
        val drawable =
            ResourcesCompat.getDrawable(resources, R.drawable.circle_shape, null)
        drawable?.let {
            val wrappedDrawable = DrawableCompat.wrap(it)
            val color = Color.parseColor("#$colorString")
            DrawableCompat.setTint(wrappedDrawable, color)
            mBinding.viewNoteColor.background = wrappedDrawable
            curNoteColor = colorString
        }
    }

    private fun subscribeToObservers() {
        viewModel.note.observe(viewLifecycleOwner, EventObserver(
            onError = {
                snackbar(it.first)
            },
            onLoading = {
                /* NO-OP */
            }
        ) {
            val note = it
            curNote = note
            with(mBinding) {
                etNoteTitle.setText(note.title)
                etNoteContent.setText(note.content)
                changeViewNoteColor(note.color)
            }
        }
        )
    }

    override fun onPause() {
        super.onPause()
        saveNote()
    }

    private fun saveNote() {
        val authEmail = basicAuthSharedPreferences.getStoredEmail()
        with(mBinding) {
            val title = etNoteTitle.text.toString()
            val content = etNoteContent.text.toString()
            if (title.isEmpty() || content.isEmpty()) {
                return
            }
            val date = System.currentTimeMillis()
            val color = curNoteColor
            val id = curNote?.id ?: UUID.randomUUID().toString()
            val owners = curNote?.owners ?: listOf(authEmail)
            val note = Note(title, content, date, owners, color, id = id)
            viewModel.insertNote(note)
        }
    }


}