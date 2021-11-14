package fi.climbstationsolutions.climbstation.adapters

import android.content.Context
import android.graphics.Paint
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import fi.climbstationsolutions.climbstation.R

class HorizontalNumberPickerAdapter(
    private val items: List<Int>,
    private val context: Context?,
    private val numberClickCallback: ((Int) -> Unit)?
) : RecyclerView.Adapter<HorizontalNumberPickerAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val numberLabel: TextView = view.findViewById(R.id.number_list_text)
    }

    private var textSize = 24F
    private var myPosition = 0

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.numbers_list, parent, false)
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position in 1 until (itemCount - 5)) {
            val number = items[position].toString()
            holder.numberLabel.text = number
            if (position == myPosition) {
                holder.numberLabel.textSize = textSize
                holder.numberLabel.setTextColor(ContextCompat.getColor(context!!, R.color.white))
                holder.numberLabel.paintFlags = Paint.UNDERLINE_TEXT_FLAG
            } else {
                holder.numberLabel.textSize = 20F
                holder.numberLabel.setTextColor(ContextCompat.getColor(context!!, R.color.horizontal_picker_nonfocus_color))
                holder.numberLabel.paintFlags = 0
            }
            holder.numberLabel.contentDescription = number
            holder.itemView.setOnClickListener {
                numberClickCallback?.invoke(items[position])
            }
        }
        else {
            holder.numberLabel.text = ""
            holder.numberLabel.contentDescription = ""
            holder.itemView.setOnClickListener {}
        }
    }

    fun setTextSize(value: Float, position: Int) {
        this.textSize = value
        myPosition = position
        notifyDataSetChanged()
    }
}
