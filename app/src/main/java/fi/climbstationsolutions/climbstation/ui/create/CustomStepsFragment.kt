package fi.climbstationsolutions.climbstation.ui.create

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import fi.climbstationsolutions.climbstation.R
import fi.climbstationsolutions.climbstation.adapters.CustomProfileAdapter
import fi.climbstationsolutions.climbstation.adapters.CustomStepsAdapter
import fi.climbstationsolutions.climbstation.databinding.FragmentCustomStepsBinding
import fi.climbstationsolutions.climbstation.ui.climb.climbFinished.ClimbFinishedFragmentArgs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CustomStepsFragment : Fragment(R.layout.fragment_custom_steps), CustomStepFocusListener {
    private lateinit var binding: FragmentCustomStepsBinding
    private val viewModel: CustomStepsViewModel by viewModels()
    private val args: CustomStepsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCustomStepsBinding.inflate(layoutInflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.getProfileWithSteps(args.id)

        binding.customStepsRv.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = CustomStepsAdapter(this@CustomStepsFragment)
        }
        setCustomStepsToRecyclerView()

        binding.floatingActionButton.setOnClickListener(fabListener)
        return binding.root
    }

    private fun setCustomStepsToRecyclerView() {
        val adapter = binding.customStepsRv.adapter as CustomStepsAdapter
        viewModel.customProfileWithSteps.observe(viewLifecycleOwner) {
            lifecycleScope.launch(Dispatchers.Main) {
                if (it.steps.isNotEmpty()) {
                    binding.customStepsRv.visibility = View.VISIBLE
                    binding.emptyRvTitle.visibility = View.GONE
                    Log.d("ITEM", it.toString())
                    adapter.addProfiles(it.steps)
                } else {
                    binding.customStepsRv.visibility = View.GONE
                    binding.emptyRvTitle.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun addStep() {
        viewModel.addStep(args.id)
    }

    private val fabListener = View.OnClickListener {
        when (it) {
            binding.floatingActionButton -> {
                addStep()
            }
        }
    }

    override fun onCustomStepDistanceListener(value: String, id: Long) {
        value.toIntOrNull()?.let { viewModel.updateStepDistance(it, id) }
    }

    override fun onCustomStepAngleListener(value: String, id: Long) {
        value.toIntOrNull()?.let { viewModel.updateStepAngle(it, id) }
    }
}