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

class HorizontalStringPickerAdapter(
    private val items: List<String>,
    private val context: Context?,
    private val stringClickCallback: ((String) -> Unit)?
) : RecyclerView.Adapter<HorizontalStringPickerAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val stringLabel: TextView = view.findViewById(R.id.string_list_text)
    }

    private var textSize = 16F
    private var myPosition = 0

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.strings_list, parent, false)
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position in 1 until (itemCount - 1)) {
            val number = items[position]
            holder.stringLabel.text = number
            if (position == myPosition) {
                holder.stringLabel.textSize = textSize
                holder.stringLabel.setTextColor(ContextCompat.getColor(context!!, R.color.white))
                holder.stringLabel.paintFlags = Paint.UNDERLINE_TEXT_FLAG
            } else {
                holder.stringLabel.textSize = 16F
                holder.stringLabel.setTextColor(ContextCompat.getColor(context!!, R.color.horizontal_picker_nonfocus_color))
                holder.stringLabel.paintFlags = 0
            }
            holder.stringLabel.contentDescription = number
            holder.itemView.setOnClickListener {
                stringClickCallback?.invoke(items[position])
            }
        }
        else {
            holder.stringLabel.text = ""
            holder.stringLabel.contentDescription = ""
            holder.itemView.setOnClickListener {}
        }
    }

    fun setTextSize(value: Float, position: Int) {
        this.textSize = value
        myPosition = position
        notifyDataSetChanged()
    }
}
