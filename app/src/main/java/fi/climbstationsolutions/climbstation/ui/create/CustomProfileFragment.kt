package fi.climbstationsolutions.climbstation.ui.create

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import fi.climbstationsolutions.climbstation.R
import fi.climbstationsolutions.climbstation.adapters.CustomProfileAdapter
import fi.climbstationsolutions.climbstation.databinding.FragmentCustomProfileBinding

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

        setCustomProfilesToRecyclerView()
        binding.floatingActionButton.setOnClickListener(fabListener)
        return binding.root
    }

    private fun setCustomProfilesToRecyclerView() {
        val adapter = binding.customProfileRv.adapter as CustomProfileAdapter
        viewModel.customProfiles.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                Log.d("ITEM", it.toString())
                adapter.addProfiles(it)
            } else {
                binding.customProfileRv.visibility = View.GONE
                binding.emptyRvTitle.visibility = View.VISIBLE
            }
        }
    }

    private fun addNamePopUp() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        val li = LayoutInflater.from(activity?.applicationContext)
        val promptsView = li.inflate(R.layout.custom_profile_prompt, null)
        builder.setView(promptsView)
        val userInput = promptsView.findViewById<EditText>(R.id.custom_profile_dialog_editText)
        builder.setCancelable(true)

        builder.setPositiveButton("Add") { _, _ ->
            viewModel.addCustomProfile(userInput.text.toString())
        }
        builder.setNegativeButton("Cancel") { _, _ ->
            Log.d("Cancel", "WORKS")
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
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
}