package fi.climbstationsolutions.climbstation.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import fi.climbstationsolutions.climbstation.R
import fi.climbstationsolutions.climbstation.database.ClimbProfileWithSteps
import fi.climbstationsolutions.climbstation.database.SessionWithData
import fi.climbstationsolutions.climbstation.databinding.SingleDifficultyItemBinding
import fi.climbstationsolutions.climbstation.ui.climb.CellClickListener
import fi.climbstationsolutions.climbstation.ui.climb.DifficultyProfileDataItem
import fi.climbstationsolutions.climbstation.ui.profile.DataItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.YearMonth
import java.util.*


class DifficultyRecyclerviewAdapter(private val cellClickListener: CellClickListener) :
    ListAdapter<DifficultyProfileDataItem, RecyclerView.ViewHolder>(DifficultyProfileDataItemCallBack()) {
    companion object {
        private const val ITEM_DIFFICULTY = 0
        private const val ITEM_HEADER = 1
    }

    private var selectedPosition = 0

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    fun addHeaderAndSubmitList(list: List<ClimbProfileWithSteps>?) {
        adapterScope.launch {
            val items = when (list?.isNullOrEmpty()) {
                null, true -> emptyList()
                else -> {
                    list.mapIndexed { i, profileWithSteps ->
                        DifficultyProfileDataItem.DifficultyItem(profileWithSteps)
                    }
                }
            }
            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
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
            binding.singleDfItem.setBackgroundResource(R.drawable.layout_background_default)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {

        }
    }

    override fun getItemCount(): Int = mProfileList.size

    private fun singleSelection(pos: Int) {
        notifyItemChanged(selectedPosition)
        selectedPosition = pos
        notifyItemChanged(pos)
    }
}

class DifficultyProfileDataItemCallBack : DiffUtil.ItemCallback<DifficultyProfileDataItem>() {
    override fun areItemsTheSame(oldItem: DifficultyProfileDataItem, newItem: DifficultyProfileDataItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: DifficultyProfileDataItem, newItem: DifficultyProfileDataItem): Boolean {
        return oldItem == newItem
    }
}