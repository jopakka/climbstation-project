package fi.climbstationsolutions.climbstation.adapters

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import fi.climbstationsolutions.climbstation.R
import fi.climbstationsolutions.climbstation.ui.statistics.StatisticsFragment
import fi.climbstationsolutions.climbstation.ui.statistics.StatisticsFragmentDirections
import fi.climbstationsolutions.climbstation.ui.statistics.StatisticsListItemData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class StatisticsAdapter(
    private val dataSet: List<StatisticsListItemData>,
    private val context: Context,
    private val fragment: StatisticsFragment
) : RecyclerView.Adapter<StatisticsAdapter.ViewHolder>() {
    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val historyDate: TextView = view.findViewById(R.id.historyDate)
        val historyProfile: TextView = view.findViewById(R.id.historyProfile)
        val historyDuration: TextView = view.findViewById(R.id.historyTimeValue)
        val historyDistance: TextView = view.findViewById(R.id.historyDistanceValue)

    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.statistics_session_history_item, viewGroup, false)
        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        val sessionId = dataSet[position].sessionId
//        viewHolder.historyDate.text = dataSet[position].sessionDate.toString()
        viewHolder.historyProfile.text = dataSet[position].sessionName
//        viewHolder.historyDuration.text = "00:09:14"

        viewHolder.historyDistance.setOnClickListener {
            enterSessionDetailView(sessionId)
        }

        viewHolder.itemView.setOnClickListener { enterSessionDetailView(sessionId) }
        viewHolder.itemView.setOnLongClickListener {
            deleteSession()
            true
        }

//        viewHolder.historyItemDelete.setOnClickListener {
//            deleteSession()
//        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

    private fun enterSessionDetailView(sessionId: Long) {
        val action = StatisticsFragmentDirections.actionStatisticToClimbFinishedFragment(sessionId)
        fragment.findNavController().navigate(action)
    }

    private fun deleteSession() {
        // Creates a dialog popup interface to confirm if user wants to delete session
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setCancelable(true)
        builder.setTitle("Delete session")
        builder.setMessage("Are you sure you want delete this session?")

        // When user confirms popup interface
        builder.setPositiveButton(
            "Yes"
        ) { _, _ ->
            Log.d("confirm", "confirmed")
            GlobalScope.launch {
                // do deletion here
            }
        }

        // When user cancels popup interface
        builder.setNegativeButton(
            "Cancel"
        ) { _, _ -> Log.d("cancel", "canceled dialog interface") }
        // Puts the popup to the screen
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

//    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
//        Log.d("StatisticsAdapter","onInterceptTouchEvent")
//        return false
//    }
//
//    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
//        Log.d("StatisticsAdapter","onTouchEvent")
//    }
//
//    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
//        Log.d("StatisticsAdapter","onRequestDisallowInterceptTouchEvent")
//    }


}