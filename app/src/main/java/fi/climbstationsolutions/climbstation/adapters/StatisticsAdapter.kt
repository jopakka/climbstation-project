package fi.climbstationsolutions.climbstation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import fi.climbstationsolutions.climbstation.R
import fi.climbstationsolutions.climbstation.database.SessionWithData
import fi.climbstationsolutions.climbstation.databinding.SessionListHeaderBinding
import fi.climbstationsolutions.climbstation.databinding.StatisticsSessionHistoryItemBinding
import fi.climbstationsolutions.climbstation.ui.profile.DataItem
import fi.climbstationsolutions.climbstation.ui.profile.SessionClickListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.YearMonth
import java.util.*

class StatisticsAdapter(
    private val clickListener: SessionClickListener
) : ListAdapter<DataItem, RecyclerView.ViewHolder>(SessionItemCallBack()) {
    companion object {
        private const val ITEM_SESSION = 0
        private const val ITEM_HEADER = 1
        private const val ITEM_LIST_EMPTY = 2
    }

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    fun addHeaderAndSubmitList(list: List<SessionWithData>?) {
        adapterScope.launch {
            val items = when (list?.isEmpty()) {
                null, true -> listOf(DataItem.EmptyListItem)
                else -> {
                    val headerPositions = mutableListOf<Pair<Int, YearMonth?>>()
                    var latestYearMonth: YearMonth? = null
                    val mList: MutableList<DataItem> = list.mapIndexed { i, session ->
                        val dateTime = session.session.createdAt.toInstant()
                            .atZone(TimeZone.getDefault().toZoneId())
                        val yearMonth = YearMonth.from(dateTime)
                        if (latestYearMonth == null || yearMonth.isBefore(latestYearMonth)) {
                            latestYearMonth = yearMonth
                            headerPositions.add(i to yearMonth)
                        }
                        DataItem.SessionItem(session)
                    }.toMutableList()


                    val listSize = mList.size
                    var next: Int
                    headerPositions.forEachIndexed { i, (pos, yearMonth) ->
                        next = try {
                            headerPositions[i + 1].first
                        } catch (e: Exception) {
                            listSize
                        }

                        val header = DataItem.HeaderItem(yearMonth, next - pos)
                        mList.add(pos + i, header)
                    }
                    mList
                }
            }
            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
    }

    class EmptyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        companion object {
            fun from(parent: ViewGroup): EmptyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.session_list_empty, parent, false)
                return EmptyViewHolder(view)
            }
        }
    }

    class HeaderViewHolder(private val binding: SessionListHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun from(parent: ViewGroup): HeaderViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = SessionListHeaderBinding.inflate(layoutInflater, parent, false)
                return HeaderViewHolder(binding)
            }
        }

        fun bind(data: DataItem.HeaderItem, sessionCount: Int) {
            binding.yearMonth = data.yearMonth
            binding.sessionCount = sessionCount
        }
    }

    class ViewHolder(private val binding: StatisticsSessionHistoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding =
                    StatisticsSessionHistoryItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }

        fun bind(item: SessionWithData) {
            binding.sessionItem = item
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_HEADER -> HeaderViewHolder.from(parent)
            ITEM_SESSION -> ViewHolder.from(parent)
            ITEM_LIST_EMPTY -> EmptyViewHolder.from(parent)
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {
                val item = getItem(position) as DataItem.SessionItem
                holder.bind(item.data)
                holder.itemView.setOnClickListener {
                    clickListener.onClick(item.data.session.id)
                }
            }
            is HeaderViewHolder -> {
                val item = getItem(position) as DataItem.HeaderItem
                holder.bind(item, item.sessionCount)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is DataItem.HeaderItem -> ITEM_HEADER
            is DataItem.SessionItem -> ITEM_SESSION
            is DataItem.EmptyListItem -> ITEM_LIST_EMPTY
        }
    }
}

class SessionItemCallBack : DiffUtil.ItemCallback<DataItem>() {
    override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem == newItem
    }
}