package fi.climbstationsolutions.climbstation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fi.climbstationsolutions.climbstation.R
import fi.climbstationsolutions.climbstation.database.ClimbProfileWithSteps
import fi.climbstationsolutions.climbstation.databinding.SingleDifficultyItemBinding
import fi.climbstationsolutions.climbstation.ui.climb.CellClickListener


class DifficultyRecyclerviewAdapter(private val cellClickListener: CellClickListener) :
    RecyclerView.Adapter<DifficultyRecyclerviewAdapter.ViewHolder>() {

    private val mProfileList: MutableList<ClimbProfileWithSteps> = mutableListOf()
    private var selectedPosition = 0

    fun addProfiles(list: List<ClimbProfileWithSteps>) {
        val startPos = mProfileList.size
        mProfileList.addAll(list)
        notifyItemRangeInserted(startPos, list.size)
    }

    class ViewHolder(private val binding: SingleDifficultyItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = SingleDifficultyItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }

        fun bind(item: ClimbProfileWithSteps) {
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
        val item = mProfileList[position]
        holder.bind(item)

        if (selectedPosition == position) {
            holder.selectedBg()
        } else {
            holder.defaultBg()
        }

        holder.itemView.setOnClickListener {
            cellClickListener.onCellClickListener(item)
            singleSelection(position)
        }
    }

    override fun getItemCount(): Int = mProfileList.size

    private fun singleSelection(pos: Int) {
        notifyItemChanged(selectedPosition)
        selectedPosition = pos
        notifyItemChanged(pos)
    }
}