package com.raitech.statussaver.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.raitech.statussaver.R
import com.raitech.statussaver.activities.home.ImageViewActivity
import com.raitech.statussaver.activities.home.VideoViewActivity
import java.io.File

class FileAdapter(private val context: Context,private val list:ArrayList<File>):Adapter<FileAdapter.FileViewHolder>()
{


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        return FileViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_layout,null,false))
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        val currentItem=list[position]

        if (currentItem.toURI().toString().endsWith(".mp4")){
            holder.imgPlay.visibility=View.VISIBLE
        }else{
            holder.imgPlay.visibility=View.GONE
        }
        Glide.with(context.applicationContext).load(currentItem.toURI().toString()).into(holder.imgView)
        holder.btnDownload.setImageResource(R.drawable.trash)
        holder.btnDownload.setOnClickListener {
            try {
                val file: File = File(currentItem.path)
                if (file.exists()){
                 //   val file2 = File(file.absolutePath)
                    if (file.delete()){
                        Toast.makeText(context,"File Deleted",Toast.LENGTH_SHORT).show()
                        list.removeAt(position)
                        notifyDataSetChanged()
                    }else{
                        Toast.makeText(context,"Something went wrong",Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context,""+e.message,Toast.LENGTH_SHORT).show()
            }
        }


        holder.layout.setOnClickListener {
            if (currentItem.toURI().toString().endsWith(".mp4")){
                val intent= Intent(context, VideoViewActivity::class.java)
                intent.putExtra("fileUri",currentItem.toURI().toString())
                intent.putExtra("isShareable",false)
                context.startActivity(intent)
            }else{
                val intent= Intent(context, ImageViewActivity::class.java)
                intent.putExtra("fileUri",currentItem.toURI().toString())
                intent.putExtra("isShareable",false)
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
       return list.size
    }
    class FileViewHolder(itemView: View) : ViewHolder(itemView) {
        var imgView: ImageView
        val imgPlay: ImageView
        val btnDownload: ImageButton
        val layout: CardView
        init {

            imgView=itemView.findViewById(R.id.img)
            imgPlay=itemView.findViewById(R.id.img_play)
            btnDownload=itemView.findViewById(R.id.btn_download)
            layout=itemView.findViewById(R.id.card_layout)
        }
    }
}