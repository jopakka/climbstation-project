package fi.climbstationsolutions.climbstation.adapters

import android.content.Context
import android.view.*
import android.widget.TextView
import androidx.core.view.marginBottom
import androidx.recyclerview.widget.RecyclerView
import fi.climbstationsolutions.climbstation.R
import fi.climbstationsolutions.climbstation.database.ClimbProfileWithSteps
import fi.climbstationsolutions.climbstation.databinding.CustomProfileItemBinding
import fi.climbstationsolutions.climbstation.databinding.PickerItemLayoutBinding

class TestiAdapter(private val data: List<Int>) : RecyclerView.Adapter<TestiAdapter.TestiViewHolder>() {


    inner class TestiViewHolder(private val binding: PickerItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(pickerNumber: Int) {
            binding.pickerItem.text =  pickerNumber.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestiViewHolder {
        val binding =
            PickerItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TestiViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TestiViewHolder, position: Int) {
        val pickerNumber = data[position]
        holder.bind(pickerNumber)
    }

    override fun getItemCount(): Int = data.size
}