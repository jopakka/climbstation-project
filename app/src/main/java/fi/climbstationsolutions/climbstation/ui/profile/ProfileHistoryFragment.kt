package fi.climbstationsolutions.climbstation.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
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

        initUI()

        return binding.root
    }

    override fun onClick(sessionId: Long) {
        val direction = ProfileFragmentDirections.actionProfileToClimbHistory(sessionId)
        findNavController().navigate(direction)
    }

    private fun initUI() {
        val adapter = StatisticsAdapter(this@ProfileHistoryFragment)
        binding.sessionRv.apply {
            layoutManager = LinearLayoutManager(context)
            this.adapter = adapter
        }

        viewModel.filteredSessions.observe(viewLifecycleOwner) {
            it?.let {
                adapter.addHeaderAndSubmitList(it)
            }
        }

        binding.fabFilter.setOnClickListener(filterAction)
    }

    private val filterAction = View.OnClickListener {
        MonthYearDialog().apply {
            setPositiveListener { m, y ->
                val beginningOfMonth = LocalDateTime.of(y, m, 1, 0, 0)
                val monthSelected = Date.from(beginningOfMonth.toInstant(ZoneOffset.UTC))
                val nextMonth = Date.from(beginningOfMonth.plusMonths(1).toInstant(ZoneOffset.UTC))
                viewModel.filterList(monthSelected, nextMonth)
            }
            setNegativeListener { viewModel.showAllSessions() }
        }.show(childFragmentManager, MonthYearDialog.TAG)
    }
}