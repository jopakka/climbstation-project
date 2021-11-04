package fi.climbstationsolutions.climbstation.ui.climb

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fi.climbstationsolutions.climbstation.R
import fi.climbstationsolutions.climbstation.databinding.SingleDifficultyItemBinding


class DifficultyRecyclerviewAdapter(private val cellClickListener: CellClicklistener) : RecyclerView.Adapter<DifficultyRecyclerviewAdapter.ViewHolder>() {

    class ViewHolder(private val binding: SingleDifficultyItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = SingleDifficultyItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }

        fun bind(item: DifficultyProfile) {
            binding.difficultyItem = item
            binding.executePendingBindings()
        }

        fun selectedBg() {
            binding.singleDfItem.setBackgroundResource(R.drawable.layout_background)
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = GlobalModel.ADJUSTMENTS[position]
        holder.bind(item)
        holder.itemView.setOnClickListener {
            cellClickListener.onCellClickListener(item)
            holder.selectedBg()
        }
    }

    override fun getItemCount(): Int = GlobalModel.ADJUSTMENTS.size
}