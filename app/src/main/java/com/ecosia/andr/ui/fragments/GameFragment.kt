package com.ecosia.andr.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.ecosia.andr.R
import com.ecosia.andr.databinding.GameFragmentLayoutBinding

class GameFragment : Fragment() {
    private lateinit var binding: GameFragmentLayoutBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.game_fragment_layout, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        flip()
    }

    private fun flip() {
        binding.gameBtn.setOnClickListener {

            when ((1..2).random()) {
                1 -> {
                    coinFlip(R.drawable.ic_coin_thumbs__up)
                }
                else -> {
                    coinFlip(R.drawable.ic_coin_thumbs_down)
                }
            }
        }
    }

    private fun coinFlip(imageId: Int) {
        binding.apply {
            coinIv.animate().apply {
                duration = 1000
                rotationXBy(1080f)
            }.withEndAction {
                coinIv.setImageResource(imageId)
            }.start()
        }
    }
}