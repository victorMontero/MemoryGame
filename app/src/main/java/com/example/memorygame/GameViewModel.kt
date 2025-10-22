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

    private val iconList = listOf(
        R.drawable.ic_bus,
        R.drawable.ic_ice_flake,
        R.drawable.ic_sun,
        R.drawable.ic_smile_cam
    )

    init {
        createNewGame()
    }

    fun onRestartGameClicked() {
        createNewGame()
    }

    private fun createNewGame() {
        var uniqueIdCounter = 0
        val newCards = iconList.flatMap { iconId ->
            listOf(
                MemoryCard(id = iconId, uniqueId = uniqueIdCounter++),
                MemoryCard(id = iconId, uniqueId = uniqueIdCounter++)
            )
        }.shuffled()

        firstSelectedCardIndex = null
        isBoardLocked = false
        _cards.value = newCards
    }

    fun onCardClicked(position: Int) {
        if (isBoardLocked) return
        val currentCards = _cards.value ?: return
        val clickedCard = currentCards.getOrNull(position) ?: return
        if (clickedCard.isFlipped || clickedCard.isMatched) return

        if (firstSelectedCardIndex == null) {
            handleFirstClick(position)
        } else {
            handleSecondClick(position)
        }
    }

    private fun handleFirstClick(position: Int) {
        firstSelectedCardIndex = position
        _cards.value = _cards.value!!.mapIndexed { index, card ->
            if (index == position) card.copy(isFlipped = true) else card
        }
    }

    private fun handleSecondClick(position: Int) {
        val firstPos = firstSelectedCardIndex!!
        if (firstPos == position) return

        isBoardLocked = true
        val cardsWithBothFlipped = _cards.value!!.mapIndexed { index, card ->
            if (index == position || index == firstPos) card.copy(isFlipped = true) else card
        }
        _cards.value = cardsWithBothFlipped

        checkForMatch(firstPos, position)
    }

    private fun checkForMatch(firstPos: Int, secondPos: Int) {
        val cards = _cards.value!!
        val firstCard = cards[firstPos]
        val secondCard = cards[secondPos]

        if (firstCard.id == secondCard.id) {
            // Deu par
            _cards.value = cards.mapIndexed { index, card ->
                if (index == firstPos || index == secondPos) card.copy(isMatched = true) else card
            }
            firstSelectedCardIndex = null
            isBoardLocked = false
        } else {
            // Não deu par
            viewModelScope.launch {
                delay(1000)
                _cards.value = cards.mapIndexed { index, card ->
                    if (index == firstPos || index == secondPos) card.copy(isFlipped = false) else card
                }
                firstSelectedCardIndex = null
                isBoardLocked = false
            }
        }
    }}