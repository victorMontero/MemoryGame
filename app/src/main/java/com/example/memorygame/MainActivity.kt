package com.example.memorygame

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.memorygame.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val viewModel: GameViewModel by viewModels()

    private lateinit var gameAdapter: GameAdapter

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val recyclerView = binding.gameRv

        gameAdapter = GameAdapter { position ->
            viewModel.onCardClicked(position)
        }

        recyclerView.adapter = gameAdapter
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        viewModel.cards.observe(this) { cardList ->
            gameAdapter.submitList(cardList)
        }
    }
}