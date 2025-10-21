package com.example.memorygame

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GameViewModel : ViewModel() {
    private var firstSelectedCardIndex: Int? = null
    private var isBoardLocked = false
    private val _cards = MutableLiveData<List<MemoryCard>>()
    val cards: LiveData<List<MemoryCard>> = _cards

    private val iconList  = mutableListOf(R.drawable.ic_bus, R.drawable.ic_ice_flake, R.drawable.ic_sun,
        R.drawable.ic_smile_cam)

    init {
        createNewGame()
    }

    fun onRestartGameClicked(){
        createNewGame()
    }

    private fun createNewGame() {
        val newCards = iconList.flatMap { iconId ->
            listOf(MemoryCard(iconId))
        }

        val shuffleCards = newCards.shuffled()

        firstSelectedCardIndex = null
        isBoardLocked = false

        _cards.value = shuffleCards
    }
}