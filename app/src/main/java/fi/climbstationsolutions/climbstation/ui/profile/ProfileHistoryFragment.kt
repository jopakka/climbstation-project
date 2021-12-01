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

        viewModel.allSessions.observe(viewLifecycleOwner) {
            it?.let {
                adapter.addHeaderAndSubmitList(it)
            }
        }
    }
}