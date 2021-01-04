package com.vusa.mymemory

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.recyclerview.widget.RecyclerView
//subclass of recycler view
class MemoryBoardAdapter(private val context: Context, private val numPieces: Int) :
        RecyclerView.Adapter<MemoryBoardAdapter.ViewHolder>() {

            //how to create one view of our recycler view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view : View =  LayoutInflater.from(context).inflate(R.layout.memory_card, parent, false)
        return ViewHolder(view)
    }

//taking the data at this section and binding it to the view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }
//how many elements are in our recylcer view
    override fun getItemCount() = numPieces

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int) {
            //no operation
        }
    }

}
