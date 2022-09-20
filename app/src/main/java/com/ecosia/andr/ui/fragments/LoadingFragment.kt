package com.ecosia.andr.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.ecosia.andr.R
import com.ecosia.andr.databinding.LoadingFragmentLayoutBinding
import com.ecosia.andr.ui.viewmodel.BookVM
import com.ecosia.andr.ui.viewmodel.BookVMFactory
import com.ecosia.andr.util.Checker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoadingFragment : Fragment() {
    private lateinit var binding: LoadingFragmentLayoutBinding
    private lateinit var viewModel: BookVM
    private val checker = Checker()
    private var savedUrl: String = ""


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.loading_fragment_layout, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {}

            })

        super.onViewCreated(view, savedInstanceState)
        val sharedPref = requireActivity().getSharedPreferences(
            requireActivity().getString(R.string.shared_pref_name),
            Context.MODE_PRIVATE
        )

        val viewModelFactory = BookVMFactory(requireActivity().application)
        viewModel = ViewModelProvider(this, viewModelFactory)[BookVM::class.java]


        if (checker.isDeviceSecured(requireActivity())) {
            findNavController().navigate(R.id.startGameFragment)
        } else {
            savedUrl = sharedPref.getString("savedUrl", "null").toString()
            if (savedUrl == "null") {
                lifecycleScope.launch(Dispatchers.IO) {
                    viewModel.getDeepLink(requireActivity())
                }
                lifecycleScope.launch(Dispatchers.Main) {
                    viewModel.urlLiveData.observe(viewLifecycleOwner) {
                        startWeb(it)
                    }
                }
            } else {
                startWeb(savedUrl)
            }
        }


    }

    private fun startWeb(url: String) {
        val bundle = bundleOf("url" to url)
        findNavController().navigate(R.id.webFragment, bundle)
    }
}