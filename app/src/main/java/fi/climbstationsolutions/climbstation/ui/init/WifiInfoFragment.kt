package fi.climbstationsolutions.climbstation.ui.init

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import fi.climbstationsolutions.climbstation.databinding.FragmentWifiInfoBinding

class WifiInfoFragment : Fragment(), View.OnClickListener {
    private lateinit var binding: FragmentWifiInfoBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWifiInfoBinding.inflate(layoutInflater)

        initUI()

        return binding.root
    }

    private fun initUI() {
        binding.apply {
            btnNext.setOnClickListener(this@WifiInfoFragment)
            btnSkip.setOnClickListener(this@WifiInfoFragment)
        }
    }

    override fun onClick(view: View?) {
        when(view) {
            binding.btnNext -> nextBtnAction()
            binding.btnSkip -> skipBtnAction()
        }
    }

    private fun nextBtnAction() {
        val direction = WifiInfoFragmentDirections.actionWifiInfoFragmentToSerialFragment()
        findNavController().navigate(direction)
    }

    private fun skipBtnAction() {
        activity?.finish()
    }
}