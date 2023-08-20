package com.raitech.statussaver.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.raitech.statussaver.R
import com.raitech.statussaver.activities.home.ImageViewActivity
import com.raitech.statussaver.activities.home.VideoViewActivity
import com.raitech.statussaver.models.StatusModel

class StatusAdapter(private val context: Context,private val list: ArrayList<StatusModel>,private val clickListener:(StatusModel)->Unit):Adapter<StatusAdapter.StatusViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatusViewHolder {
       return StatusViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_layout,null,false))
    }

    override fun onBindViewHolder(holder: StatusViewHolder, position: Int) {
        val currentItem=list[position]

        if (currentItem.fileUri!!.endsWith(".mp4")){
            holder.imgPlay.visibility=View.VISIBLE
        }else{
            holder.imgPlay.visibility=View.GONE
        }
        Glide.with(context.applicationContext).load(currentItem.fileUri).into(holder.imgView)

        holder.btnDownload.setOnClickListener {

            clickListener(currentItem)

        }
        holder.layout.setOnClickListener {
            if (currentItem.fileUri!!.endsWith(".mp4")){
               val intent=Intent(context,VideoViewActivity::class.java)
                intent.putExtra("fileUri",currentItem.fileUri)
                intent.putExtra("isShareable",true)
                context.startActivity(intent)
            }else{
                val intent=Intent(context,ImageViewActivity::class.java)
                intent.putExtra("fileUri",currentItem.fileUri)
                intent.putExtra("isShareable",true)
                context.startActivity(intent)
            }
        }

    }

    override fun getItemCount(): Int {
       return list.size
    }

    class StatusViewHolder(itemView: View) : ViewHolder(itemView) {

        val imgView:ImageView
        val imgPlay:ImageView
        val btnDownload:ImageButton
        val layout:CardView
        init {

            imgView=itemView.findViewById(R.id.img)
            imgPlay=itemView.findViewById(R.id.img_play)
            btnDownload=itemView.findViewById(R.id.btn_download)
            layout=itemView.findViewById(R.id.card_layout)
        }





    }
}