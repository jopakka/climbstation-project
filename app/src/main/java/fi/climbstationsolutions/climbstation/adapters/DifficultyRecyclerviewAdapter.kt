package fi.climbstationsolutions.climbstation.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fi.climbstationsolutions.climbstation.R
import fi.climbstationsolutions.climbstation.databinding.SingleDifficultyItemBinding
import fi.climbstationsolutions.climbstation.network.profile.Profile
import fi.climbstationsolutions.climbstation.network.profile.ProfileHandler
import fi.climbstationsolutions.climbstation.ui.climb.CellClicklistener


class DifficultyRecyclerviewAdapter(
    private val cellClickListener: CellClicklistener,
    private val context: Context,
    sharedPrefPos: Int?
) : RecyclerView.Adapter<DifficultyRecyclerviewAdapter.ViewHolder>() {

    private var selectedItemPos = sharedPrefPos ?: -1
    private var lastItemSelectedPos = sharedPrefPos ?: -1

    class ViewHolder(private val binding: SingleDifficultyItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = SingleDifficultyItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }

        fun bind(item: Profile) {
            binding.profileItem = item
            binding.executePendingBindings()
        }

        fun selectedBg() {
            binding.singleDfItem.setBackgroundResource(R.drawable.layout_background)
        }

        fun defaultBg() {
            binding.singleDfItem.setBackgroundResource(R.color.transparent)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = ProfileHandler.readProfiles(context, R.raw.profiles)[position]
        holder.bind(item)

        if (selectedItemPos == position) {
            holder.selectedBg()
        } else {
            holder.defaultBg()
        }

        holder.itemView.setOnClickListener {
            cellClickListener.onCellClickListener(item)
            singleSelection(position)
        }
    }

    override fun getItemCount(): Int = ProfileHandler.readProfiles(context, R.raw.profiles).size

    private fun singleSelection(pos: Int) {
        selectedItemPos = pos
        lastItemSelectedPos = if (lastItemSelectedPos == -1)
            selectedItemPos
        else {
            notifyItemChanged(lastItemSelectedPos)
            selectedItemPos
        }
        notifyItemChanged(selectedItemPos)
    }
}