package com.example.ccp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ccp.R
import com.example.ccp.model.CommentDTO

class CommentAdapter(
    private val context: Context,
    private var commentList: List<CommentDTO> // 변수명 변경
) : RecyclerView.Adapter<CommentAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val comment = commentList[position] // 변수명 변경
        holder.bind(comment)
    }

    override fun getItemCount(): Int {
        return commentList.size // 변수명 변경
    }

    inner class ViewHolder(commentView: View) : RecyclerView.ViewHolder(commentView){
        private val commentWriterTextView : TextView = commentView.findViewById(R.id.commentWriter)
        private val commentContentTextView : TextView = commentView.findViewById(R.id.commentContent)
        private val commentTimeTextView : TextView = commentView.findViewById(R.id.commentTime)

        fun bind(comment : CommentDTO){
            commentWriterTextView.text = comment.writerUsername
            commentContentTextView.text = comment.content
            commentTimeTextView.text = comment.regdate.toString()
        }
    }

    // 데이터 변경 시 호출하여 RecyclerView 갱신
    fun updateData(newCommentList: List<CommentDTO>) {
        commentList = newCommentList
        notifyDataSetChanged()
    }
}