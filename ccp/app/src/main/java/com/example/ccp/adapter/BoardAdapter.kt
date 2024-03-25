// BoardAdapter.kt
package com.example.ccp.adapter

import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ccp.model.BoardDTO
import com.example.ccp.databinding.ItemBoardBinding
import com.squareup.picasso.Picasso


class BoardAdapter(private var boards: List<BoardDTO>) : RecyclerView.Adapter<BoardAdapter.BoardViewHolder>() {

    inner class BoardViewHolder(private val binding: ItemBoardBinding) : RecyclerView.ViewHolder(binding.root) {
        // ViewHolder 내부에 각 아이템의 View를 바인딩하는 함수
        fun bind(board: BoardDTO) {
            binding.tvTitle.text = board.title

            if (board.content != null) {
                binding.tvContent.text = board.content
            } else {
                binding.tvContent.text = "내용 없음"
            }
            if (!board.imageUrl.isNullOrEmpty()) {
                Picasso.get()
                    .load(board.imageUrl)
                    .into(binding.imageView)
            }

            Log.d("BoardAdapter", "Title: ${board.title}, Content: ${board.content}")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardViewHolder {
        // ViewHolder 생성 시 호출되는 함수
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemBoardBinding.inflate(layoutInflater, parent, false)
        return BoardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BoardViewHolder, position: Int) {
        // ViewHolder가 RecyclerView에 바인딩될 때 호출되는 함수
        val board = boards[position]
        holder.bind(board)
    }

    override fun getItemCount(): Int {
        // RecyclerView에 표시할 아이템의 개수를 반환하는 함수
        return boards.size
    }

    fun setData(newBoards: List<BoardDTO>) {
        // RecyclerView에 표시할 데이터를 설정하는 함수
        boards = newBoards
        notifyDataSetChanged() // 데이터 변경을 알리고 RecyclerView를 갱신
    }

    fun setBoardImage(board: BoardDTO, bitmap: Bitmap) {
        val position = boards.indexOf(board)
        if (position != -1) {
            boards[position].bitmap = bitmap // 해당 보드에 비트맵 이미지 설정
            notifyItemChanged(position) // 해당 위치의 아이템을 갱신하여 이미지를 표시
        }
    }
}
