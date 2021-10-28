package fi.climbstationsolutions.climbstation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fi.climbstationsolutions.climbstation.adapters.StatisticsAdapter

class StatisticsFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_statistics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val testArray = arrayOf(1, 2, 3, 4, 5, 6, 7)

        val recyclerView: RecyclerView = view.findViewById(R.id.sessionList)
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        recyclerView.adapter = StatisticsAdapter(testArray)
    }
}