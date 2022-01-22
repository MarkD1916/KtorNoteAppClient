package com.androiddevs.ktornoteapp.ui.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.androiddevs.ktornoteapp.R
import com.androiddevs.ktornoteapp.databinding.EditTextEmailBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.skydoves.colorpickerview.ColorEnvelope

import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener

class AddOwnerDialog : DialogFragment() {

    private var positiveListener: ((String) -> Unit)? = null

    private var _binding: EditTextEmailBinding? = null
    val mBinding get() = _binding!!

    fun setPositiveListener(listener: (String) -> Unit) {
        positiveListener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = EditTextEmailBinding.inflate(LayoutInflater.from(context))

        return MaterialAlertDialogBuilder(requireContext())
            .setIcon(R.drawable.ic_add_person)
            .setTitle("Add Owner to Note")
            .setMessage(
                "Enter an E-Mail of a person you want to share the note with." +
                        "This person will be able read and edit the note."
            )
            .setView(mBinding.root)
            .setPositiveButton("Add") { _, _ ->
                val email =
                    mBinding.root.findViewById<EditText>(R.id.etAddOwnerEmail).text.toString()
                positiveListener?.let { yes ->
                    yes(email)
                }
            }
            .setNegativeButton("Cancel") { dialogInterface, _ ->
                dialogInterface.cancel()
            }
            .create()

    }
}