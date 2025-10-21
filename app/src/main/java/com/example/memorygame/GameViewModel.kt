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
                MemoryCard(iconId, uniqueIdCounter++),
                MemoryCard(iconId, uniqueIdCounter++)
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
            // ===== PRIMEIRO CLIQUE =====
            firstSelectedCardIndex = position

            // Cria nova lista com a primeira carta virada
            _cards.value = currentCards.mapIndexed { index, card ->
                if (index == position) {
                    card.copy(isFlipped = true)
                } else {
                    card
                }
            }

        } else {
            // ===== SEGUNDO CLIQUE =====
            val firstPos = firstSelectedCardIndex!!

            // Previne clicar na mesma carta
            if (firstPos == position) return

            isBoardLocked = true

            // Mostra a segunda carta virada
            val cardsWithBothFlipped = currentCards.mapIndexed { index, card ->
                if (index == position || index == firstPos) {
                    card.copy(isFlipped = true)
                } else {
                    card
                }
            }
            _cards.value = cardsWithBothFlipped

            // Verifica se deu match
            val firstCard = cardsWithBothFlipped[firstPos]
            val secondCard = cardsWithBothFlipped[position]

            if (firstCard.id == secondCard.id) {
                // ===== MATCH! =====
                _cards.value = cardsWithBothFlipped.mapIndexed { index, card ->
                    if (index == firstPos || index == position) {
                        card.copy(isMatched = true)
                    } else {
                        card
                    }
                }

                firstSelectedCardIndex = null
                isBoardLocked = false

            } else {
                // ===== NÃO DEU MATCH =====
                viewModelScope.launch {
                    delay(1000)

                    // Desvira as duas cartas
                    _cards.value = cardsWithBothFlipped.mapIndexed { index, card ->
                        if (index == firstPos || index == position) {
                            card.copy(isFlipped = false)
                        } else {
                            card
                        }
                    }

                    firstSelectedCardIndex = null
                    isBoardLocked = false
                }
            }
        }
    }
}