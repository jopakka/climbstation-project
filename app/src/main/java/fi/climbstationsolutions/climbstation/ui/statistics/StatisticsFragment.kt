package fi.climbstationsolutions.climbstation.ui.statistics

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fi.climbstationsolutions.climbstation.R
import fi.climbstationsolutions.climbstation.adapters.StatisticsAdapter
import fi.climbstationsolutions.climbstation.database.AppDatabase
import fi.climbstationsolutions.climbstation.database.SessionWithData
import fi.climbstationsolutions.climbstation.database.SessionWithDataDao
import kotlinx.coroutines.*
import java.util.*

class StatisticsFragment : Fragment() {
    private val parentJob = Job()
    private val ioScope = CoroutineScope(Dispatchers.IO + parentJob)
    private val mainScope = CoroutineScope(Dispatchers.Main + parentJob)

    private lateinit var dao: SessionWithDataDao

    private lateinit var sessionsList: List<SessionWithData>
    private lateinit var adapterItemList: MutableList<StatisticsListItemData>

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

        val myContext = this.context


        // Gets all session id's from database

        mainScope.launch {
            Log.d("statistics_fragment","getting sessionsList")
            sessionsList = getAllSessions()
            adapterItemList = mutableListOf()
            Log.d("statistics_fragment","after ioScope, sessionsList: ${sessionsList}")
            val testArray = arrayOf(1, 2, 3, 4, 5, 6, 7)
            for (item in sessionsList.indices) {
                val sessionId: Long = sessionsList[item].session.id
                val sessionName: String = sessionsList[item].session.name
                val sessionDate: Date = sessionsList[item].session.createdAt
                val adapterItem = StatisticsListItemData(sessionId, sessionName, sessionDate)
                adapterItemList.add(adapterItem)
            }

            Log.d("statistics_fragment","adapterItemList: ${adapterItemList}")

            val recyclerView: RecyclerView = view.findViewById(R.id.sessionList)
            recyclerView.layoutManager = LinearLayoutManager(myContext)
            recyclerView.adapter = context?.let { StatisticsAdapter(adapterItemList, it, requireActivity()) }
        }
    }

    private suspend fun getAllSessions() = withContext(Dispatchers.IO) {
        dao = AppDatabase.get(requireContext()).sessionDao()
        val getSessionsList = dao.getAllSessionsWithData()
        Log.d("statistics_fragment", "${getSessionsList}")
        return@withContext getSessionsList
    }
}