package com.vusa.mymemory

import android.animation.ArgbEvaluator
import android.graphics.Color
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
import com.github.jinatonic.confetti.CommonConfetti
import com.google.android.material.snackbar.Snackbar
import com.vusa.mymemory.models.BoardSize
import com.vusa.mymemory.models.MemoryGame

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var clRoot: ConstraintLayout
    private lateinit var rvBoard: RecyclerView
    private lateinit var tvNumMoves: TextView
    private lateinit var tvNumPairs: TextView

    private lateinit var adapter: MemoryBoardAdapter
    private lateinit var memoryGame: MemoryGame


    //initialize boardSize
    private var boardSize: BoardSize = BoardSize.EASY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //references to resources i.e. textview and recycle view
        clRoot = findViewById(R.id.clRoot)
        rvBoard = findViewById(R.id.rvBoard)
        tvNumMoves = findViewById(R.id.tvNumMoves)
        tvNumPairs = findViewById(R.id.tvNumPairs)

        setupBoard()
    }

    /*
    * "Inflating" a view means taking the layout XML and parsing it to create the view
    *  and view group objects from the elements and their attributes specified within,
    *  and then adding the hierarchy of those views and view groups to the parent ViewGroup.*/
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.mi_refresh -> {
                if (memoryGame.getNumMoves() > 0 && !memoryGame.haveWonGame()) {
                    showAlertDialog(getString(R.string.quit), null) {
                        setupBoard()
                    }
                } else {
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
        val boardSizeView =
            LayoutInflater.from(this).inflate(R.layout.dialog_board_size, null)
        val radioGroupSize = boardSizeView.findViewById<RadioGroup>(R.id.radioGroup)

        //by default, check the radio button of the current size
        when (boardSize) {
            BoardSize.EASY -> radioGroupSize.check(R.id.rbEasy)
            BoardSize.MEDIUM -> radioGroupSize.check(R.id.rbMedium)
            BoardSize.HARD -> radioGroupSize.check(R.id.rbHard)
        }

        showAlertDialog(getString(R.string.choose_new_size), boardSizeView) {
            //set new board size
            boardSize = when (radioGroupSize.checkedRadioButtonId) {
                //handle selected radio button with enum switch cases
                R.id.rbEasy -> BoardSize.EASY
                R.id.rbMedium -> BoardSize.MEDIUM
                else -> BoardSize.HARD
            }
            setupBoard()
        }
    }

    private fun showAlertDialog(
        title: String,
        view: View?,
        positiveClickLister: View.OnClickListener
    ) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setView(view)
            //options with actions, negative always behaves as never mind
            .setNegativeButton(getString(R.string.cancel), null)
            //only ok needs to be giving action
            .setPositiveButton(getString(R.string.ok)) { _, _ ->
                positiveClickLister.onClick(null)
            }.show()
    }

    private fun updateGameWithFlip(position: Int) {
        //error handling
        if (memoryGame.haveWonGame()) {
            //alert user that a move is invalid
            Snackbar.make(clRoot, "", Snackbar.LENGTH_SHORT).show()
            return
        }
        if (memoryGame.isCardFaceUp(position)) {
            //alert user that a move is invalid
            Snackbar.make(clRoot, getString(R.string.invalid_move), Snackbar.LENGTH_LONG).show()
            return
        }
        if (memoryGame.flipCard(position)) {
            Log.i(TAG, getString(R.string.found_match, memoryGame.numPairsFound))
            //takes two colors and fades with progress
            val color = ArgbEvaluator().evaluate(
                memoryGame.numPairsFound.toFloat() / boardSize.getNumPairs(),
                ContextCompat.getColor(this, R.color.color_progresss_none),
                ContextCompat.getColor(this, R.color.color_progresss_full)
            ) as Int
            tvNumPairs.setTextColor(color)
            tvNumPairs.text = resources.getString(
                R.string.pairs,
                memoryGame.numPairsFound,
                boardSize.getNumPairs()
            )
            //check if user won the game
            if (memoryGame.haveWonGame()) {
                //alert user that they have won the game
                Snackbar.make(clRoot, getString(R.string.you_won), Snackbar.LENGTH_LONG).show()
                //Confetti effect on win
                CommonConfetti.rainingConfetti(
                    clRoot,
                    intArrayOf(Color.YELLOW, Color.GREEN, Color.MAGENTA)
                ).oneShot()
            }
        }

        tvNumMoves.text = resources.getString(R.string.moves, memoryGame.getNumMoves())
        //tell the memory board adapter that we flipped a card
        adapter.notifyDataSetChanged()
    }

    private fun setupBoard() {
        when (boardSize) {
            BoardSize.EASY -> {
                tvNumMoves.text = resources.getString(R.string.easy_moves)
                tvNumPairs.text = resources.getString(R.string.easy_pairs)
            }
            BoardSize.MEDIUM -> {
                tvNumMoves.text = resources.getString(R.string.medium_moves)
                tvNumPairs.text = resources.getString(R.string.medium_pairs)
            }
            BoardSize.HARD -> {
                tvNumMoves.text = resources.getString(R.string.hard_moves)
                tvNumPairs.text = resources.getString(R.string.hard_pairs)
            }
        }

        tvNumPairs.setTextColor(ContextCompat.getColor(this, R.color.color_progresss_none))
        memoryGame = MemoryGame(boardSize)

        //define layout to be dynamically set based on screen size
        adapter = MemoryBoardAdapter(
            this,
            boardSize,
            memoryGame.cards,
            object : MemoryBoardAdapter.CardClickListener {
                override fun onCardClicked(position: Int) {
                    updateGameWithFlip(position)
                }
            })

        rvBoard.adapter = adapter
        //makes application efficient by telling the app that the recyclerview size is constant
        rvBoard.setHasFixedSize(true)
        rvBoard.layoutManager = GridLayoutManager(this, boardSize.getWidth())
    }
}