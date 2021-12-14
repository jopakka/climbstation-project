package fi.climbstationsolutions.climbstation.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import fi.climbstationsolutions.climbstation.R
import fi.climbstationsolutions.climbstation.database.ClimbStep
import fi.climbstationsolutions.climbstation.databinding.CustomStepItemBinding
import fi.climbstationsolutions.climbstation.ui.create.CustomStepFocusListener
import androidx.core.content.ContextCompat.getSystemService

import android.graphics.Rect

import android.widget.EditText

import android.view.MotionEvent
import android.widget.TextView
import androidx.core.content.ContextCompat.getSystemService

data class DuplicateValues(
    val distance: Int,
    val angle: Int
)

class CustomStepsAdapter(private val customStepFocusListener: CustomStepFocusListener, private val context: Context) :
    RecyclerView.Adapter<CustomStepsAdapter.ViewHolder>() {

    private val customStepsList: MutableList<ClimbStep> = mutableListOf()

    fun addProfiles(list: List<ClimbStep>) {
        customStepsList.clear()
        customStepsList.let {
            customStepsList.addAll(list)
            notifyItemRangeInserted(it.size, list.size)
        }
    }

    inner class ViewHolder(private val binding: CustomStepItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ClimbStep, position: Int) {
            binding.customStepItem = item

            binding.stepRvTitle.text = this.itemView.context.getString(R.string.Step, position + 1)

            binding.lengthEditText.hint =
                this.itemView.context.getString(R.string.distanceShort, item.distance.toFloat())
            binding.angleEditText.hint =
                this.itemView.context.getString(R.string.angleShortInt, item.angle)

            binding.lengthEditText.doOnTextChanged { text, _, _, _ ->
                text.toString().toIntOrNull().let {
                    if (it == null || it >= 0) {
                        binding.lengthEditTextLayout.error = null
                    } else {
                        binding.lengthEditTextLayout.error = "0+ meters"
                    }
                }
            }
            binding.angleEditText.doOnTextChanged { text, _, _, _ ->
                text.toString().toIntOrNull().let {
                    if (it in (-45..15) || it == null ) {
                        binding.angleEditTextLayout.error = null
                    } else {
                        binding.angleEditTextLayout.error =
                            "-45° - 15°"
                    }
                }
            }

            binding.lengthEditText.onFocusChangeListener = View.OnFocusChangeListener { _, focus ->
                if (!focus) {
                    binding.lengthEditText.text.toString().toIntOrNull()?.let {
                        if (it != item.distance) {
                            if (it >= 0) {
                                Log.d("UPDATE", "WORKS")
                                customStepFocusListener.onCustomStepDistanceListener(it, item.id)
                            }
                        }
                    }
                }
            }

            binding.angleEditText.onFocusChangeListener = View.OnFocusChangeListener { _, focus ->
                if (!focus) {
                    binding.angleEditText.text.toString().toIntOrNull()?.let {
                        if (it in (-45..10)) {
                            Log.d("UPDATE", "WORKS")
                            customStepFocusListener.onCustomStepAngleListener(it, item.id)
                        }
                    }
                }
            }

            binding.lengthEditText.setOnEditorActionListener(imeDoneListener)
            binding.angleEditText.setOnEditorActionListener(imeDoneListener)

            binding.executePendingBindings()
        }

        private val imeDoneListener = TextView.OnEditorActionListener { textView, i, _ ->
            when(textView) {
                binding.angleEditText -> {
                    if (i == EditorInfo.IME_ACTION_DONE) {
                        hideKeyboard(textView)
                        binding.angleEditText.clearFocus()
                        return@OnEditorActionListener true
                    }
                    false
                }
                binding.lengthEditText -> {
                    if (i == EditorInfo.IME_ACTION_DONE) {
                        hideKeyboard(textView)
                        binding.lengthEditText.clearFocus()
                        return@OnEditorActionListener true
                    }
                    false
                }
                else -> {
                    return@OnEditorActionListener true
                }
            }
        }
        private fun hideKeyboard(view: View) {
            getSystemService(context, InputMethodManager::class.java)
                ?.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun deleteStep(pos: Int): Long {
        val item = customStepsList[pos]
        customStepsList.removeAt(pos)
        notifyItemRemoved(pos)
        notifyItemRangeRemoved(pos, customStepsList.size)
        return item.id
    }

    fun duplicateStep(pos: Int): DuplicateValues {
        val item = customStepsList[pos]
        return DuplicateValues(item.distance, item.angle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            CustomStepItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = customStepsList[position]
        holder.bind(item, position)
    }

    override fun getItemCount(): Int = customStepsList.size
}
