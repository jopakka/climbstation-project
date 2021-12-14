package fi.climbstationsolutions.climbstation.ui.share

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fi.climbstationsolutions.climbstation.R
import fi.climbstationsolutions.climbstation.database.ClimbStep
import fi.climbstationsolutions.climbstation.databinding.CustomStepViewItemBinding

class CustomStepsViewAdapter : RecyclerView.Adapter<CustomStepsViewAdapter.ViewHolder>() {

    private val customStepsList: MutableList<ClimbStep> = mutableListOf()

    fun addProfiles(list: List<ClimbStep>) {
        customStepsList.clear()
        customStepsList.let {
            customStepsList.addAll(list)
            notifyItemRangeInserted(it.size, list.size)
        }
    }

    inner class ViewHolder(private val binding: CustomStepViewItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ClimbStep, position: Int) {
            val context = binding.root.context
            binding.apply {
                stepRvTitle.text = context.getString(R.string.Step, position + 1)
                txtLength.text = context.getString(R.string.distanceShort, item.distance.toFloat())
                txtAngle.text = context.getString(R.string.angleShortInt, item.angle)
                executePendingBindings()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            CustomStepViewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = customStepsList[position]
        holder.bind(item, position)
    }

    override fun getItemCount(): Int = customStepsList.size
}
