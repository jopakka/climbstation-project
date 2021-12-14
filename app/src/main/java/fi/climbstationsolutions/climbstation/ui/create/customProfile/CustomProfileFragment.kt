package fi.climbstationsolutions.climbstation.ui.create.customProfile

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.View
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fi.climbstationsolutions.climbstation.R
import fi.climbstationsolutions.climbstation.databinding.FragmentCustomProfileBinding
import fi.climbstationsolutions.climbstation.utils.ProfileSharer
import fi.climbstationsolutions.climbstation.utils.SwipeToDelete

class CustomProfileFragment : Fragment(), CustomProfileClickListener {
    private lateinit var binding: FragmentCustomProfileBinding
    private val viewModel: CustomProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCustomProfileBinding.inflate(layoutInflater)

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.customProfileRv.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = CustomProfileAdapter(this@CustomProfileFragment)
        }

        setSwipeHandler()
        setCustomProfilesToRecyclerView()

        binding.floatingActionButton.setOnClickListener(fabListener)

        registerForContextMenu(binding.customProfileRv)
        return binding.root
    }

    private fun setSwipeHandler() {
        val swipeHandler = object : SwipeToDelete(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = binding.customProfileRv.adapter as CustomProfileAdapter
                when (direction) {
                    ItemTouchHelper.LEFT -> {
                        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
                        builder.apply {
                            setTitle("Delete custom profile")
                            setMessage("Are you sure you want to delete the profile?")
                            setNegativeButton("Cancel") { _, _ ->
                                adapter.notifyItemChanged(viewHolder.bindingAdapterPosition)
                            }
                            setPositiveButton("Delete") { _, _ ->
                                val id = adapter.deleteStep(viewHolder.bindingAdapterPosition)
                                viewModel.deleteProfile(id)
                            }
                            show()
                        }
                    }
                }
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(binding.customProfileRv)
    }

    private fun setCustomProfilesToRecyclerView() {
        val adapter = binding.customProfileRv.adapter as CustomProfileAdapter
        viewModel.customProfiles.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                binding.customProfileRv.visibility = View.VISIBLE
                binding.emptyRvTitle.visibility = View.GONE
                adapter.addProfiles(it)
            } else {
                binding.customProfileRv.visibility = View.GONE
                binding.emptyRvTitle.visibility = View.VISIBLE
            }
        }
    }

    private fun addNamePopUp() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        val promptsView = layoutInflater.inflate(R.layout.custom_profile_prompt, null)
        builder.setView(promptsView)
        val userInput = promptsView.findViewById<EditText>(R.id.custom_profile_dialog_editText)

        builder.apply {
            setPositiveButton("Add") { _, _ ->
                if (userInput.text.toString() != "")
                    viewModel.addCustomProfile(userInput.text.toString())
            }
            setNegativeButton("Cancel") { _, _ ->
                Log.d("Cancel", "WORKS")
            }
            show()
        }
    }

    private val fabListener = View.OnClickListener {
        when (it) {
            binding.floatingActionButton -> {
                addNamePopUp()
            }
        }
    }

    override fun onCustomProfileClickListener(id: Long) {
        val action = CustomProfileFragmentDirections.actionCreateToCustomStepsFragment(id)
        findNavController().navigate(action)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menuShare -> {
                val sharer = ProfileSharer(requireActivity())
                val adapter = binding.customProfileRv.adapter as CustomProfileAdapter
                viewModel.customProfiles.observe(viewLifecycleOwner) {
                    val profile = it.firstOrNull { p -> p.profile.id == adapter.selectedProfileId }
                    if (profile != null) sharer.shareProfile(profile)
                }
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }
}