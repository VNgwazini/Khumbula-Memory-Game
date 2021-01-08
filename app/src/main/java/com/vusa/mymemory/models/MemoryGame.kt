package com.vusa.mymemory.models

import com.vusa.mymemory.utils.DEFAULT_ICONS

//class that creates a game and manages the state of that game
class MemoryGame(private val boardSize: BoardSize) {


    //depending on the board size, the list of chosen images changes
    val cards: List<MemoryCard>
    var numPairsFound = 0;

    private var numCardFlips = 0
    private var indexOfSingleSelectedCard: Int? = null

    //initializer block
    init {
        //grab the desired number of icons, after randomizing
        val chosenImages : List<Int> =  DEFAULT_ICONS.shuffled().take(boardSize.getNumPairs())
        //make sure we have two of each image selected
        val randomizedImages : List<Int> = (chosenImages + chosenImages).shuffled()
        //map each randomized image to a memory card and store them into a list
        cards = randomizedImages.map{ MemoryCard(it) }
    }

    fun flipCard(position: Int) : Boolean {
        numCardFlips++
        val card : MemoryCard = cards[position]

        /*There are 3 cases we need to consider on a card flip
        * 0 cards previously flipped over -> restore previously selected cards to default state + flip selected card
        * 1 cards previously flipped over -> flip selected card + check if match
        * 2 cards previously flipped over -> restore previously selected cards to default state + flip selected card
        * */
        var foundMatch = false
        if (indexOfSingleSelectedCard == null) {
            //0 or 2 cards flipped
            restoreCards()
            indexOfSingleSelectedCard = position
        }
        else {
            //only one card is selected
                //!! mean don't yell at me for this error mr. compiler
            foundMatch = checkForMatch(indexOfSingleSelectedCard!!, position)
            indexOfSingleSelectedCard = null
        }

        //opposite of whatever it was before
        card.isFaceUp = !card.isFaceUp
        return foundMatch
    }

    private fun checkForMatch(position1: Int, position2: Int): Boolean {
        if (cards[position1].identifer != cards[position2].identifer){
            return false
        }
        cards[position1].isMatched = true
        cards[position2].isMatched = true
        numPairsFound++
        return true

    }

    private fun restoreCards() {
        for (card : MemoryCard in cards) {
            if(!card.isMatched){
                card.isFaceUp = false
            }
        }
    }

    fun haveWonGame(): Boolean {
        //you've won if you have found all pairs
        return numPairsFound == boardSize.getNumPairs()
    }

    fun isCardFaceUp(position: Int): Boolean {
        return cards[position].isFaceUp
    }

    fun getNumMoves(): Int {
        //a complete move is two cards being flipped over
        return numCardFlips / 2
    }
}