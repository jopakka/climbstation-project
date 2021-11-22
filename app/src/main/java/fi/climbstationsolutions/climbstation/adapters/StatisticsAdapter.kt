package fi.climbstationsolutions.climbstation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fi.climbstationsolutions.climbstation.database.SessionWithData
import fi.climbstationsolutions.climbstation.databinding.StatisticsSessionHistoryItemBinding
import fi.climbstationsolutions.climbstation.ui.profile.SessionClickListener

class StatisticsAdapter(private val list: List<SessionWithData>, private val clickListener: SessionClickListener) : RecyclerView.Adapter<StatisticsAdapter.ViewHolder>() {

    class ViewHolder(private val binding: StatisticsSessionHistoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = StatisticsSessionHistoryItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }

        fun bind(item: SessionWithData) {
            binding.sessionItem = item
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
        holder.itemView.setOnClickListener {
            clickListener.onClick(item.session.id)
        }
    }

    override fun getItemCount(): Int = list.size
}