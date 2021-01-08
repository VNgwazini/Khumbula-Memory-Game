package com.vusa.mymemory

import android.animation.ArgbEvaluator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.vusa.mymemory.models.BoardSize
import com.vusa.mymemory.models.MemoryCard
import com.vusa.mymemory.models.MemoryGame
import com.vusa.mymemory.utils.DEFAULT_ICONS
import java.text.FieldPosition

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }
    private lateinit var clRoot: ConstraintLayout
    private lateinit var  rvBoard: RecyclerView
    private lateinit var tvNumMoves: TextView
    private lateinit var tvNumPairs: TextView

    private lateinit var adapter: MemoryBoardAdapter
    private lateinit var memoryGame: MemoryGame


    //initialze boardSize
    private var boardSize: BoardSize = BoardSize.EASY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //refrences to rosources i.e. textview and recycle view
        clRoot = findViewById(R.id.clRoot)
        rvBoard = findViewById(R.id.rvBoard)
        tvNumMoves = findViewById(R.id.tvNumMoves)
        tvNumPairs = findViewById(R.id.tvNumPairs)

        setupBoard()
    }

    /*
    * "Inflating" a view means taking the layout XML and parsing it to create the view
    *  and viewgroup objects from the elements and their attributes specified within,
    *  and then adding the hierarchy of those views and viewgroups to the parent ViewGroup.*/
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.mi_refresh -> {
                if (memoryGame.getNumMoves() > 0 && !memoryGame.haveWonGame()){
                    showAlertDialog("Quit your current game?", null, View.OnClickListener {
                        setupBoard()
                    })
                }
                else {
                    //set up the game again
                    setupBoard()
                }
            }
            R.id.mi_new_size -> {
                showNewSizeDialog()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showNewSizeDialog() {
        val boardSizeView = LayoutInflater.from(this).inflate(R.layout.dialog_board_size, null)
        val radioGroupSize = boardSizeView.findViewById<RadioGroup>(R.id.radioGroup)

        //by default, check the radio button of the current size
        when (boardSize) {
            BoardSize.EASY -> radioGroupSize.check(R.id.rbEasy)
            BoardSize.MEDIUM -> radioGroupSize.check(R.id.rbMedium)
            BoardSize.HARD -> radioGroupSize.check(R.id.rbHard)
        }

        showAlertDialog("Choose new size", boardSizeView , View.OnClickListener {
            //set new board size
            boardSize = when (radioGroupSize.checkedRadioButtonId){
                //handle selected radio button with enum switch cases
                R.id.rbEasy -> BoardSize.EASY
                R.id.rbMedium -> BoardSize.MEDIUM
                else -> BoardSize.HARD
            }
            setupBoard()
        })
    }

    private fun showAlertDialog(title : String, view : View?, positiveClickLister : View.OnClickListener) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setView(view)
                //options with actions, negative always behaves as never mind
            .setNegativeButton("Cancel",null)
                //only ok needs to be giving action
            .setPositiveButton("OK"){ _,_ ->
                positiveClickLister.onClick(null)
            }.show()
    }

    private fun updateGameWithFlip(position: Int){
        //error handling
        if(memoryGame.haveWonGame())
        {
            //alert user that a move is invalid
            Snackbar.make(clRoot, "You've already won!", Snackbar.LENGTH_SHORT).show()
            return
        }
        if(memoryGame.isCardFaceUp(position))
        {
            //alert user that a move is invalid
            Snackbar.make(clRoot, "Invalid move!", Snackbar.LENGTH_LONG).show()
            return
        }
        if(memoryGame.flipCard(position))
        {
            Log.i(TAG, "Found a match! Number of pairs found: ${memoryGame.numPairsFound}")
            //takes two colors and fades with progress
            val color = ArgbEvaluator().evaluate(
                    memoryGame.numPairsFound.toFloat() / boardSize.getNumPairs(),
                    ContextCompat.getColor(this,R.color.color_progresss_none),
                    ContextCompat.getColor(this,R.color.color_progresss_full)
            ) as Int
            tvNumPairs.setTextColor(color)
            tvNumPairs.text = "Pairs: ${memoryGame.numPairsFound} / ${boardSize.getNumPairs()}"
            //check if user won the game
            if (memoryGame.haveWonGame()){
                //alert user that they have won the game
                Snackbar.make(clRoot, "You won! Congratulations.", Snackbar.LENGTH_LONG).show()
            }
        }

        tvNumMoves.text = "Moves: ${memoryGame.getNumMoves()}"
        //tell the memory board adapter that we flipped a card
        adapter.notifyDataSetChanged()
    }

    private fun setupBoard() {
        when (boardSize){
            BoardSize.EASY -> {
                tvNumMoves.text = "Easy: 4 x 2"
                tvNumPairs.text = "Pairs: 0 / 4"
            }
            BoardSize.MEDIUM -> {
                tvNumMoves.text = "Medium: 6 x 3"
                tvNumPairs.text = "Pairs: 0 / 9 "
            }
            BoardSize.HARD -> {
                tvNumMoves.text = "Hard: 6 x 12"
                tvNumPairs.text = "Pairs: 0 / 12"
            }
        }

        tvNumPairs.setTextColor(ContextCompat.getColor(this,R.color.color_progresss_none))

        //grab the desired number of icons, after randomizing
        val chosenImages : List<Int> =  DEFAULT_ICONS.shuffled().take(boardSize.getNumPairs())
        //make sure we have two of each image selected
        val randomizedImages : List<Int> = (chosenImages + chosenImages).shuffled()
        //map each randomized image to a memory card and store them into a list
        val memoryCards : List<MemoryCard> = randomizedImages.map{ MemoryCard(it) }

        memoryGame = MemoryGame(boardSize)

        //define layout to be dynamically set based on screen size
        adapter = MemoryBoardAdapter(this, boardSize, memoryGame.cards, object: MemoryBoardAdapter.CardClickListener{
            override fun onCardClicked(position: Int) {
                updateGameWithFlip(position)
            }
        })

        rvBoard.adapter = adapter
        //makes application effecient by telling the app that the recyclerview size is constant
        rvBoard.setHasFixedSize(true)
        rvBoard.layoutManager = GridLayoutManager(this, boardSize.getWidth())
    }
}