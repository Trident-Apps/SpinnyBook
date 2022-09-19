package com.ecosia.andr.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ecosia.andr.R
import com.ecosia.andr.databinding.StartGameFragmentLayoutBinding

class StartGameFragment : Fragment() {
    private lateinit var binding: StartGameFragmentLayoutBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.start_game_fragment_layout, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            startGameButton.setOnClickListener {
                findNavController().navigate(R.id.gameFragment)
            }
        }
    }
}