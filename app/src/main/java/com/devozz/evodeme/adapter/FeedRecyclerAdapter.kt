package com.devozz.evodeme.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.devozz.evodeme.R
import com.devozz.evodeme.util.loadImage

class FeedRecyclerAdapter(
    private val userEmailArray: ArrayList<String>,
    private val userCommentArray : ArrayList<String>,
    private val userImageArray : ArrayList<String>
) : RecyclerView.Adapter<FeedRecyclerAdapter.PostHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        return PostHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_row,parent,false)
        )
    }

    override fun getItemCount(): Int = userEmailArray.size

    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        holder.apply {
            recyclerEmailText?.text = userEmailArray[position]
            recyclerCommentText?.text = userCommentArray[position]
            recyclerImageView?.loadImage(url = userImageArray[position])
        }
    }

    class PostHolder(view : View) : RecyclerView.ViewHolder(view) {
        var recyclerEmailText : TextView? = null
        var recyclerCommentText : TextView? = null
        var recyclerImageView : ImageView? = null
        init {
            view.run {
                recyclerEmailText = findViewById(R.id.recyclerEmailText)
                recyclerCommentText = findViewById(R.id.recyclerCommentText)
                recyclerImageView = findViewById(R.id.recyclerImageView)
            }
        }
    }
}