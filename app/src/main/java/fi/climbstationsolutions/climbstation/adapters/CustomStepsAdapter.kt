package fi.climbstationsolutions.climbstation.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import fi.climbstationsolutions.climbstation.R
import fi.climbstationsolutions.climbstation.database.ClimbStep
import fi.climbstationsolutions.climbstation.databinding.CustomStepItemBinding
import fi.climbstationsolutions.climbstation.ui.create.CustomStepFocusListener

class CustomStepsAdapter(private val customStepFocusListener: CustomStepFocusListener) :
    RecyclerView.Adapter<CustomStepsAdapter.ViewHolder>() {

    private val customStepsList: MutableList<ClimbStep> = mutableListOf()

    fun addProfiles(list: List<ClimbStep>) {
        customStepsList.clear()
        customStepsList.let {
            customStepsList.addAll(list)
            notifyItemRangeInserted(it.size, list.size)
        }
    }

    class ViewHolder(private val binding: CustomStepItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = CustomStepItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }

        fun bind(item: ClimbStep, position: Int) {
            binding.customStepItem = item
            binding.stepIndex = position + 1
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = customStepsList[position]
        holder.bind(item, position)
        val lengthEditText = holder.itemView.findViewById<TextInputLayout>(R.id.lengthEditTextLayout)
        val angleEditText = holder.itemView.findViewById<TextInputLayout>(R.id.angleEditTextLayout)

        lengthEditText.editText?.onFocusChangeListener = View.OnFocusChangeListener { _, focus ->
            if (!focus) {
                if (lengthEditText.editText?.text.toString().toIntOrNull() != item.distance) {
                    customStepFocusListener.onCustomStepDistanceListener(
                        lengthEditText.editText?.text.toString(),
                        item.id
                    )
                }
            }
        }


        angleEditText.editText?.onFocusChangeListener = View.OnFocusChangeListener { _, focus ->
            Log.d("onFocus", "WORKS")
            if (!focus) {
                Log.d("offFocus", "WORKS")
                if (angleEditText.editText?.text.toString().toIntOrNull() != item.angle) {
                    Log.d("editText", angleEditText.editText?.text.toString())
                    Log.d("item", item.angle.toString())
                    customStepFocusListener.onCustomStepAngleListener(
                        angleEditText.editText?.text.toString(),
                        item.id
                    )
                }
            }
        }
    }

    override fun getItemCount(): Int = customStepsList.size
}
