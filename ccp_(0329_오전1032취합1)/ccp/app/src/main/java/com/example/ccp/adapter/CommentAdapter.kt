package com.example.ccp.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.ccp.R
import com.example.ccp.model.CommentDTO
import com.example.ccp.util.RetrofitClient.apiService
import com.example.ccp.util.RetrofitClient.commentService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
        private val linkDeleteComment: TextView = itemView.findViewById(R.id.linkDeleteComment)

        fun bind(comment : CommentDTO){
            commentWriterTextView.text = comment.writerUsername
            commentContentTextView.text = comment.content
            commentTimeTextView.text = comment.regdate.toString()

            // 댓글 삭제 버튼 클릭 이벤트 처리
            linkDeleteComment.setOnClickListener{
                Log.d("deleteComment","댓글 삭제 버튼")
                deleteCommentToServer(comment.cnum)
            }
        }
    }

    // 데이터 변경 시 호출하여 RecyclerView 갱신
    fun updateData(newCommentList: List<CommentDTO>) {
        commentList = newCommentList
        notifyDataSetChanged()
    }

    private fun deleteCommentToServer(cnum : Long?){
        cnum?.let { cnumNotNull ->
            commentService.deleteComments(cnumNotNull.toInt()).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(context, "댓글이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                        // 서버에서 성공적으로 삭제됐을 때 로컬 데이터 갱신
                        val newList = commentList.toMutableList()
                        newList.removeIf { it.cnum == cnumNotNull }
                        updateData(newList)
                    } else {
                        Toast.makeText(context, "댓글 삭제에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.e("CommentAdapter", "댓글 삭제 실패", t)
                    Toast.makeText(context, "네트워크 오류로 댓글 삭제에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}