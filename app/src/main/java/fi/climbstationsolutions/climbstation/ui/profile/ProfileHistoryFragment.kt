package fi.climbstationsolutions.climbstation.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import fi.climbstationsolutions.climbstation.adapters.StatisticsAdapter
import fi.climbstationsolutions.climbstation.databinding.FragmentProfileHistoryBinding
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

class ProfileHistoryFragment : Fragment(), SessionClickListener {
    private lateinit var binding: FragmentProfileHistoryBinding
    private val viewModel: ProfileViewModel by viewModels(
        ownerProducer = { requireParentFragment() }
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileHistoryBinding.inflate(layoutInflater)
        binding.lifecycleOwner = viewLifecycleOwner

        initUI()

        return binding.root
    }

    override fun onClick(sessionId: Long) {
        val direction = ProfileFragmentDirections.actionProfileToClimbHistory(sessionId)
        findNavController().navigate(direction)
    }

    private fun initUI() {
        val adapter = StatisticsAdapter(this@ProfileHistoryFragment)
        val layoutManager = LinearLayoutManager(context)
        binding.sessionRv.apply {
            this.layoutManager = layoutManager
            this.adapter = adapter
        }

        val smoothScroller = object : LinearSmoothScroller(context) {
            override fun getVerticalSnapPreference(): Int {
                return SNAP_TO_START
            }
        }

        viewModel.filteredSessions.observe(viewLifecycleOwner) {
            it?.let {
                adapter.addHeaderAndSubmitList(it)
                smoothScroller.targetPosition = 0
                layoutManager.startSmoothScroll(smoothScroller)
            }
        }

        binding.apply {
            fabFilter.setOnClickListener(filterAction)
            chipFilter.setOnCloseIconClickListener { clearFilter() }
        }
    }

    private val filterAction = View.OnClickListener {
        MonthYearDialog().apply {
            setPositiveListener { m, y ->
                val beginningOfMonth = LocalDateTime.of(y, m, 1, 0, 0)
                val monthSelected = Date.from(beginningOfMonth.toInstant(ZoneOffset.UTC))
                val nextMonth = Date.from(beginningOfMonth.plusMonths(1).toInstant(ZoneOffset.UTC))
                viewModel.filterList(monthSelected, nextMonth)
                binding.apply {
                    filterMonth = beginningOfMonth.month.name
                    filterYear = y
                }
            }
        }.show(childFragmentManager, MonthYearDialog.TAG)
    }

    private fun clearFilter() {
        viewModel.showAllSessions()
        binding.apply {
            filterMonth = null
            filterYear = null
        }
    }
}