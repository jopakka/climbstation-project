package fi.climbstationsolutions.climbstation.ui.create.customStep

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fi.climbstationsolutions.climbstation.R
import fi.climbstationsolutions.climbstation.adapters.CustomStepsAdapter
import fi.climbstationsolutions.climbstation.databinding.FragmentCustomStepsBinding
import fi.climbstationsolutions.climbstation.utils.SwipeToDelete
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
            adapter = CustomStepsAdapter(this@CustomStepsFragment, context)
        }

        setSwipeHandler()
        setCustomStepsToRecyclerView()

        binding.floatingActionButton.setOnClickListener(fabListener)
        return binding.root
    }

    private fun setSwipeHandler() {
        val swipeHandler = object : SwipeToDelete(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = binding.customStepsRv.adapter as CustomStepsAdapter
                when (direction) {
                    ItemTouchHelper.LEFT -> {
                        val id = adapter.deleteStep(viewHolder.bindingAdapterPosition)
                        viewModel.deleteStep(id)
                    }
                    ItemTouchHelper.RIGHT -> {
                        val duplicateValues =
                            adapter.duplicateStep(viewHolder.bindingAdapterPosition)
                        viewModel.duplicateStep(
                            args.id,
                            duplicateValues.distance,
                            duplicateValues.angle
                        )
                    }
                }
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(binding.customStepsRv)
    }

    private fun setCustomStepsToRecyclerView() {
        val adapter = binding.customStepsRv.adapter as CustomStepsAdapter
        viewModel.customProfileWithSteps.observe(viewLifecycleOwner) {
            lifecycleScope.launch(Dispatchers.Main) {
                if (it.steps.isNotEmpty()) {
                    binding.customStepsRv.visibility = View.VISIBLE
                    binding.emptyRvTitle.visibility = View.GONE
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

    override fun onCustomStepDistanceListener(value: Int, id: Long) {
        viewModel.updateStepDistance(value, id)
    }

    override fun onCustomStepAngleListener(value: Int, id: Long) {
        viewModel.updateStepAngle(value, id)
    }
}