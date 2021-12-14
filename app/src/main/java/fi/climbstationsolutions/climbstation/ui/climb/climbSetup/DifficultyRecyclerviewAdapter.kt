package fi.climbstationsolutions.climbstation.ui.climb.climbSetup

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import fi.climbstationsolutions.climbstation.R
import fi.climbstationsolutions.climbstation.database.ClimbProfileWithSteps
import fi.climbstationsolutions.climbstation.databinding.DifficultyListHeader2Binding
import fi.climbstationsolutions.climbstation.databinding.SingleDifficultyItemBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DifficultyRecyclerviewAdapter(private val cellClickListener: CellClickListener) :
    ListAdapter<DifficultyProfileDataItem, RecyclerView.ViewHolder>(
        DifficultyProfileDataItemCallBack()
    ) {
    companion object {
        private const val ITEM_DIFFICULTY = 0
        private const val ITEM_HEADER1 = 1
        private const val ITEM_HEADER2 = 2
    }

    private var selectedPosition = 0

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    fun addHeaderAndSubmitList(list: List<ClimbProfileWithSteps>?, listener: (sProfId: Long) -> Unit) {
        adapterScope.launch {
            val items = when (list?.isNullOrEmpty()) {
                null, true -> listOf(DifficultyProfileDataItem.DifficultyHeader1Item)
                else -> {
                    val mList = mutableListOf<DifficultyProfileDataItem>()
                    mList.add(DifficultyProfileDataItem.DifficultyHeader1Item)

                    val defaults = list.filter { it.profile.isDefault }
                        .map { DifficultyProfileDataItem.DifficultyItem(it) }
                    val customs = list.filter { !it.profile.isDefault }
                        .map { DifficultyProfileDataItem.DifficultyItem(it) }
                    if (customs.isNotEmpty()) {
                        mList.add(DifficultyProfileDataItem.DifficultyHeader2Item(false))
                        mList.addAll(customs)
                    }
                    mList.add(DifficultyProfileDataItem.DifficultyHeader2Item(true))
                    mList.addAll(defaults)

                    selectedPosition = 2
                    listener(mList[selectedPosition].id)

                    mList
                }
            }
            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
    }

    class Header1ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        companion object {
            fun from(parent: ViewGroup): Header1ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.difficulty_list_header1, parent, false)
                return Header1ViewHolder(view)
            }
        }
    }

    class Header2ViewHolder(private val binding: DifficultyListHeader2Binding) :
        RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun from(parent: ViewGroup): Header2ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = DifficultyListHeader2Binding.inflate(layoutInflater)
                return Header2ViewHolder(binding)
            }
        }

        fun bind(isDefault: Boolean) {
            binding.isDefault = isDefault
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_HEADER1 -> Header1ViewHolder.from(parent)
            ITEM_HEADER2 -> Header2ViewHolder.from(parent)
            ITEM_DIFFICULTY -> ViewHolder.from(parent)
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {
                val item = getItem(position) as DifficultyProfileDataItem.DifficultyItem
                holder.bind(item.climbProfileWithSteps)

                if (selectedPosition == position) {
                    holder.selectedBg()
                } else {
                    holder.defaultBg()
                }

                holder.itemView.setOnClickListener {
                    Log.d("DRVA", "holder.itemview clicked")
                    cellClickListener.onCellClickListener(item.climbProfileWithSteps)
                    singleSelection(position)
                }
            }
            is Header2ViewHolder -> {
                val item = getItem(position) as DifficultyProfileDataItem.DifficultyHeader2Item
                holder.bind(item.default)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is DifficultyProfileDataItem.DifficultyHeader1Item -> ITEM_HEADER1
            is DifficultyProfileDataItem.DifficultyHeader2Item -> ITEM_HEADER2
            is DifficultyProfileDataItem.DifficultyItem -> ITEM_DIFFICULTY
        }
    }

    private fun singleSelection(pos: Int) {
        notifyItemChanged(selectedPosition)
        selectedPosition = pos
        notifyItemChanged(pos)
    }
}

class DifficultyProfileDataItemCallBack : DiffUtil.ItemCallback<DifficultyProfileDataItem>() {
    override fun areItemsTheSame(
        oldItem: DifficultyProfileDataItem,
        newItem: DifficultyProfileDataItem
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: DifficultyProfileDataItem,
        newItem: DifficultyProfileDataItem
    ): Boolean {
        return oldItem == newItem
    }
}