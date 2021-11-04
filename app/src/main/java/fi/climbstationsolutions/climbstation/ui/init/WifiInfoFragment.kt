package fi.climbstationsolutions.climbstation.ui.init

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import fi.climbstationsolutions.climbstation.databinding.FragmentWifiInfoBinding

class WifiInfoFragment : Fragment() {
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
        binding.btnNext.setOnClickListener(nextBtnClickListener)
    }

    private val nextBtnClickListener = View.OnClickListener {
        val direction = WifiInfoFragmentDirections.actionWifiInfoFragmentToSerialFragment()
        findNavController().navigate(direction)
    }
}