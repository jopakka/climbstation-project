package fi.climbstationsolutions.climbstation.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import fi.climbstationsolutions.climbstation.databinding.FragmentHistoryBinding

class ClimbHistoryFragment : Fragment() {
    private lateinit var binding: FragmentHistoryBinding
    private val viewModel: ClimbHistoryViewModel by viewModels()
    private val args: ClimbHistoryFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoryBinding.inflate(layoutInflater)

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.getSession(args.sessionId)

        return binding.root
    }
}