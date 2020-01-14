package com.example.helps

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class Adapter internal  constructor(context: Context):
    RecyclerView.Adapter<Adapter.QuesViewHolder>()
{
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var shows = emptyList<Questions>() //cached copy of help

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuesViewHolder {
        val itemView = inflater.inflate(R.layout.help_view_layout, parent, false)

        return QuesViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: QuesViewHolder, position: Int) {
        val current = shows[position]
        holder.questionTextView.text = current.questionName
        holder.replyTextView.text = current.replyName
    }

    override fun getItemCount() = shows.size

    inner class QuesViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val questionTextView: TextView = itemView.findViewById(R.id.questions)
        val replyTextView: TextView = itemView.findViewById(R.id.replys)
    }

    internal fun setPost(shows: List<Questions>)
    {
        this.shows = shows
        notifyDataSetChanged()
    }

}