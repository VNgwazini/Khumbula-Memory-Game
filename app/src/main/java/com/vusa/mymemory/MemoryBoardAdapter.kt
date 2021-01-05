package com.vusa.mymemory

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.min

//subclass of recycler view
class MemoryBoardAdapter(private val context: Context, private val numPieces: Int) :
        RecyclerView.Adapter<MemoryBoardAdapter.ViewHolder>() {

    //singleton where we define constants to be accessed via containing class
    companion object {
        private const val MARGIN_SIZE = 10
        private const val TAG = "MemoryBoardAdapter"
    }

            //how to create one view of our recycler view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //each view created will be half the width of the parent
        val cardWidth : Int = parent.width / 2 - (2 * MARGIN_SIZE)
        //each view created will be a quarter of the parent height
        val cardHeight : Int = parent.height / 4 - (2 * MARGIN_SIZE)
        val cardSideLength : Int = min(cardWidth, cardHeight)
        val view:View =  LayoutInflater.from(context).inflate(R.layout.memory_card, parent, false)
        //cast layout params to Margin layout Params
        val layoutParams : ViewGroup.MarginLayoutParams = view.findViewById<CardView>(R.id.cardView).layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.width = cardSideLength
        layoutParams.height = cardSideLength
        layoutParams.setMargins(MARGIN_SIZE, MARGIN_SIZE, MARGIN_SIZE, MARGIN_SIZE)
        return ViewHolder(view)
    }

    //how many elements are in our recycler view
    override fun getItemCount() = numPieces

    //taking the data at this section and binding it to the view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //grab reference to image button in card view
        private val imageButton = itemView.findViewById<ImageButton>(R.id.imageButton)
        //recognize that we clicked a specific button via its position
        fun bind(position: Int) {
            imageButton.setOnClickListener{
                Log.i(TAG, "Clicked on position $position")
            }
        }
    }

}
