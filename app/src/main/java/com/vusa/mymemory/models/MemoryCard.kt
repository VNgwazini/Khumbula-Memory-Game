package com.vusa.mymemory.models
//this class stores the card and its state in relation to the board and its match
data class MemoryCard(
        val identifier: Int,
        var isFaceUp: Boolean = false,
        var isMatched: Boolean = false
)