package com.example.helpwithpicturesapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ImageAdapter(
    val urls: List<String>
): RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val recyclerViewImageView = itemView.findViewById<ImageView>(R.id.imageViewUpload)
        val uploadButton = itemView.findViewById<Button>(R.id.uploadButton)
        val downloadButton = itemView.findViewById<Button>(R.id.downloadButton)
        val deleteButton = itemView.findViewById<Button>(R.id.deleteButton)

        val uploadImageView = itemView.findViewById<ImageView>(R.id.uploadImageView)

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_image,parent,false)
        )
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
       val url = urls[position]

        Glide.with(holder.itemView).load(url).into(holder.recyclerViewImageView)
        holder.uploadButton
        holder.downloadButton
        holder.deleteButton
        holder.uploadImageView
    }

    override fun getItemCount(): Int {
        return urls.size
    }
}