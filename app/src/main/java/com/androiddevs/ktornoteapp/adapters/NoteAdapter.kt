package com.androiddevs.ktornoteapp.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.androiddevs.ktornoteapp.R
import com.androiddevs.ktornoteapp.data.local.model.Note
import com.androiddevs.ktornoteapp.databinding.ItemNoteBinding
import java.text.SimpleDateFormat
import java.util.*

interface AdapterActionListener {

    fun itemClick(item: Note) {

    }
}

class NoteAdapter(val context: Context, private val actionListener: AdapterActionListener) :
    RecyclerView.Adapter<NoteAdapter.NoteViewHolder>(), View.OnClickListener {
    inner class NoteViewHolder(private val binding: ItemNoteBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Note) {
            this.apply {
                bindItem(item)
            }
        }

        private fun bindItem(item: Note) {
            with(this.binding) {
                tvTitle.text = item.title
                if (!item.isSynced) {
                    ivSynced.setImageResource(R.drawable.ic_cross)
                    tvSynced.text = "Not Synced"
                } else {
                    ivSynced.setImageResource(R.drawable.ic_check)
                    tvSynced.text = "Synced"
                }

                val dateFormat = SimpleDateFormat("dd.MM.yy, HH:mm", Locale.getDefault())
                val dateString = dateFormat.format(item.date)
                tvDate.text = dateString

                val drawable =
                    ResourcesCompat.getDrawable(context.resources, R.drawable.circle_shape, null)
                drawable?.let {
                    val wrappedDrawable = DrawableCompat.wrap(it)
                    val color = Color.parseColor("#${item.color}")
                    DrawableCompat.setTint(wrappedDrawable, color)
                    viewNoteColor.background = it
                }

            }
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    var notes: List<Note>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding =
            ItemNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(notes[position])

    }

    override fun getItemCount(): Int {
        return notes.size
    }

    override fun onClick(v: View) {
        val item = v.tag as Note
        when (v.id) {
            R.id.noteItemId -> {
                actionListener.itemClick(item)
            }
        }
    }


}