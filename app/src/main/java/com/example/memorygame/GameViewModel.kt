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
        var uniqueIdCounter = 0
        val newCards = iconList.flatMap { iconId ->
            listOf(MemoryCard(iconId, uniqueIdCounter++), MemoryCard(iconId, uniqueIdCounter++))
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

        val workingCards = _cards.value?.map { it.copy() } ?: return

        // 2. Modifique a cópia
        workingCards[position].isFlipped = true

        if (firstSelectedCardIndex == null) {
            // --- PRIMEIRO CLIQUE ---
            firstSelectedCardIndex = position

            // 3. Poste a lista modificada
            _cards.value = workingCards // 1ª atualização (funciona)

        } else {
            // --- SEGUNDO CLIQUE ---
            isBoardLocked = true

            // 4. Poste a lista mostrando a 2ª carta virada
            _cards.value = workingCards // 2ª atualização (funciona)

            val firstCard = workingCards[firstSelectedCardIndex!!]
            val secondCard = workingCards[position]

            if (firstCard.id == secondCard.id) {
                // --- DEU MATCH ---
                firstCard.isMatched = true
                secondCard.isMatched = true
                firstSelectedCardIndex = null
                isBoardLocked = false

                // 5. CORREÇÃO: Poste uma *NOVA CÓPIA* da lista
                _cards.value = workingCards.map { it.copy() } // 3ª atualização (agora funciona)

            } else {
                // --- NÃO DEU MATCH ---
                viewModelScope.launch {
                    delay(1000)

                    firstCard.isFlipped = false
                    secondCard.isFlipped = false
                    firstSelectedCardIndex = null
                    isBoardLocked = false

                    // 6. CORREÇÃO: Poste uma *NOVA CÓPIA* da lista
                    _cards.value = workingCards.map { it.copy() } // 3ª atualização (agora funciona)
                }
            }
        }
    }
}