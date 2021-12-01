package fi.climbstationsolutions.climbstation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import fi.climbstationsolutions.climbstation.database.ClimbProfileWithSteps
import fi.climbstationsolutions.climbstation.databinding.CustomProfileItemBinding
import fi.climbstationsolutions.climbstation.ui.climb.CellClickListener
import fi.climbstationsolutions.climbstation.ui.climb.ClimbFragmentDirections
import fi.climbstationsolutions.climbstation.ui.create.CustomProfileClickListener

class CustomProfileAdapter(private val customProfileClickListener: CustomProfileClickListener) : RecyclerView.Adapter<CustomProfileAdapter.ViewHolder>() {

    private val customProfileList: MutableList<ClimbProfileWithSteps> = mutableListOf()

    fun addProfiles(list: List<ClimbProfileWithSteps>) {
        customProfileList.clear()
        customProfileList.let {
            customProfileList.addAll(list)
            notifyItemRangeInserted(it.size, list.size)
        }
    }

    class ViewHolder(private val binding: CustomProfileItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = CustomProfileItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }

        fun bind(item: ClimbProfileWithSteps) {
            binding.customProfileItem = item
            binding.executePendingBindings()
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
    }

    override fun getItemCount(): Int = customProfileList.size
}
