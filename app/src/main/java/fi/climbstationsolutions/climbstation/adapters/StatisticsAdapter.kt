package fi.climbstationsolutions.climbstation.adapters

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.replace
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import fi.climbstationsolutions.climbstation.R
import fi.climbstationsolutions.climbstation.ui.climb.ClimbFinishedFragment
import fi.climbstationsolutions.climbstation.ui.statistics.StatisticsFragment
import fi.climbstationsolutions.climbstation.ui.statistics.StatisticsListItemData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class StatisticsAdapter(
    private val dataSet: List<StatisticsListItemData>,
    private val context: Context,
    private val activity: FragmentActivity
) : RecyclerView.Adapter<StatisticsAdapter.ViewHolder>() {
    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val historyDate: TextView = view.findViewById(R.id.historyDate)
        val historyName: TextView = view.findViewById(R.id.historyName)
        val historyDuration: TextView = view.findViewById(R.id.historyDuration)
        val historyItem: TextView = view.findViewById(R.id.historyItem)

        val historyItemDelete: MaterialButton = view.findViewById(R.id.historyItemDelete)
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
        viewHolder.historyDate.text = dataSet[position].sessionDate.toString()
        viewHolder.historyName.text = dataSet[position].sessionName
        viewHolder.historyDuration.text = "00:09:14"

        viewHolder.historyItem.setOnClickListener {
            enterSessionDetailView(sessionId)
        }

        viewHolder.historyItemDelete.setOnClickListener {
            deleteSession()
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

    private fun enterSessionDetailView(sessionId: Long) {
        val sfm = activity.supportFragmentManager
        Log.d("SA","sessionId: $sessionId")
        val data = Bundle()
        data.putLong("sessionId", sessionId)
        val fragment = ClimbFinishedFragment()
        fragment.arguments = data
        Log.d("SA","fragment args: ${fragment.arguments}")

        val transaction = sfm.beginTransaction()
        transaction.replace(R.id.fragmentContainer, fragment)
        transaction.commit()
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
}