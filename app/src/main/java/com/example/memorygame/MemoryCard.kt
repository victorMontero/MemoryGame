package com.example.memorygame

data class MemoryCard(
    val id : Int,
    val uniqueId : Int,
    val isFlipped: Boolean = false,
    val isMatched: Boolean = false
) {
}