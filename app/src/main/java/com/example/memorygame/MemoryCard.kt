package com.example.memorygame

data class MemoryCard(
    val id : Int,
    var isFlipped: Boolean = false,
    var isMatched: Boolean = false
) {
}