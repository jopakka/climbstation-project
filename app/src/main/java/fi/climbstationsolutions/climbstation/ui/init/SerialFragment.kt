package fi.climbstationsolutions.climbstation.ui.init

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import fi.climbstationsolutions.climbstation.databinding.FragmentSerialBinding
import fi.climbstationsolutions.climbstation.sharedprefs.PREF_NAME
import fi.climbstationsolutions.climbstation.sharedprefs.PreferenceHelper
import fi.climbstationsolutions.climbstation.sharedprefs.PreferenceHelper.set
import fi.climbstationsolutions.climbstation.sharedprefs.SERIAL_NO_PREF_NAME

class SerialFragment : Fragment() {

    private lateinit var binding: FragmentSerialBinding
    private val viewModel: InitViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSerialBinding.inflate(layoutInflater)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        initUI()

        return binding.root
    }

    /**
     * Initializes UI elements and observers
     */
    private fun initUI() {
        binding.apply {
            etSerialNo.addTextChangedListener(textOnChange)
            etSerialNo.setOnEditorActionListener(keyboardActionListener)
            btnContinue.setOnClickListener(btnClickListener)
        }
        viewModel.loading.observe(viewLifecycleOwner, loadingObserver)
    }

    private val keyboardActionListener = TextView.OnEditorActionListener { textView, actionId, _ ->
        var handled = false

        if(actionId == EditorInfo.IME_ACTION_DONE) {
            keyboardStatus(false, textView)
            handled = true
        }

        return@OnEditorActionListener handled
    }

    /**
     * ClickListener for button
     */
    private val btnClickListener = View.OnClickListener {
        testSerial()
    }

    /**
     * Tests is given serial number correct
     */
    private fun testSerial() {
        editableStatus(false)
        keyboardStatus(false, binding.etSerialNo)
        viewModel.testSerialNo(binding.etSerialNo.text.toString())
    }

    private fun editableStatus(value: Boolean) {
        binding.apply {
            btnContinue.isClickable = value
            etSerialNo.isEnabled = value
        }
    }

    private fun keyboardStatus(show: Boolean, view: View) {
        val con = context ?: return
        val imm = con.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if(show) {
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        } else {
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private val loadingObserver = Observer<Boolean> {
        if(it) return@Observer

        if(viewModel.serial != null) {
//            Log.d(TAG, "Serial: ${viewModel.serial}")
            context?.let { con ->
                PreferenceHelper.customPrefs(con, PREF_NAME)[SERIAL_NO_PREF_NAME] = viewModel.serial
                val direction = SerialFragmentDirections.actionGlobalMainActivity()
                findNavController().navigate(direction)
                activity?.finish()

            }
        } else {
            editableStatus(true)
            binding.etSerialNo.requestFocus()
            keyboardStatus(true, binding.etSerialNo)
        }
    }

    private val textOnChange = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            // Nothing to do here
        }

        override fun onTextChanged(cs: CharSequence, p1: Int, p2: Int, p3: Int) {
            binding.btnContinue.isEnabled = cs.trim().isNotEmpty()
        }

        override fun afterTextChanged(p0: Editable?) {
            // Nothing to do here
        }

    }
}