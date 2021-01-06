package com.vusa.mymemory.models

import com.vusa.mymemory.utils.DEFAULT_ICONS

//class that creates a game and manages the state of that game
class MemoryGame(private val boardSize: BoardSize) {


    //depending on the board size, the list of chosen images changes
    val cards: List<MemoryCard>
    val numPairsFound = 0;

    //initializer block
    init {
        //grab the desired number of icons, after randomizing
        val chosenImages : List<Int> =  DEFAULT_ICONS.shuffled().take(boardSize.getNumPairs())
        //make sure we have two of each image selected
        val randomizedImages : List<Int> = (chosenImages + chosenImages).shuffled()
        //map each randomized image to a memory card and store them into a list
        cards = randomizedImages.map{ MemoryCard(it) }
    }

    fun flipCard(position: Int) {
        val card : MemoryCard = cards[position]
        //opposite of whatever it was before
        card.isFaceUp = !card.isFaceUp
    }
}