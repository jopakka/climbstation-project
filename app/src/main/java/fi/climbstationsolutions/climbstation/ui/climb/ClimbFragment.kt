package fi.climbstationsolutions.climbstation.ui.climb

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import fi.climbstationsolutions.climbstation.R
import fi.climbstationsolutions.climbstation.databinding.FragmentClimbBinding

class ClimbFragment : Fragment(R.layout.fragment_climb), CellClicklistener {
    private lateinit var binding: FragmentClimbBinding

    private val viewModel: ClimbViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentClimbBinding.inflate(layoutInflater)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        binding.difficultyRv.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = DifficultyRecyclerviewAdapter(this@ClimbFragment)
        }

        return binding.root
    }

    override fun onCellClickListener(profile: DifficultyProfile) {
        viewModel.postValue(profile)
    }
}