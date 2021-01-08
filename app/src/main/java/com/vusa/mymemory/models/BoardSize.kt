package com.vusa.mymemory.models

//dynamically size board based on difficulty level selected via enum
enum class BoardSize(val numCards: Int) {
    EASY(numCards = 8),
    MEDIUM(numCards = 18),
    HARD(numCards = 24);

    fun getWidth(): Int {
        //when is like a switch statement on this which is the  current enum value
        return when (this) {
            EASY -> 2
            MEDIUM -> 3
            HARD -> 4
        }
    }

    fun getHeight() : Int {
        return numCards / getWidth()
    }

    fun getNumPairs() : Int {
        return numCards / 2
    }
}