package fi.climbstationsolutions.climbstation.adapters

import android.view.*
import androidx.recyclerview.widget.RecyclerView
import fi.climbstationsolutions.climbstation.R
import fi.climbstationsolutions.climbstation.database.ClimbProfileWithSteps
import fi.climbstationsolutions.climbstation.databinding.CustomProfileItemBinding
import fi.climbstationsolutions.climbstation.ui.create.CustomProfileClickListener

class CustomProfileAdapter(private val customProfileClickListener: CustomProfileClickListener) :
    RecyclerView.Adapter<CustomProfileAdapter.ViewHolder>() {

    private val customProfileList: MutableList<ClimbProfileWithSteps> = mutableListOf()
    var selectedProfileId: Long = -1
        private set

    fun addProfiles(list: List<ClimbProfileWithSteps>) {
        customProfileList.clear()
        customProfileList.let {
            customProfileList.addAll(list)
            notifyItemRangeInserted(it.size, list.size)
        }
    }

    fun deleteStep(pos: Int): Long {
        val item = customProfileList[pos]
        customProfileList.removeAt(pos)
        notifyItemRemoved(pos)
        return item.profile.id
    }

    inner class ViewHolder(private val binding: CustomProfileItemBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnCreateContextMenuListener {

        fun bind(item: ClimbProfileWithSteps) {
            binding.root.setOnCreateContextMenuListener(this)
            binding.customProfileItem = item

            binding.customProfileEditBtn.setOnClickListener {
                customProfileClickListener.onCustomProfileClickListener(item.profile.id)
            }

            binding.executePendingBindings()
        }

        override fun onCreateContextMenu(
            menu: ContextMenu,
            v: View,
            menuInfo: ContextMenu.ContextMenuInfo?
        ) {
            menu.add(Menu.NONE, R.id.menuShare, Menu.NONE, v.context.getString(R.string.share))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            CustomProfileItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = customProfileList[position]
        holder.bind(item)
        holder.itemView.setOnLongClickListener {
            selectedProfileId = item.profile.id
            false
        }
    }

    override fun getItemCount(): Int = customProfileList.size
}
