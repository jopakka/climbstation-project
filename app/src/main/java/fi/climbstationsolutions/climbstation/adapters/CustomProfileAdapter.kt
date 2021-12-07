package fi.climbstationsolutions.climbstation.adapters

import android.app.Activity
import android.view.*
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import fi.climbstationsolutions.climbstation.R
import fi.climbstationsolutions.climbstation.database.ClimbProfileWithSteps
import fi.climbstationsolutions.climbstation.databinding.CustomProfileItemBinding
import fi.climbstationsolutions.climbstation.ui.climb.CellClickListener
import fi.climbstationsolutions.climbstation.ui.climb.ClimbFragmentDirections
import fi.climbstationsolutions.climbstation.ui.create.CustomProfileClickListener

class CustomProfileAdapter(private val customProfileClickListener: CustomProfileClickListener) : RecyclerView.Adapter<CustomProfileAdapter.ViewHolder>() {

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

    class ViewHolder(private val binding: CustomProfileItemBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnCreateContextMenuListener {

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = CustomProfileItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }

        fun bind(item: ClimbProfileWithSteps) {
            binding.root.setOnCreateContextMenuListener(this)
            binding.customProfileItem = item
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
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = customProfileList[position]
        holder.bind(item)
        holder.itemView.setOnClickListener {
            customProfileClickListener.onCustomProfileClickListener(item.profile.id)
        }
        holder.itemView.setOnLongClickListener {
            selectedProfileId = item.profile.id
            false
        }
    }

    override fun getItemCount(): Int = customProfileList.size
}
