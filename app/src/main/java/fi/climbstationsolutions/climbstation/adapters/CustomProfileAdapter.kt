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

    fun deleteStep(pos: Int): Long {
        val item = customProfileList[pos]
        customProfileList.removeAt(pos)
        notifyItemRemoved(pos)
        return item.profile.id
    }

    inner class ViewHolder(private val binding: CustomProfileItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ClimbProfileWithSteps) {
            binding.customProfileItem = item

            binding.customProfileEditBtn.setOnClickListener {
                customProfileClickListener.onCustomProfileClickListener(item.profile.id)
            }

            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CustomProfileItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = customProfileList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = customProfileList.size
}
