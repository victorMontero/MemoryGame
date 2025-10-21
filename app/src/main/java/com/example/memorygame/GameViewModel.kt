package com.example.memorygame

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
            listOf(MemoryCard(iconId), MemoryCard(iconId))
        }

        val shuffleCards = newCards.shuffled()

        firstSelectedCardIndex = null
        isBoardLocked = false

        _cards.value = shuffleCards
    }

    fun onCardClicked(position: Int){
        if (isBoardLocked) return
        val clickedCard = _cards.value?.get(position) ?: return
        if (clickedCard.isFlipped || clickedCard.isMatched) return

        val currentCards = _cards.value?.map { it.copy() } ?: return

        currentCards[position].isFlipped = true

        if(firstSelectedCardIndex == null){
            firstSelectedCardIndex = position
            _cards.value = currentCards
        } else {
            isBoardLocked = true
            _cards.value = currentCards

            val firstCard = currentCards[firstSelectedCardIndex!!]
            val secondCard = currentCards[position]

            if (firstCard.id == secondCard.id){
                firstCard.isMatched = true
                secondCard.isMatched = true
                firstSelectedCardIndex = null
                isBoardLocked = false
                _cards.value = currentCards
            } else{
                viewModelScope.launch {
                    delay(1000) // Espera 1 segundo

                    // Desvira as duas cartas
                    firstCard.isFlipped = false
                    secondCard.isFlipped = false

                    firstSelectedCardIndex = null // Reseta
                    isBoardLocked = false // Destrava
                    _cards.value = currentCards // Atualiza a UI
                }
            }
        }
    }
}