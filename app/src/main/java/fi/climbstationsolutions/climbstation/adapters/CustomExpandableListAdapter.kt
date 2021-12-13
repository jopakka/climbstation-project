package fi.climbstationsolutions.climbstation.adapters

import android.content.Context
import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import fi.climbstationsolutions.climbstation.R
import fi.climbstationsolutions.climbstation.ui.viewmodels.MainActivityViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class CustomExpandableListAdapter(
    private var context: Context,
    private var listTitles: List<String>,
    private var listItems: LinkedHashMap<String, List<String>>,
    private var viewModel: MainActivityViewModel
) : BaseExpandableListAdapter() {

    override fun getGroupCount(): Int {
        return this.listTitles.size
    }

    override fun getChildrenCount(p0: Int): Int {
        return this.listItems[this.listTitles[p0]]!!.size
    }

    override fun getGroup(p0: Int): Any {
        return this.listTitles[p0]
    }

    override fun getChild(p0: Int, p1: Int): Any {
        return this.listItems[this.listTitles[p0]]!![p1]
    }

    override fun getGroupId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getChildId(p0: Int, p1: Int): Long {
        return p1.toLong()
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun getGroupView(p0: Int, p1: Boolean, p2: View?, p3: ViewGroup?): View {
        val title: String = getGroup(p0) as String
        var convertView = p2
        if (p2 == null) {
            Log.d("CELA", "getGroupView() p2 is null")
            val layoutInflater =
                this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = layoutInflater.inflate(R.layout.drawer_list_group, null)
        }
        val txtTitle: TextView = convertView!!.findViewById(R.id.listTitle)
        val txtArrow: ImageView = convertView!!.findViewById(R.id.ivGroupIndicator)
        val txtSelectedColor: ImageView = convertView!!.findViewById(R.id.ivGroupColorIndicator)
//        txtTitle.setTypeface(null, Typeface.BOLD)
        txtTitle.text = title

        // icon
        when (title) {
            "Settings" -> {
                txtTitle.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_settings_24, 0, 0, 0)
            }
            "Info" -> {
                txtTitle.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_info_24, 0, 0, 0)
            }
            "Connect" -> {
                txtTitle.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_wifi_24, 0, 0, 0)
            }
        }

        txtTitle.compoundDrawablePadding = 20

        // sets arrow icon up or down depending on if it is selected
        if (title != "Connect") {
            txtArrow.isSelected = p1
            txtSelectedColor.isSelected = p1
        } else {
            txtArrow.background = null
        }

        return convertView
    }

    override fun getChildView(p0: Int, p1: Int, p2: Boolean, p3: View?, p4: ViewGroup?): View {
        val title: String = getChild(p0, p1) as String
        var convertView = p3
        if (convertView == null) {
            Log.d("CELA", "getGroupView() p2 is null")
            val layoutInflater =
                this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = layoutInflater.inflate(R.layout.drawer_list_child, null)
        }
        val txtChild: TextView = convertView!!.findViewById(R.id.expandableListItem)

        // Remove drawables to avoid duplicates
        txtChild.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)

        txtChild.text = title
//        txtChild.setTextColor("#000000")
        when (title) {
            "Bodyweight" -> {
                txtChild.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_scale_24, 0, 0, 0)
                txtChild.compoundDrawablePadding = 20
                GlobalScope.launch(Dispatchers.Main) {
                    txtChild.text =
                        context.getString(R.string.child_item_title, title, viewModel.getWeight())
                }
            }
            "Machine speed" -> {
                txtChild.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_speed_24, 0, 0, 0)
                txtChild.compoundDrawablePadding = 20
            }
            "Text to speech" -> {
                txtChild.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_tts_24, 0, R.drawable.ic_off_24, 0)
                txtChild.compoundDrawablePadding = 20
            }
            "How to connect to ClimbStation machine" -> {
                txtChild.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_wifi_24, 0, 0, 0)
                txtChild.compoundDrawablePadding = 20
            }
            "How to climb" -> {
                txtChild.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_stairs_24, 0, 0, 0)
                txtChild.compoundDrawablePadding = 20
            }
            "How to create custom climbing profiles" -> {
                txtChild.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_edit_24, 0, 0, 0)
                txtChild.compoundDrawablePadding = 20
            }
        }
        return convertView
    }

    override fun isChildSelectable(p0: Int, p1: Int): Boolean {
        return true
    }
}